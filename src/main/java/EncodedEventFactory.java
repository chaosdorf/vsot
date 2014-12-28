import com.lmax.disruptor.EventFactory;

public class EncodedEventFactory implements EventFactory<EncodedEvent>
{
	public EncodedEvent newInstance()
	{
		return new EncodedEvent();
	}
}