import com.lmax.disruptor.EventHandler;
import util.FileUtils;

import java.io.FileWriter;
import java.io.IOException;

public class EncodedEventHandler implements EventHandler<EncodedEvent>
{
	private static final FileWriter fileWriter;

	static
	{
		FileWriter tmpFileWriter = null;
		try
		{
			tmpFileWriter = new FileWriter(Main.OUTPUT_FILE, true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		fileWriter = tmpFileWriter;
	}

	public void onEvent(EncodedEvent encodedEvent, long eventSequence, boolean endOfBatch)
	{
		try
		{
			fileWriter.append(encodedEvent.getValue());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void shutdown()
	{
		FileUtils.closeSilently(fileWriter);
	}
}