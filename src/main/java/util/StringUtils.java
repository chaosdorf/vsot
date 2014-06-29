package util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils
{
	public static List<String> splitInChunks(String text, int chunkSize)
	{
		final int length = text.length();
		final List<String> result = new ArrayList<>((length + chunkSize - 1) / chunkSize);
		for (int i = 0; i < length; i += chunkSize)
		{
			result.add(text.substring(i, Math.min(length, i + chunkSize)) + "\n");
		}

		return result;
	}
}
