package util;

public class TranscoderUtils
{
	private static boolean DEBUG = true;

	public static int[] encode(final byte[] data)
	{
		final int inputLength = data.length;
		final boolean isUneven = (inputLength % 2 > 0);
		byte[] input = new byte[inputLength + (isUneven ? 1 : 0)];
		System.arraycopy(data, 0, input, 0, inputLength);
		if (isUneven)
		{
			input[input.length - 1] = 0;
		}
		final int[] encoded = new int[inputLength / 2 + (isUneven ? 1 : 0)];

		for (int i = 0; i < inputLength; i += 2)
		{
			if (DEBUG)
			{
				System.out.println(toBinary(32, input[i]));
				System.out.println(toBinary(32, input[i + 1]));
			}

			// 0b1111_0000 of input1
			int newCodepoint = 0xe0;
			newCodepoint += ((input[i] & 0b1111_0000) >>> 4);
			if (DEBUG)
			{
				System.out.println("first: " + toBinary(32, newCodepoint));
			}

			// 0b0000_1111 of input1 and 0b1100_0000 of input2
			newCodepoint <<= 8;
			newCodepoint += 0x80;
			newCodepoint += ((input[i] & 0b0000_1111) << 2);
			newCodepoint += ((input[i + 1] & 0b1100_0000) >>> 6);
			if (DEBUG)
			{
				System.out.println("second: " + toBinary(32, newCodepoint));
			}

			// 0b0011_1111 of input2
			newCodepoint <<= 8;
			newCodepoint += 0x80;
			newCodepoint += (input[i + 1] & 0x3F);
			if (DEBUG)
			{
				System.out.println("third: " + toBinary(32, newCodepoint));
			}

			encoded[i / 2] = newCodepoint;
		}
		return encoded;
	}

	public static byte[] decode(final byte[] data)
	{
		// Decode data
		final byte[] decoded = new byte[calcDecodedByteArraySize(data.length, 0)];
		int skippedBytes = 0;

		int j = 0;
		for (int i = 0; i < data.length - 2; i += 3)
		{
			boolean invert = false;
			if (data[i] == 33)
			{
				invert = true;
				skippedBytes++;
				i++;
			}
			boolean isPadding = false;
			if (data[i] == 45)
			{
				isPadding = true;
				skippedBytes += 2;
				i++;
			}

			decoded[j] = (byte) (((data[i] & 0b0000_1111) << 4) | ((data[i + 1] & 0b0011_1100) >>> 2));
			if (!isPadding)
			{
				decoded[j + 1] = (byte) (((data[i + 1] & 0b0000_0011) << 6) | ((data[i + 2] & 0b0011_1111)));
			}

			if (invert)
			{
				decoded[j] = (byte) (~decoded[j] & 0xff);
				if (!isPadding)
				{
					decoded[j + 1] = (byte) (~decoded[j + 1] & 0xff);
				}
			}

			j += 2;
		}

		byte[] result = new byte[calcDecodedByteArraySize(data.length, skippedBytes)];
		System.arraycopy(decoded, 0, result, 0, result.length);

		return result;
	}

	public static boolean compareResults(final byte[] original, final byte[] decoded, final int maxResults)
	{
		if (original == null)
		{
			System.out.println("No original data!");
			return false;
		}
		if (decoded == null)
		{
			System.out.println("No decoded data!");
			return false;
		}

		int orgLength = original.length;
		int convertedLength = decoded.length;

		if (orgLength != convertedLength)
		{
			System.out.println(String.format("Length mismatch! %d (org) vs. %s (decoded)", orgLength, convertedLength));
		}

		int limit = 0;
		for (int i = 0; i < orgLength; i++)
		{
			if (original[i] != decoded[i])
			{
				if (limit == 0)
				{
					System.out.println("       Original Converted");
				}
				System.out.printf("%5d: %8s %8s%n",
						i,
						TranscoderUtils.toBinary(8, original[i]),
						TranscoderUtils.toBinary(8, decoded[i])
				);
				if (++limit == maxResults)
				{
					break;
				}
			}
		}
		return (limit > 0);
	}

	public static String toBinary(final int bits, final int data)
	{
		final String binary = String.format("%" + bits + "s", Integer.toBinaryString(data)).replace(' ', '0');
		return binary.substring(binary.length() - bits);
	}

	private static int calcDecodedByteArraySize(int originalLength, int skippedBytes)
	{
		return (originalLength - skippedBytes) * 2 / 3;
	}
}
