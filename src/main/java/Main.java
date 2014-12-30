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
	public static final int BUFFER_SIZE = 4096;

	public static final String FILE_SUFFIX = "webm";

	public static final String BASE_DIR = "src/main/resources/";
	public static final String INPUT_FILE = BASE_DIR + "input." + FILE_SUFFIX;
	public static final String OUTPUT_FILE = BASE_DIR + "output.txt";

	public static void main(String[] args) throws Exception
	{
		long start = System.nanoTime();

		CountDownLatch awaitCompletion = new CountDownLatch(1);
		FileUtils.delete(OUTPUT_FILE);

		ExecutorService encodedExecutor = Executors.newCachedThreadPool();
		EncodedEventFactory encodedEventFactory = new EncodedEventFactory();
		Disruptor<EncodedEvent> encodedDisruptor = new Disruptor<>(encodedEventFactory, BUFFER_SIZE, encodedExecutor);
		encodedDisruptor.handleEventsWith(new EncodedEventHandler());
		encodedDisruptor.start();

		ExecutorService fileReaderExecutor = Executors.newCachedThreadPool();
		FileReaderEventFactory fileReaderFactory = new FileReaderEventFactory();
		Disruptor<FileReaderEvent> fileReaderDisruptor = new Disruptor<>(fileReaderFactory, BUFFER_SIZE, fileReaderExecutor);

		RingBuffer<EncodedEvent> encodedRingBuffer = encodedDisruptor.getRingBuffer();
		fileReaderDisruptor.handleEventsWith(new FileReaderEventHandler(encodedRingBuffer));
		fileReaderDisruptor.start();

		RingBuffer<FileReaderEvent> fileReaderRingBuffer = fileReaderDisruptor.getRingBuffer();
		FileHandlerEventProducer producer = new FileHandlerEventProducer(fileReaderRingBuffer, awaitCompletion);
		producer.produce(INPUT_FILE);

		awaitCompletion.await();

		fileReaderDisruptor.shutdown();
		encodedDisruptor.shutdown();

		encodedExecutor.shutdownNow();
		fileReaderExecutor.shutdownNow();

		fileReaderExecutor.awaitTermination(1, TimeUnit.MINUTES);
		encodedExecutor.awaitTermination(1, TimeUnit.MINUTES);

		EncodedEventHandler.shutdown();

		long encodingDone = System.nanoTime();
		System.out.println(String.format("Encoding done in %d ms", TimeUnit.NANOSECONDS.toMillis(encodingDone - start)));

		byte[] encodedLoad = FileUtils.load(OUTPUT_FILE);
		byte[] decoded = TranscoderUtils.decode(encodedLoad);

		String decodedFileName = BASE_DIR + "decoded." + FILE_SUFFIX;
		FileUtils.delete(decodedFileName);
		FileUtils.save(decodedFileName, decoded);

		long decodingDone = System.nanoTime();
		System.out.println(String.format("Decoding done in %d ms", TimeUnit.NANOSECONDS.toMillis(decodingDone - encodingDone)));

		byte[] original = FileUtils.load(INPUT_FILE);
		TranscoderUtils.compareResults(original, decoded, 10);

		long comparingDone = System.nanoTime();
		System.out.println(String.format("Comparing done in %d ms", TimeUnit.NANOSECONDS.toMillis(comparingDone - decodingDone)));

		long end = System.nanoTime();
		System.out.println(String.format("Total runtime: %d ms", TimeUnit.NANOSECONDS.toMillis(end - start)));
	}
}