package util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtils
{
	public static byte[] load(final URL url)
	{
		try
		{
			final Path path = Paths.get(url.toURI());
			return Files.readAllBytes(path);
		}
		catch (URISyntaxException | IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
    public static void bitWrite(int[] encoded, String file)
    {
        final BitOutputStream bos = new BitOutputStream(file);
        try
        {
            for(int codepoint: encoded)
            {
                bos.write(24, codepoint);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            bos.close();
        }
    }
	public static void save(final String file, final byte[] content)
	{
		try (final FileOutputStream fileOutputStream = new FileOutputStream(file))
		{
			fileOutputStream.write(content);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void saveToChunks(final String file, final String content, final int chunkSize)
	{
		try (final FileOutputStream fileOutputStream = new FileOutputStream(file))
		{
			final List<String> tweets = StringUtils.splitInChunks(content, chunkSize);
			for (final String tweet : tweets)
			{
				fileOutputStream.write(tweet.getBytes());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
