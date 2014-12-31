import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import util.Utf8;

import java.io.UnsupportedEncodingException;

public class FileReaderEventHandler implements EventHandler<FileReaderEvent>
{
	private final byte[] bytes = new byte[3];

	private final RingBuffer<EncodedEvent> ringBuffer;

	public FileReaderEventHandler(final RingBuffer<EncodedEvent> ringBuffer)
	{
		this.ringBuffer = ringBuffer;
	}

	public void onEvent(FileReaderEvent fileReaderEvent, long eventSequence, boolean endOfBatch) throws UnsupportedEncodingException
	{
		byte byte1 = (byte) fileReaderEvent.getChar1();
		byte byte2 = (byte) fileReaderEvent.getChar2();
		boolean isPadded = fileReaderEvent.isPadded();

		String prefix = "";
		convertToUtf8(byte1, byte2);
		if (!Utf8.isValidUtf8(bytes))
		{
			prefix += "!";
			convertToUtf8(~byte1 & 0xff, ~byte2 & 0xff);
		}
		if (!Utf8.isValidUtf8(bytes))
		{
			throw new IllegalArgumentException();
		}
		if (isPadded)
		{
			prefix += "-";
		}

		long sequence = ringBuffer.next();
		try
		{
			EncodedEvent encodedEvent = ringBuffer.get(sequence);
			encodedEvent.setValue(prefix + new String(bytes, "utf-8"));
		}
		finally
		{
			ringBuffer.publish(sequence);
		}
	}

	public void convertToUtf8(int byte1, int byte2)
	{
		bytes[0] = (byte) (0b1110_0000 | ((byte1 & 0b1111_0000) >>> 4));
		bytes[1] = (byte) (0b1000_0000 | ((byte1 & 0b0000_1111) << 2) | ((byte2 & 0b1100_0000) >>> 6));
		bytes[2] = (byte) (0b1000_0000 | ((byte2 & 0b0011_1111)));
	}
}