import util.FileUtils;
import util.TranscoderUtils;

public class Main
{
	public static void main(final String[] args)
	{
		// Read image file into byte array
		final byte[] original = FileUtils.load(Main.class.getResource("chaosdorf-icon.jpg"));

		// Let's do the magic
		final byte[] encoded = TranscoderUtils.encode(original);
/*        for(int i = 0; i < original.length; i++)
        {
            System.out.printf("%x ",original[i]);
            if((i+1) % 15 == 0)System.out.print("\n");
        }
        System.out.print("\n##########\n");
*/
		final byte[] decoded = TranscoderUtils.decode(encoded);
        for(int i = 0; i < decoded.length; i++)
        {
            System.out.printf("%x ",decoded[i]);
            if((i+1) % 15 == 0)System.out.print("\n");
        }
        System.out.print("\n");
		// Check if converting did work
		/*if (TranscoderUtils.compareResults(original, decoded, 10))
		{
			System.err.println("We had some errors in the converting process!");
			System.exit(1);
		}
		*/
		System.out.println("Conversion was successful! Saving results...");

		// Save image back into new file
		FileUtils.save("src/main/resources/output.jpg", decoded);

		// Save encoded Strings in 140 character chunks
		//FileUtils.saveToChunks("src/main/resources/encoded.txt", encoded, 140);

		System.out.println("Done!");
	}
}
