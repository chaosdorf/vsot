import util.TranscoderUtils;

public class FileReaderEvent
{
	private int char1;
	private int char2;
	private boolean isPadded;

	public int getChar1()
	{
		return char1;
	}

	public void setChar1(final int char1)
	{
		this.char1 = char1;
	}

	public int getChar2()
	{
		return char2;
	}

	public void setChar2(final int char2)
	{
		this.char2 = char2;
	}

	public boolean isPadded()
	{
		return isPadded;
	}

	public void setPadded(final boolean isPadded)
	{
		this.isPadded = isPadded;
	}

	@Override
	public String toString()
	{
		return "FileReaderEvent{" +
				"char1=" + TranscoderUtils.toBinary(8, char1) +
				", char2=" + TranscoderUtils.toBinary(8, char2) +
				", isPadded=" + isPadded +
				'}';
	}
}