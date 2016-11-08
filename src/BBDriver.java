import java.awt.GraphicsEnvironment;
import BouncyBallSource.BouncyBallV5;

public class BBDriver
{
	public static void main(String[] args)
	{
		if(!BBDriver.class.getResource("BBDriver.class").toString().startsWith("jar"))
			if(System.getProperty("os.name").toLowerCase().contains("windows"))
				if(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length > 1)
					new BouncyBallV5(1930, 10);
				else
					new BouncyBallV5(10, 10);
			else
				new BouncyBallV5(10, 30);
		else
			new BouncyBallV5(10, 10);
	}
}