import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
	public static void main(final String[] args) throws IOException, URISyntaxException
	{
		final URL url = Main.class.getResource("chaosdorf-icon.jpg");
		final Path path = Paths.get(url.toURI());
		final byte[] content = Files.readAllBytes(path);

		final String encoded = TranscoderUtils.encode(content);
		final byte[] decoded = TranscoderUtils.decode(encoded);

		int limit = 0;
		for (int i = 0; i < content.length; i++)
		{
			if (content[i] != decoded[i])
			{
				System.out.printf("%5d: %8s %8s\n",
						i,
						TranscoderUtils.toBinary(8, content[i]),
						TranscoderUtils.toBinary(8, decoded[i])
				);
				if (++limit > 10)
				{
					break;
				}
			}
		}

		//final FileOutputStream fos = new FileOutputStream("src/main/resources/output.jpg");
		//fos.write(decoded);
		//fos.close();
	}
}
