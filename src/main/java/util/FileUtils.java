package util;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtils
{
	public static void delete(String fileName)
	{
		try
		{
			Path path = Paths.get(fileName);
			Files.deleteIfExists(path);
		}
		catch (IOException ignored)
		{
		}
	}

	public static byte[] load(String fileName)
	{
		try
		{
			Path path = Paths.get(fileName);
			return Files.readAllBytes(path);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void save(String file, byte[] content)
	{
		if (content == null)
		{
			return;
		}
		try (FileOutputStream fileOutputStream = new FileOutputStream(file))
		{
			fileOutputStream.write(content);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void saveToChunks(String file, String content, int chunkSize)
	{
		try (FileOutputStream fileOutputStream = new FileOutputStream(file))
		{
			List<String> tweets = StringUtils.splitInChunks(content, chunkSize);
			for (String tweet : tweets)
			{
				fileOutputStream.write(tweet.getBytes());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void closeSilently(Closeable closeable)
	{
		if (closeable != null)
		{
			if (closeable instanceof FileWriter)
			{
				try
				{
					((FileWriter) closeable).flush();
				}
				catch (IOException ignored)
				{
				}
			}
			try
			{
				closeable.close();
			}
			catch (IOException ignored)
			{
			}
		}
	}
}
