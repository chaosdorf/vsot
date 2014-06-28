public class Example
{
	public static void example()
	{
		final String content = "Bei der UTF-8-Kodierung wird jedem Unicode-Zeichen eine speziell kodierte Zeichenkette variabler Länge zugeordnet. Dabei unterstützt UTF-8 Zeichenketten bis zu einer Länge von vier Byte, auf die sich – wie bei allen UTF-Formaten – alle Unicode-Zeichen abbilden lassen.";

		// Original content
		System.out.println("Original content:");
		System.out.println(content + " (" + content.length() + "/140)");
		System.out.println();

		// ASCII only
		System.out.println("Convert ASCII only:");

		final String encodedJustASCII = encodeJustASCII(content);
		System.out.println(encodedJustASCII + " (" + encodedJustASCII.length() + "/140)");

		final String decodedJustASCII = TranscoderUtils.decodeToString(encodedJustASCII);
		System.out.println(decodedJustASCII + " (" + decodedJustASCII.length() + "/140)");

		System.out.println();

		// 3 Byte for 2 characters
		System.out.println("Convert to 3 Byte:");

		final String encodedTo3Byte = TranscoderUtils.encode(content.getBytes());
		System.out.println(encodedTo3Byte + " (" + encodedTo3Byte.length() + "/140)");

		final String decodedFrom3Byte = TranscoderUtils.decodeToString(encodedTo3Byte);
		System.out.println(decodedFrom3Byte + " (" + decodedFrom3Byte.length() + "/140)");
	}

	private static String encodeJustASCII(final String input)
	{
		// Pad data if number of characters is uneven
		String data = input;
		while (data.length() % 2 > 0)
		{
			data += " ";
		}

		// Encode data
		String encoded = "";
		for (int i = 0; i < data.length(); i += 2)
		{
			encoded += (char) (data.charAt(i) << 8 | data.charAt(i + 1));
		}

		return encoded;
	}
}
