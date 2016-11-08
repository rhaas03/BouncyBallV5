package resources;

import java.awt.*;
import javax.swing.*;

public class RichJLabel extends JLabel {
	private int x, y;

	public RichJLabel(String text)
	{
		super(text);
		x = 0;
		x = 0;
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		g2d.setFont(getFont());
		FontMetrics fm = g2d.getFontMetrics();

		g2d.setColor(getForeground());
		g2d.drawString(getText(), x, y + fm.getAscent());
	}
}
