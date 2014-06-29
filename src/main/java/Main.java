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
		if (TranscoderUtils.compareResults(content, decoded, 10))
		{
			System.err.println("We had some errors in the converting process!");
			System.exit(1);
		}
		System.out.println("Conversion was successful! Saving results...");

		// Save image back into new file
		FileUtils.save("src/main/resources/output.jpg", decoded);

		// Save encoded Strings in 140 character chunks
		FileUtils.saveToChunks("src/main/resources/encoded.txt", encodedBase64, 140);

		System.out.println("Done!");
	}
}
