import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		// Specify the size of the ring buffer, must be power of 2.
		int bufferSize = 8;

		Executor encodedExecutor = Executors.newCachedThreadPool();
		EncodedEventFactory encodedEventFactory = new EncodedEventFactory();
		Disruptor<EncodedEvent> encodedDisruptor = new Disruptor<>(encodedEventFactory, bufferSize, encodedExecutor);
		encodedDisruptor.handleEventsWith(new EncodedEventHandler());
		encodedDisruptor.start();

		Executor fileReaderExecutor = Executors.newCachedThreadPool();
		FileReaderEventFactory fileReaderFactory = new FileReaderEventFactory();
		Disruptor<FileReaderEvent> fileReaderDisruptor = new Disruptor<>(fileReaderFactory, bufferSize, fileReaderExecutor);

		RingBuffer<EncodedEvent> encodedRingBuffer = encodedDisruptor.getRingBuffer();
		fileReaderDisruptor.handleEventsWith(new FileReaderEventHandler(encodedRingBuffer));
		fileReaderDisruptor.start();

		RingBuffer<FileReaderEvent> fileReaderRingBuffer = fileReaderDisruptor.getRingBuffer();
		FileHandlerEventProducer producer = new FileHandlerEventProducer(fileReaderRingBuffer);
		producer.produce("src/main/resources/input.png");

		TimeUnit.SECONDS.sleep(10);

		fileReaderDisruptor.shutdown();
		encodedDisruptor.shutdown();

		System.exit(0);
	}
}