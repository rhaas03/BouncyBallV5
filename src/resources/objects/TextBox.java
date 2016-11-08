package resources.objects;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class TextBox implements Serializable
{
	private String initText;
	private String[] text;
	private Font font = new Font("", Font.PLAIN, 22);
	private Rectangle2D box;

	private Color foreground = Color.BLACK;
	private Color background = null;

	public static final int LEFT = 1;
	public static final int CENTER = 2;
	public static final int RIGHT = 3;
	private int alignment = LEFT;

	private int x, y, width, height;

	private Insets insets = new Insets(0, 0, 0, 0);

	public TextBox(String text, int x, int y)
	{
		initText = text;
		this.text = text.split("\\n");
		this.x = x;
		this.y = y;
		basicOps();
	}

	public TextBox(String text, int x, int y, Font f)
	{
		initText = text;
		this.text = text.split("\\n");
		this.x = x;
		this.y = y;
		this.font = f;
		basicOps();
	}

	public TextBox(String text, int x, int y, Font f, int align)
	{
		initText = text;
		this.text = text.split("\\n");
		this.x = x;
		this.y = y;
		this.font = f;
		this.alignment = align;
		basicOps();
	}

	private void basicOps()
	{
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();

		if(this.text.length > 1)
		{
			int[] textWidths = new int[this.text.length];
			for(int i = 0; i < this.text.length; i++)
				textWidths[i] = fm.stringWidth(this.text[i]);

			int maxWidth = 0;
			for(int i = 0; i < textWidths.length; i++)
				maxWidth = Math.max(textWidths[i], maxWidth);

			height = (fm.getHeight()/2 * this.text.length) + (10 * this.text.length);
			width = maxWidth;
		}
		else
		{
			height = (fm.getHeight()/2 * this.text.length) + (10 * this.text.length);
			width = fm.stringWidth(this.text[0]);
		}

		box = new Rectangle2D.Double(this.x + fm.getLeading(), this.y, width + insets.left + insets.right, height + insets.top + insets.bottom + fm.getDescent());
	}

	public void drawText(Graphics2D g2d)
	{
		if(background != null)
			g2d.setColor(background);
		else
			g2d.setColor(new Color(0,0,0,0));

		g2d.fill(box);

		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();

		g2d.setColor(foreground);
		if(alignment == LEFT)
		{
			int stringY = (int)box.getY() + insets.top + fm.getAscent();
			for(int i = 0; i < text.length; i++)
			{
				int stringX = (int)box.getX() + insets.left;
				g2d.drawString(text[i], stringX, stringY);
				stringY += fm.getHeight()/2 + 10;
			}
		}
		else if(alignment == CENTER)
		{
			//int stringY = (int)(box.getCenterY() - fm.getHeight()/2 + fm.getAscent());
			int stringY = (int)box.getY() + insets.top + fm.getAscent();
			for(int i = 0; i < text.length; i++)
			{
				int stringX = (int)((box.getCenterX() - fm.stringWidth(text[i])/ 2 - fm.getLeading() * font.getSize()/4)) + fm.getLeading()*4;
				g2d.drawString(text[i], stringX, stringY);
				stringY += fm.getHeight()/2 + 10;
			}
		}
		else if(alignment == RIGHT)
		{
			int stringY = (int)box.getY() + insets.top + fm.getAscent();
			for(int i = 0; i < text.length; i++)
			{
				int stringX = (int)((box.getMaxX() - fm.stringWidth(text[i]) - fm.getLeading() * font.getSize()/4));
				g2d.drawString(text[i], stringX, stringY);
				stringY += fm.getHeight()/2 + 10;
			}
		}
	}

	public void setForeground(Color fg)
	{
		foreground = fg;
	}

	public void setBackground(Color bg)
	{
		background = bg;
	}

	public void setFont(Font f)
	{
		font = f;

		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();

		if(this.text.length > 1)
		{
			int[] textWidths = new int[this.text.length];
			for(int i = 0; i < this.text.length; i++)
				textWidths[i] = fm.stringWidth(this.text[i]);

			int maxWidth = 0;
			for(int i = 0; i < textWidths.length; i++)
				maxWidth = Math.max(textWidths[i], maxWidth);

			height = (fm.getHeight()/2 * this.text.length) + (10 * this.text.length);
			width = maxWidth;
		}
		else
		{
			height = (fm.getHeight()/2 * this.text.length) + (10 * this.text.length);
			width = fm.stringWidth(this.text[0]);
		}

		box = new Rectangle2D.Double(this.x + fm.getLeading(), this.y, width + insets.left + insets.right, height + insets.top + insets.bottom + fm.getDescent());
	}

	public void setInsets(Insets i)
	{
		insets = i;
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		box = new Rectangle2D.Double(this.x + fm.getLeading(), this.y, width + insets.left + insets.right, height + insets.top + insets.bottom + fm.getDescent());
	}

	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;

		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();

		if(this.text.length > 1)
		{
			int[] textWidths = new int[this.text.length];
			for(int i = 0; i < this.text.length; i++)
				textWidths[i] = fm.stringWidth(this.text[i]);

			int maxWidth = 0;
			for(int i = 0; i < textWidths.length; i++)
				maxWidth = Math.max(textWidths[i], maxWidth);

			height = (fm.getHeight()/2 * this.text.length) + (10 * this.text.length);
			width = maxWidth;
		}
		else
		{
			height = (fm.getHeight()/2 * this.text.length) + (10 * this.text.length);
			width = fm.stringWidth(this.text[0]);
		}

		box = new Rectangle2D.Double(this.x + fm.getLeading(), this.y, width + insets.left + insets.right, height + insets.top + insets.bottom + fm.getDescent());
	}

	public void setAlignment(int align)
	{
		alignment = align;
	}

	public void setText(String text)
	{
		this.text = text.split("\\n");
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();

		if(this.text.length > 1)
		{
			int[] textWidths = new int[this.text.length];
			for(int i = 0; i < this.text.length; i++)
				textWidths[i] = fm.stringWidth(this.text[i]);

			int maxWidth = 0;
			for(int i = 0; i < textWidths.length; i++)
				maxWidth = Math.max(textWidths[i], maxWidth);

			height = (fm.getHeight()/2 * this.text.length) + (10 * this.text.length);
			width = maxWidth;
		}
		else
		{
			height = (fm.getHeight()/2 * this.text.length) + (10 * this.text.length);
			width = fm.stringWidth(this.text[0]);
		}

		box = new Rectangle2D.Double(this.x + fm.getLeading(), this.y, width + insets.left + insets.right, height + insets.top + insets.bottom + fm.getDescent());
	}

	public String getText()
	{
		return initText.replace("\n", "\\n");
	}

	public Shape getShape()
	{
		return box;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public TextBox clone()
	{
		return new TextBox(this.initText, this.x, this.y);
	}

	public String toString()
	{
		return "TextBox[text=" + initText.replace("\n", "\\n") + ",x=" + x + ",y=" + y + "]";
	}

	public int getAlignment()
	{
		return alignment;
	}

	public boolean getBold()
	{
		return font.isBold();
	}

	public String getFontName()
	{
		return font.getFontName();
	}

	public int getFontSize()
	{
		return font.getSize();
	}
}
