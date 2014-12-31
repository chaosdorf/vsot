import com.lmax.disruptor.RingBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class FileHandlerEventProducer
{
	private final RingBuffer<FileReaderEvent> ringBuffer;
	private final CountDownLatch awaitCompletion;

	public FileHandlerEventProducer(RingBuffer<FileReaderEvent> ringBuffer, final CountDownLatch awaitCompletion)
	{
		this.ringBuffer = ringBuffer;
		this.awaitCompletion = awaitCompletion;
	}

	public void produce(String fileName)
	{
		try (FileChannel fileChannel = FileChannel.open(Paths.get(fileName)))
		{
			ByteBuffer buffer = ByteBuffer.allocate(2);
			while (fileChannel.read(buffer) > 0)
			{
				buffer.flip();
				boolean isPadded = (buffer.limit() < 2);
				int char1 = buffer.get();
				int char2 = (isPadded) ? ' ' : buffer.get();

				long sequence = ringBuffer.next();
				try
				{
					FileReaderEvent fileReaderEvent = ringBuffer.get(sequence);
					fileReaderEvent.setChar1(char1);
					fileReaderEvent.setChar2(char2);
					fileReaderEvent.setPadded(isPadded);
				}
				finally
				{
					ringBuffer.publish(sequence);
				}

				buffer.clear();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		awaitCompletion.countDown();
	}
}