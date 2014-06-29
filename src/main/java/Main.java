import util.FileUtils;
import util.TranscoderUtils;

public class Main
{
	public static void main(final String[] args)
	{
		// Read image file into byte array
		final byte[] original = FileUtils.load(Main.class.getResource("chaosdorf-icon.jpg"));

		// Let's do the magic
		final String encoded = TranscoderUtils.encodeBase64(original);
		final byte[] decoded = TranscoderUtils.decodeBase64(encoded);

		// Check if converting did work
		if (TranscoderUtils.compareResults(original, decoded, 10))
		{
			System.err.println("We had some errors in the converting process!");
			System.exit(1);
		}
		System.out.println("Conversion was successful! Saving results...");

		// Save image back into new file
		FileUtils.save("src/main/resources/output.jpg", decoded);

		// Save encoded Strings in 140 character chunks
		FileUtils.saveToChunks("src/main/resources/encoded.txt", encoded, 140);

		System.out.println("Done!");
	}
}
