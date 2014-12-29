import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.*;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		// Specify the size of the ring buffer, must be power of 2.
		int bufferSize = 1024;

		CountDownLatch awaitCompletion = new CountDownLatch(1);

		ExecutorService encodedExecutor = Executors.newCachedThreadPool();
		EncodedEventFactory encodedEventFactory = new EncodedEventFactory();
		Disruptor<EncodedEvent> encodedDisruptor = new Disruptor<>(encodedEventFactory, bufferSize, encodedExecutor);
		encodedDisruptor.handleEventsWith(new EncodedEventHandler());
		encodedDisruptor.start();

		ExecutorService fileReaderExecutor = Executors.newCachedThreadPool();
		FileReaderEventFactory fileReaderFactory = new FileReaderEventFactory();
		Disruptor<FileReaderEvent> fileReaderDisruptor = new Disruptor<>(fileReaderFactory, bufferSize, fileReaderExecutor);

		RingBuffer<EncodedEvent> encodedRingBuffer = encodedDisruptor.getRingBuffer();
		fileReaderDisruptor.handleEventsWith(new FileReaderEventHandler(encodedRingBuffer));
		fileReaderDisruptor.start();

		RingBuffer<FileReaderEvent> fileReaderRingBuffer = fileReaderDisruptor.getRingBuffer();
		FileHandlerEventProducer producer = new FileHandlerEventProducer(fileReaderRingBuffer, awaitCompletion);
		producer.produce("src/main/resources/input.png");

		awaitCompletion.await();

		fileReaderDisruptor.shutdown();
		encodedDisruptor.shutdown();

		encodedExecutor.shutdownNow();
		fileReaderExecutor.shutdownNow();

		fileReaderExecutor.awaitTermination(1, TimeUnit.MINUTES);
		encodedExecutor.awaitTermination(1, TimeUnit.MINUTES);
	}
}