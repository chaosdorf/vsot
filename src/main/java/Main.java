public class Main
{
	private static boolean DEBUG = false;

	public static void main(final String[] args)
	{
		final String content = "Bei der UTF-8-Kodierung wird jedem Unicode-Zeichen eine speziell kodierte Zeichenkette variabler Länge zugeordnet. Dabei unterstützt UTF-8 Zeichenketten bis zu einer Länge von vier Byte, auf die sich – wie bei allen UTF-Formaten – alle Unicode-Zeichen abbilden lassen.";

		// Original content
		System.out.println("Original content:");
		System.out.println(content);
		System.out.println();

		// ASCII only
		System.out.println("Convert ASCII only:");

		final String encodedJustASCII = encodeJustASCII(content);
		System.out.println(encodedJustASCII + " (" + encodedJustASCII.length() + "/140)");

		final String decodedJustASCII = decodeFrom3Byte(encodedJustASCII);
		System.out.println(decodedJustASCII + " (" + decodedJustASCII.length() + "/140)");

		System.out.println();

		// 3 Byte for 2 characters
		System.out.println("Convert to 3 Byte:");

		final String encodedTo3Byte = encodeTo3Byte(content);
		System.out.println(encodedTo3Byte + " (" + encodedTo3Byte.length() + "/140)");

		final String decodedFrom3Byte = decodeFrom3Byte(encodedTo3Byte);
		System.out.println(decodedFrom3Byte + " (" + decodedFrom3Byte.length() + "/140)");
	}

	private static String encodeJustASCII(final String input)
	{
		String content = input;
		while (content.length() % 2 > 0)
		{
			content += " ";
		}

		String out = "";
		for (int i = 0; i < content.length(); i += 2)
		{
			out += (char) (content.charAt(i) << 8 | content.charAt(i + 1));
		}

		return out;
	}

	private static String encodeTo3Byte(final String input)
	{
		String content = input;
		int byteLength = content.getBytes().length;
		while (byteLength % 2 > 0)
		{
			content += " ";
			byteLength = content.getBytes().length;
		}

		final byte[] data = content.getBytes();
		final int size = (int) Math.ceil(data.length * 3 / 2);
		final byte[] encoded = new byte[size];

		int j = 0;
		for (int i = 0; i < size; i += 3)
		{
			encoded[i    ] = (byte) (0b1110_0000 | ((data[j    ] & 0b1111_0000) >> 4));
			encoded[i + 1] = (byte) (0b1000_0000 | ((data[j    ] & 0b0000_1111) << 2) | ((data[j + 1] & 0b1100_0000) >> 6));
			encoded[i + 2] = (byte) (0b1000_0000 | ((data[j + 1] & 0b0011_1111)));

			if (DEBUG)
			{
				System.out.printf("1110 %s - 10 %s %s - 10 %s - %s %s - %s\n",
						toBinary(4, ((data[j    ] & 0b1111_0000) >> 4)),
						toBinary(4, ((data[j    ] & 0b0000_1111))),
						toBinary(2, ((data[j + 1] & 0b1100_0000) >> 6)),
						toBinary(6, ((data[j + 1] & 0b0011_1111))),
						(char) data[j    ],
						(char) data[j + 1],
						new String(new byte[]{encoded[i], encoded[i + 1], encoded[i + 2]})
				);
			}

			j += 2;
		}

		return new String(encoded);
	}

	private static String decodeFrom3Byte(final String content)
	{
		final byte[] data = content.getBytes();
		final byte[] decoded = new byte[(int) Math.ceil(data.length * 2 / 3)];

		int j = 0;
		for (int i = 0; i < data.length; i += 3)
		{
			decoded[j    ] = (byte) (((data[i    ] & 0b0000_1111) << 4) | ((data[i + 1] & 0b0011_1100) >> 2));
			decoded[j + 1] = (byte) (((data[i + 1] & 0b0000_0011) << 6) | ((data[i + 2] & 0b0011_1111)));
			j += 2;
		}

		return (new String(decoded)).trim();
	}

	private static String toBinary(final int bits, final int data)
	{
		return String.format("%" + bits + "s", Integer.toBinaryString(data)).replace(' ', '0');
	}
}
