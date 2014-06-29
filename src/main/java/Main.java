import util.FileUtils;
import util.TranscoderUtils;

public class Main
{
	public static void main(final String[] args)
	{
		// Read image file into byte array
		final byte[] content = FileUtils.load(Main.class.getResource("chaosdorf-icon.jpg"));

		// Let's do the magic
		final String encodedBase64 = TranscoderUtils.encodeBase64(content);
		final byte[] decoded = TranscoderUtils.decodeBase64(encodedBase64);

		// Check if converting did work
		int limit = 0;
		for (int i = 0; i < content.length; i++)
		{
			if (content[i] != decoded[i])
			{
				if (limit == 0)
				{
					System.out.println("       Original Converted");
				}
				System.out.printf("%5d: %8s %8s\n",
						i,
						TranscoderUtils.toBinary(8, content[i]),
						TranscoderUtils.toBinary(8, decoded[i])
				);
				if (++limit == 10)
				{
					break;
				}
			}
		}
		if (limit > 0)
		{
			System.err.println("We had some errors in the converting process!");
			System.exit(1);
		}

		// Save image back into new file
		FileUtils.save("src/main/resources/output.jpg", decoded);

		// Save encoded Strings as tweetable chunks
		FileUtils.saveToChunks("src/main/resources/encoded.txt", encodedBase64, 140);
	}
}
