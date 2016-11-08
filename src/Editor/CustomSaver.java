package Editor;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;

public class CustomSaver implements Serializable
{
	private ArrayList<Object> objs;
	private Dimension size;

	public CustomSaver(ArrayList<Object> objs, Dimension size)
	{
		this.objs = objs; this.size = size;
	}

	public ArrayList<Object> getAllObjects()
	{
		return objs;
	}

	public Dimension getSize()
	{
		return size;
	}
}
