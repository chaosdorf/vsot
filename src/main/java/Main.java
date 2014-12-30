import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import util.FileUtils;
import util.TranscoderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main
{
	public static final String BASE_DIR = "src/main/resources/";
	public static final String OUTPUT_FILE = BASE_DIR + "output.txt";

	public static void main(String[] args) throws Exception
	{
		final int bufferSize = 1024;
		final String suffix = "png";

		final String inputFileName = BASE_DIR + "input." + suffix;

		long start = System.nanoTime();

		FileUtils.delete(OUTPUT_FILE);

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
		producer.produce(inputFileName);

		awaitCompletion.await();

		fileReaderDisruptor.shutdown();
		encodedDisruptor.shutdown();

		encodedExecutor.shutdownNow();
		fileReaderExecutor.shutdownNow();

		fileReaderExecutor.awaitTermination(1, TimeUnit.MINUTES);
		encodedExecutor.awaitTermination(1, TimeUnit.MINUTES);

		EncodedEventHandler.shutdown();

		System.out.println(String.format("Total time: %d ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)));

		final byte[] encodedLoad = FileUtils.load(OUTPUT_FILE);
		final byte[] decoded = TranscoderUtils.decode(encodedLoad);

		final String decodedFileName = BASE_DIR + "decoded." + suffix;
		FileUtils.delete(decodedFileName);
		FileUtils.save(decodedFileName, decoded);

		final byte[] original = FileUtils.load(inputFileName);
		TranscoderUtils.compareResults(original, decoded, 10);
	}
}