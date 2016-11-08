package resources;

import java.awt.*;
import javax.swing.*;

public class JGradientButton extends JButton
{
	private Color secondColor = Color.WHITE;

	public JGradientButton(String text)
	{
		super(text);
		setContentAreaFilled(false);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setPaint(new GradientPaint(
				new Point(0, 0), getBackground(),
				new Point(0, getHeight()/3), secondColor));
		g2d.fillRect(0, 0, getWidth(), getHeight()/3);
		g2d.setPaint(new GradientPaint(
				new Point(0, getHeight()/3), secondColor,
				new Point(0, getHeight()), getBackground()));
		g2d.fillRect(0, getHeight()/3, getWidth(), getHeight());
		//g2d.dispose();

		super.paintComponent(g);
	}

	public void setSecondColor(Color c)
	{
		secondColor = c;
	}
}
