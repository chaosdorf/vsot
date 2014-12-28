import com.lmax.disruptor.EventFactory;

public class FileReaderEventFactory implements EventFactory<FileReaderEvent>
{
	public FileReaderEvent newInstance()
	{
		return new FileReaderEvent();
	}
}