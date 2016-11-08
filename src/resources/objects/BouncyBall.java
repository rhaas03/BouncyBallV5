package resources.objects;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;

public class BouncyBall implements Serializable
{
	public static final double STANDARD_RLSPEED = 2.25;
	public static final double STANDARD_DEFALT_UPSPEED = 4.25;

	//Location and Ball Color Variables
	private double x, y;
	private int diameter;
	private Color[] color;

	//Speed Variables
	private double rlSpeed;
	private double defaultUpSpeed;
	private final double speedIncrement = .2;

	private double initSpeed = .1/2;
	private double downSpeed = initSpeed;
	private double upSpeed;

	//Direction Variables
	private int direction;
	public static final int UP = 1;
	public static final int DOWN = 2;

	private boolean right = false;
	private boolean left = false;

	public BouncyBall(double x, double y, int d, Color c)
	{
		this.x = x;
		this.y = y;
		this.diameter = d;
		this.color = new Color[]{Color.WHITE, c};
	}

	public BouncyBall(double x, double y, int d, Color c, double rlSpeed, double defaultUpSpeed)
	{
		this.x = x;
		this.y = y;
		this.diameter = d;
		this.color = new Color[]{Color.WHITE, c};
		this.rlSpeed = rlSpeed;
		this.defaultUpSpeed = defaultUpSpeed;
	}

	public void drawBall(Graphics2D g2d)
	{
		Point2D center = new Point2D.Float((int)x, (int)y);
		float radius;
		if(diameter > 1)
			radius = diameter/2;
		else
			radius = 1;
		float[] dist = {0.0f, 1.0f};
		RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, color);
		g2d.setPaint(rgp);

		g2d.fillOval((int)x - diameter/2, (int)y - diameter/2, diameter, diameter);
		g2d.setColor(Color.BLACK);
		g2d.drawOval((int)x - diameter/2, (int)y - diameter/2, diameter, diameter);
	}

	public void moveBall()
	{
		checkBounce();

		//Up and Down Movement
		if(direction == DOWN)
		{
			y += downSpeed;
			downSpeed += speedIncrement;
		}
		else if(direction == UP)
		{
			y -= upSpeed;
			upSpeed -= speedIncrement;
		}

		//Right and Left Movement
		if(right)
			x += rlSpeed;
		else if(left)
			x -= rlSpeed;
	}

	private void checkBounce()
	{
		if(upSpeed <= 0)
		{
			direction = DOWN;
			upSpeed = defaultUpSpeed;
			downSpeed = initSpeed;
		}
	}

	public int getX()
	{return (int)x;}

	public int getY()
	{return (int)y;}

	public int getDiameter()
	{return diameter;}

	public void setDirection(int direction)
	{
		this.direction = direction;
		if(this.direction == DOWN)
		{
			upSpeed = defaultUpSpeed;
			downSpeed = initSpeed;
		}
		else if(this.direction == UP)
			downSpeed = initSpeed;
	}

	public void setUpSpeed(double UpSpeed)
	{
		this.upSpeed = UpSpeed;
	}

	public void resetUpSpeed()
	{
		upSpeed = defaultUpSpeed;
	}

	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public Shape getShape()
	{
		return new Ellipse2D.Double(x - diameter/2, y - diameter/2, diameter, diameter);
	}

	public double getDefaultUpSpeed()
	{
		return defaultUpSpeed;
	}

	public double getRLSpeed()
	{
		return rlSpeed;
	}

	public String toString()
	{
		return "BouncyBall[x="+(int)x+", y="+(int)y+", d="+diameter+", rlSpeed="+rlSpeed+", upSpeed="+defaultUpSpeed+",direction="+direction+"]";
	}

	public void setRight(boolean b)
	{
		right = b;
	}

	public void setLeft(boolean b)
	{
		left = b;
	}

	public int getDirection()
	{
		return direction;
	}

	public int getBottomOfBall()
	{
		return (int)y + diameter/2;
	}

	public int getTopOfBall()
	{
		return (int)x - diameter/2;
	}

	public double getDownSpeed()
	{
		return downSpeed;
	}

	public double getUpSpeed()
	{
		return upSpeed;
	}

	public BouncyBall clone()
	{
		return new BouncyBall(this.x, this.y, this.diameter, this.color[1], this.rlSpeed, this.defaultUpSpeed);
	}
}
