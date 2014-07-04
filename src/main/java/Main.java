import util.FileUtils;
import util.TranscoderUtils;
import util.BitOutputStream;
public class Main
{
    public static boolean DEBUG = false;
	public static void main(final String[] args)
	{
		// Read image file into byte array
		final byte[] original = FileUtils.load(Main.class.getResource("chaosdorf-icon.jpg"));

		// Let's do the magic
		final int[] encoded = TranscoderUtils.encodeV2(original);
        bitWrite(encoded);

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
		FileUtils.save("src/main/resources/output.jpg", decoded);

		System.out.println("Done!");
	}
    private static void bitWrite(int[] encoded)
    {
        final BitOutputStream bos = new BitOutputStream("src/main/resources/encoded.txt");
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
}
