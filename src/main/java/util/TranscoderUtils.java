package util;

import org.apache.commons.codec.binary.Base64;

public class TranscoderUtils
{
	private static boolean DEBUG = false;

	public static String encodeBase64(final byte[] input)
	{
		return encode(Base64.encodeBase64(input));
	}

	public static byte[] decodeBase64(final String input)
	{
		return Base64.decodeBase64(decode(input));
	}

	public static String encode(final byte[] input)
	{
		// Pad data if number of bytes is uneven
		final int byteLength = input.length;
		final boolean isUneven = (byteLength % 2 > 0);
		final byte[] data = new byte[isUneven ? byteLength + 1 : byteLength];
		System.arraycopy(input, 0, data, 0, byteLength);
		if (isUneven)
		{
			data[byteLength] = ' ';
		}

		// Encode data
		final int size = (int) Math.ceil(data.length * 3 / 2);
		final byte[] encoded = new byte[size];

		int j = 0;
		for (int i = 0; i < size; i += 3)
		{
			encoded[i] = (byte) (0b1110_0000 | ((data[j] & 0b1111_0000) >> 4));
			encoded[i + 1] = (byte) (0b1000_0000 | ((data[j] & 0b0000_1111) << 2) | ((data[j + 1] & 0b1100_0000) >> 6));
			encoded[i + 2] = (byte) (0b1000_0000 | ((data[j + 1] & 0b0011_1111)));

			if (DEBUG)
			{
				System.out.printf("1110 %s - 10 %s %s - 10 %s - %s %s - %s\n",
						toBinary(4, ((data[j] & 0b1111_0000) >> 4)),
						toBinary(4, ((data[j] & 0b0000_1111))),
						toBinary(2, ((data[j + 1] & 0b1100_0000) >> 6)),
						toBinary(6, ((data[j + 1] & 0b0011_1111))),
						(char) data[j],
						(char) data[j + 1],
						new String(new byte[]{encoded[i], encoded[i + 1], encoded[i + 2]})
				);
			}

			j += 2;
		}

		return new String(encoded);
	}

	public static byte[] decode(final String input)
	{
		final byte[] data = input.getBytes();

		// Decode data
		final byte[] decoded = new byte[(int) Math.ceil(data.length * 2 / 3)];

		int j = 0;
		for (int i = 0; i < data.length; i += 3)
		{
			decoded[j] = (byte) (((data[i] & 0b0000_1111) << 4) | ((data[i + 1] & 0b0011_1100) >> 2));
			decoded[j + 1] = (byte) (((data[i + 1] & 0b0000_0011) << 6) | ((data[i + 2] & 0b0011_1111)));
			j += 2;
		}

		return decoded;
	}

	public static String decodeToString(final String input)
	{
		return (new String(decode(input))).trim();
	}

	public static String toBinary(final int bits, final int data)
	{
		return String.format("%" + bits + "s", Integer.toBinaryString(data)).replace(' ', '0');
	}
}
