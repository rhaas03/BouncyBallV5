package resources.objects;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;

public class StarShape implements Serializable
{
	private int size;
	private int x, y;

	private Shape star;

	public static final double STAR_ROTATE_SPEED = .02;

	public StarShape(int x, int y, int size)
	{
		this.x = x; this.y = y; this.size = size/2;
		star = createDefaultStar(this.size, this.x, this.y);
	}

	public static Shape createDefaultStar(double radius, double centerX, double centerY)
    {
        return createStar(centerX, centerY, radius, radius * 2.63, 5,
            Math.toRadians(-18));
    }

    private static Shape createStar(double centerX, double centerY,
        double innerRadius, double outerRadius, int numRays,
        double startAngleRad)
    {
        Path2D path = new Path2D.Double();
        double deltaAngleRad = Math.PI / numRays;
        for (int i = 0; i < numRays * 2; i++)
        {
            double angleRad = startAngleRad + i * deltaAngleRad;
            double ca = Math.cos(angleRad);
            double sa = Math.sin(angleRad);
            double relX = ca;
            double relY = sa;
            if ((i & 1) == 0)
            {
                relX *= outerRadius;
                relY *= outerRadius;
            }
            else
            {
                relX *= innerRadius;
                relY *= innerRadius;
            }
            if (i == 0)
            {
                path.moveTo(centerX + relX, centerY + relY);
            }
            else
            {
                path.lineTo(centerX + relX, centerY + relY);
            }
        }
        path.closePath();
        return path;
    }

    public void drawStar(Graphics2D g2d)
    {
    	g2d.fill(star);
    }

    public void setLocation(int x, int y)
    {
    	this.x = x;
    	this.y = y;
    	star = createDefaultStar(size, x/2, y/2);
    }

    public int getX()
    {
    	return x;
    }
    public int getY()
    {
    	return y;
    }

    public int getSize()
    {
    	return size * 2;
    }

    public Shape getShape()
    {
    	return star;
    }

    public StarShape clone()
    {
    	return new StarShape(this.x, this.y, this.size = size*2);
    }
    
    public String toString()
    {
    	return "StarShape[x="+x+",y="+y+"]";
    }
}
