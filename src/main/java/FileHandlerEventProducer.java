import com.lmax.disruptor.RingBuffer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileHandlerEventProducer
{
	private final RingBuffer<FileReaderEvent> ringBuffer;

	public FileHandlerEventProducer(RingBuffer<FileReaderEvent> ringBuffer)
	{
		this.ringBuffer = ringBuffer;
	}

	public void produce(String fileName)
	{
		try (RandomAccessFile aFile = new RandomAccessFile(fileName, "r"))
		{
			try (FileChannel inChannel = aFile.getChannel())
			{
				ByteBuffer buffer = ByteBuffer.allocate(2);
				while (inChannel.read(buffer) > 0)
				{
					buffer.flip();
					int char1 = buffer.get();
					int char2 = (buffer.limit() > 1) ? buffer.get() : ' ';

					long sequence = ringBuffer.next();
					try
					{
						FileReaderEvent fileReaderEvent = ringBuffer.get(sequence);
						fileReaderEvent.setChar1(char1);
						fileReaderEvent.setChar2(char2);
					}
					finally
					{
						ringBuffer.publish(sequence);
					}

					buffer.clear();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}