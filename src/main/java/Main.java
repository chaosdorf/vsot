import util.FileUtils;
import util.TranscoderUtils;
import util.BitOutputStream;
public class Main
{
    public static boolean DEBUG = false;
	public static void main(final String[] args)
	{
		// Read image file into byte array
		final byte[] original = FileUtils.load(Main.class.getResource("stein.bmp"));

		// Let's do the magic
		final int[] encoded = TranscoderUtils.encodeV2(original);
        FileUtils.bitWrite(encoded, "src/main/resources/encoded.txt");

        final byte[] encodedLoad = FileUtils.load(Main.class.getResource("encoded.txt"));

        final byte[] decoded = TranscoderUtils.decode(encodedLoad);

		// Check if converting did work
		if (DEBUG && TranscoderUtils.compareResults(original, decoded, 10))
		{
			System.err.println("We had some errors in the converting process!");
			System.exit(1);
		}
		System.out.println("Conversion was successful! Saving results...");

		// Save image back into new file
		FileUtils.save("src/main/resources/output.bmp", decoded);

		// Save encoded Strings in 140 character chunks
		//FileUtils.saveToChunks("src/main/resources/encoded.txt", encoded, 140);

		System.out.println("Done!");
	}
}
