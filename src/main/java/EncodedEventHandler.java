import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;

import java.io.UnsupportedEncodingException;

public class EncodedEventHandler implements EventHandler<EncodedEvent>
{
	public void onEvent(EncodedEvent encodedEvent, long eventSequence, boolean endOfBatch)
	{
		System.out.print(encodedEvent.getValue());
	}
}