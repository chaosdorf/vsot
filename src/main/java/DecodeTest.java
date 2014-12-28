import util.FileUtils;
import util.TranscoderUtils;

public class DecodeTest
{
	public static void main(final String[] args)
	{
		final byte[] encodedLoad = FileUtils.load(DecodeTest.class.getResource("encoded.txt"));
		final byte[] decoded = TranscoderUtils.decode(encodedLoad);

		// Save decoded data back into new file
		FileUtils.save("src/main/resources/decoded.png", decoded);

		final byte[] original = FileUtils.load(DecodeTest.class.getResource("input.png"));
		TranscoderUtils.compareResults(original, decoded, 10);
	}
}
