package BouncyBallSource;

/*
 * Created by Ryan Haas
 * Started Mid September
 */

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.joda.time.DateTime;

import Editor.EditorMenu;
import resources.*;

public class BouncyBallV5 extends JPanel implements ActionListener, MouseMotionListener, MouseListener, KeyListener, FocusListener
{
	public JFrame frame = new JFrame("Bouncy Ball v5");
	public Container canvas;

	public Encrypter encrypter = new Encrypter(Encrypter.LOW_ENCRYPTION);

	public static final int DEFAULT_WIDTH = 600;
	public static final int DEFAULT_HEIGHT = 300;

	//Screens
	private GameMenu gameMenu;
	private About about;
	private Help help;
	private EditorMenu editorMenu;
	private final String[] allScreens = {"Menu", "About", "Help", "Editor"};

	//Layout/Screen Controller
	private CardLayout screenController = new CardLayout();
	private JPanel screenContainer = new JPanel(screenController);

	//Used to move frame while being undecorated
	private ComponentMover cm = new ComponentMover();

	private Timer refresh = new Timer(1000/60, this);
	private Timer clearGarbage = new Timer(10000, this);
	private Timer minimizeTimer = new Timer(1000/30, this);

	//Header Buttons
	private JButton quit = new JButton("\u2715");
	private JButton minimize = new JButton("\u2015");
	private JButton back = new JButton("\u2190");

	//Various Colors
	private final Color buttonColor = new Color(0, 150, 250);
	private Color ballColor = Color.BLUE;
	private Color mouseLineColor = new Color(0, 150, 250);
	public static final Color BG_COLOR = new Color(220, 220, 220);

	//Version Number
	public static final String VERSION = "5.0.0A1";

	//Screen ints (to set and change screens)
	public static final int GAME_MENU = 1;
	public static final int PLAY_MENU = 2;
	public static final int SETTINGS = 3;
	public static final int ABOUT = 4;
	public static final int HELP = 5;
	public static final int EDITOR_MENU = 6;
	private int screen;

	//In order for back button to work
	private ArrayList<Integer> screenHistory = new ArrayList<Integer>();
	private int screenCounter = -1;

	//Fonts
	public Font comfortaa;
	public Font samsung1;
	public Font blackTuesday;
	public Font timeburner;
	public Font apple2;

	//Booleans, self-explanatory
	public boolean outOfFocus;
	public boolean consoleOpen = false;

	//Components for header
	private JPanel headPane;
	private JLabel headTitle;
	private JPanel buttonPane;

	//Root path for files and saves
	public static final String FILE_PATH = ((BouncyBallV5.class.getResource("BouncyBallV5.class").toString().startsWith("jar") ? ".BB5_Info/" :
		BouncyBallV5.class.getProtectionDomain().getCodeSource().getLocation().getPath() + ".BB5_Info/"));

	//Log Directory
	private final File logDirectory = new File(FILE_PATH + "logs/");

	//Icon File
	public final ImageIcon fav = new ImageIcon(getClass().getResource("/resources/favicon.png"));

	//For Log File Output
	private File logFile;
	private BufferedWriter logWriter;

	public BouncyBallV5(int locX, int locY)
	{
		//Basic Window Setup
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		frame.addWindowListener(closer());
		frame.setLocation(locX, locY);
		frame.setUndecorated(true);
		frame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		frame.addKeyListener(this);
		frame.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		frame.addFocusListener(this);
		frame.setResizable(false);
		setBackground(BG_COLOR);

		//Checks if FILE_PATH exists
		if(!new File(FILE_PATH).isDirectory())
			new File(FILE_PATH).mkdir();
		//Checks if logDirectory exists
		if(!logDirectory.isDirectory())
			logDirectory.mkdir();

		//For log file
		DateTime dt = new DateTime();
		String year = Integer.toString(dt.getYear());
		String month = Integer.toString(dt.getMonthOfYear()).length() < 2 ? "0" + Integer.toString(dt.getMonthOfYear()) : Integer.toString(dt.getMonthOfYear());
		String day = Integer.toString(dt.getDayOfMonth()).length() < 2 ? "0" + Integer.toString(dt.getDayOfMonth()) : Integer.toString(dt.getDayOfMonth());
		String hour = "";
		if(dt.getHourOfDay() > 12)
			if(Integer.toString(dt.getHourOfDay() - 12).length() < 2)
				hour = "0" + Integer.toString(dt.getHourOfDay() - 12);
			else
				hour = Integer.toString(dt.getHourOfDay() - 12);
		else
			hour = Integer.toString(dt.getHourOfDay());

		String minute = Integer.toString(dt.getMinuteOfHour()).length() < 2 ? "0" + Integer.toString(dt.getMinuteOfHour()) : Integer.toString(dt.getMinuteOfHour());
		String second = Integer.toString(dt.getSecondOfMinute()).length() < 2 ? "0" + Integer.toString(dt.getSecondOfMinute()) : Integer.toString(dt.getSecondOfMinute());

		logFile = new File(logDirectory.getPath() + "/log" + year + "-" + month + "-" + day  + "-" + hour + "-" + minute  + "-" + second + ".txt");
		if(!logFile.exists())
			try
		{
				logFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		try
		{
			logWriter = new BufferedWriter(new FileWriter(logFile));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		log("-------");

		//Adds the initial screen
		screen = GAME_MENU;
		screenHistory.add(screen);
		addHeader();

		addMouseListener(this);
		addMouseMotionListener(this);

		canvas = frame.getContentPane();
		canvas.add(this);

		//Allows Mac computers to use the menu bar
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		//Registers all Fonts, and sets the UI Look and Feel to the system's
		try
		{
			comfortaa = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/fonts/comfortaa.ttf"));
			samsung1 = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/fonts/samsung.ttf"));
			blackTuesday = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/fonts/black_tuesday.ttf"));
			timeburner = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/fonts/timeburnernormal.ttf"));
			apple2 = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("resources/fonts/Apple2.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(comfortaa);
			ge.registerFont(samsung1);
			ge.registerFont(blackTuesday);
			ge.registerFont(timeburner);
			ge.registerFont(apple2);

			log("Fonts registered");

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
			log("Error Registering Fonts");
		}

		//Sets the icon of the window
		frame.setIconImage(fav.getImage());
		try
		{
			Class.forName("com.apple.eawt.Application");
			com.apple.eawt.Application.getApplication().setDockIconImage(frame.getIconImage());
			log("Running Mac OS X");
		} catch(Exception e)
		{
			//e.printStackTrace();
			System.err.println("Not a mac");
			log("Not running Mac OS X");
		}

		frame.pack();

		//Initializes all the screens
		gameMenu = new GameMenu(this, ballColor);
		about = new About(this);
		help = new Help(this);
		editorMenu = new EditorMenu(this);

		//Adds the screen to the controller panel
		frame.add(screenContainer, BorderLayout.CENTER);
		screenContainer.add(this, allScreens[0]);
		screenContainer.add(about, allScreens[1]);
		screenContainer.add(help, allScreens[2]);
		screenContainer.add(editorMenu, allScreens[3]);

		//Shows the menu
		screenController.show(screenContainer, allScreens[0]);

		//Sets the window visible
		frame.setVisible(true);
		outOfFocus = false;

		//Registers components, allowing the frame to move around the screen
		//while undecorated == true
		cm.registerComponent(frame, about, help, editorMenu, this);
		cm.setChangeCursor(false);
		/*if(System.getProperty("os.name").contains("Windows"))
			cm.setEdgeInsets(new Insets(-1000, -1000, 1000, 1000));*/

		//Starts the timers
		refresh.start();
		clearGarbage.start();

		log("Environment setup successful");

		//Runs Garbage Collector
		Runtime.getRuntime().gc();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		//Adds antialiasing to make graphics look better
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		//Draws the Menu
		if(screen == GAME_MENU)
			gameMenu.drawGameMenu(g2d);
	}

	//Adds the title bar
	private void addHeader()
	{
		headPane = new JPanel();
		headPane.setLayout(new BoxLayout(headPane, BoxLayout.LINE_AXIS));
		headPane.setBackground(mouseLineColor);

		buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
		buttonPane.setBackground(mouseLineColor);

		Font buttonFont = new Font("", Font.PLAIN, 18);

		//Minimize Button
		minimize.setFocusable(false);
		minimize.setPreferredSize(new Dimension(30, 20));
		minimize.setMargin(new Insets(0, 0, 0, 0));
		minimize.setOpaque(false);
		minimize.setBorder(null);
		minimize.setForeground(Color.WHITE);
		minimize.setOpaque(true);
		minimize.setFont(buttonFont);
		minimize.setBackground(buttonColor);

		//Quit Button
		quit.setFocusable(false);
		quit.setPreferredSize(new Dimension(30, 20));
		quit.setMargin(new Insets(0, 0, 0, 0));
		quit.setBorder(null);
		quit.setForeground(Color.WHITE);
		quit.setOpaque(true);
		quit.setFont(buttonFont);
		quit.setBackground(buttonColor);

		//Back Button
		back.setFocusable(false);
		back.setPreferredSize(new Dimension(30, 20));
		back.setMargin(new Insets(0, 0, 0, 0));
		back.setOpaque(false);
		back.setBorder(null);
		back.setForeground(Color.WHITE);
		back.setOpaque(true);
		back.setFont(buttonFont);
		back.setBackground(buttonColor);

		buttonPane.add(back);
		back.setVisible(false);

		buttonPane.add(minimize);
		buttonPane.add(quit);

		//Title Label
		headTitle = new JLabel("Bouncy Ball Version " + VERSION);
		headTitle.setBorder(new EmptyBorder(0, 5, 0, 0));
		headTitle.setFont(new Font("", Font.BOLD, 14));
		headTitle.setForeground(Color.WHITE);
		headTitle.setOpaque(true);
		headTitle.setFocusable(false);
		headTitle.setBackground(mouseLineColor);
		headPane.setFocusable(false);
		buttonPane.setFocusable(false);

		headPane.add(headTitle);
		headPane.add(Box.createHorizontalGlue());
		headPane.add(buttonPane);

		minimize.addActionListener(this);
		quit.addActionListener(this);
		back.addActionListener(this);

		minimize.addMouseListener(this);
		quit.addMouseListener(this);
		back.addMouseListener(this);

		headPane.setPreferredSize(new Dimension(headPane.getPreferredSize().width, 24));
		frame.add(headPane, BorderLayout.NORTH);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == refresh)
		{
			repaint();
			if(screen == GAME_MENU)
				gameMenu.moveSample();
			else if(screen == EDITOR_MENU)
			{
				//Animates Editor Menu Buttons
				editorMenu.moveCreate();
				editorMenu.moveEdit();
				editorMenu.movePlay();
			}
		}
		else if(source == quit)
		{
			try
			{
				log("Exiting");
				log("-------");
				logWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
		else if(source == minimize)
		{
			log("Frame minimized");
			frame.setState(JFrame.ICONIFIED);
			refresh.stop();
			minimizeTimer.start();
		}
		else if(source == clearGarbage)
			//Runs Garabage Collector
			Runtime.getRuntime().gc();
		else if(source == back)
			moveBackScreen();
		else if(source == minimizeTimer)
		{
			if(frame.getState() != JFrame.ICONIFIED)
			{
				minimizeTimer.stop();
				refresh.start();
				log("Frame re-opened");
			}
		}
	}

	public void mousePressed(MouseEvent e)
	{
		Object source = e.getSource();
		if(!outOfFocus)
		{
			if(screen == GAME_MENU)
			{
				//Checks if the moused was pressed over the Menu Buttons
				gameMenu.clickedButtons(e.getX(), e.getY());

				//Shows screens
				if(screen == ABOUT)
					screenController.show(screenContainer, allScreens[1]);
				else if(screen == HELP)
					screenController.show(screenContainer, allScreens[2]);
				else if(screen == EDITOR_MENU)
				{
					screenController.show(screenContainer, allScreens[3]);
					changeSize(DEFAULT_WIDTH, frame.getHeight() - 30);
				}
			}
			else if(screen == EDITOR_MENU)
				if(source == editorMenu.createButton)
					editorMenu.createPressed();
				else if(source == editorMenu.editButton)
					editorMenu.editPressed();
				else if(source == editorMenu.playButton)
					editorMenu.playPressed();
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		Object source = e.getSource();
		if(!outOfFocus)
		{
			if(screen == EDITOR_MENU)
				if(source == editorMenu.createButton)
					editorMenu.createReleased();
				else if(source == editorMenu.editButton)
					editorMenu.editReleased();
				else if(source == editorMenu.playButton)
					editorMenu.playReleased();
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		if(!outOfFocus)
			if(screen == GAME_MENU)
				//Underlines Game Menu Buttons
				gameMenu.mouseButtons(e.getX(), e.getY());
	}

	public void mouseDragged(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
		Object source = e.getSource();
		if(!outOfFocus)
		{
			if(source == quit)
				quit.setBackground(Color.RED);
			else if(source == minimize)
				minimize.setBackground(new Color(48, 174, 255));
			else if(source == back)
				back.setBackground(new Color(48, 174, 255));

			if(screen == EDITOR_MENU)
				if(source == editorMenu.createButton)
					editorMenu.createEntered();
				else if(source == editorMenu.editButton)
					editorMenu.editEntered();
				else if(source == editorMenu.playButton)
					editorMenu.playEntered();
		}
		else
			if(source == quit)
				quit.setBackground(Color.RED);
			else if(source == minimize)
				minimize.setBackground(mouseLineColor.darker());
			else if(source == back)
				back.setBackground(mouseLineColor.darker());
			else;
	}

	public void mouseExited(MouseEvent e)
	{
		Object source = e.getSource();
		if(source == quit)
			quit.setBackground(mouseLineColor);
		else if(source == minimize)
			minimize.setBackground(mouseLineColor);
		else if(source == back)
			back.setBackground(mouseLineColor);

		if(screen == EDITOR_MENU)
			if(source == editorMenu.createButton)
				editorMenu.createExited();
			else if(source == editorMenu.editButton)
				editorMenu.editExited();
			else if(source == editorMenu.playButton)
				editorMenu.playExited();
	}

	public void keyPressed(KeyEvent e)
	{
		int k = e.getKeyCode();
		if(k == KeyEvent.VK_BACK_QUOTE)
		{
			if(!consoleOpen)
			{
				//Opens Console
				new Console(this);
				consoleOpen = true;
				log("Opening Console");
			}
		}
		else if(k == KeyEvent.VK_1)
		{
			frame.setLocation(10, 10);
			log("Moving frame to 10x10");
		}
		else if(k == KeyEvent.VK_2)
		{
			frame.setLocation(1930, 10);
			log("Moving frame to 10x10");
		}
	}

	public void keyReleased(KeyEvent e)
	{

	}

	public void setScreen(int screen)
	{
		//Sets the screen and adds the previous screen to the history
		//to allow use of the back button
		screenHistory.add(this.screen);
		this.screen = screen;

		//Shows the back button as long as the screen
		//is not that game menu because it is unnecessary
		if(screen != GAME_MENU)
			setBackButton(true);

		screenCounter++;

		log("Moved to " + getScreenText() + " screen");
	}

	public String getScreenText()
	{
		if(screen == GAME_MENU)
			return "Game Menu";
		else if(screen == ABOUT)
			return "About";
		else if(screen == HELP)
			return "Help";
		else if(screen == EDITOR_MENU)
			return "Editor Menu";
		else
			return "";
	}

	//Serves same function as the standard setScreen() method
	//but is primarily for console functionality
	//COME BACK TO THIS
	public void setScreenManually(int screen)
	{
		screenHistory.add(this.screen);
		this.screen = screen;
		screenCounter++;

		if(screen == ABOUT)
		{
			screenController.show(screenContainer, allScreens[1]);
			setCursor(Cursor.getDefaultCursor());
			setBackButton(true);
		}
		else if(screen == HELP)
		{
			screenController.show(screenContainer, allScreens[2]);
			setCursor(Cursor.getDefaultCursor());
			setBackButton(true);
		}
		else if(screen == EDITOR_MENU)
		{
			screenController.show(screenContainer, allScreens[3]);
			setCursor(Cursor.getDefaultCursor());
			setBackButton(true);
			changeSize(DEFAULT_WIDTH, frame.getHeight() - 30);
		}
		else if(screen == GAME_MENU)
		{
			screenController.show(screenContainer, allScreens[0]);
			setCursor(Cursor.getDefaultCursor());
			setBackButton(false);
			changeSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		}

		log("Moved to " + getScreenText() + " screen");
	}

	//Changes the size of the window after it is already visible
	public void changeSize(Dimension d)
	{
		frame.setPreferredSize(d);
		canvas = frame.getContentPane();
		frame.pack();
	}
	public void changeSize(int w, int h)
	{
		Dimension d = new Dimension(w, h);
		frame.setPreferredSize(d);
		canvas = frame.getContentPane();
		frame.pack();
	}

	//Moves back a screen
	public void moveBackScreen()
	{
		screen = screenHistory.get(screenCounter);

		if(screen == GAME_MENU)
		{
			setBackButton(false);
			screenController.show(screenContainer, allScreens[0]);
			changeSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		}
		screenCounter--;


		log("Moved back to " + getScreenText() + " screen");
	}

	//Checks to see if the window gains or loses focus
	public void focusGained(FocusEvent e)
	{
		Object source = e.getSource();
		if(source == frame)
		{
			refresh.start();
			mouseLineColor = new Color(0, 150, 250);
			headTitle.setBackground(mouseLineColor);
			buttonPane.setBackground(mouseLineColor);
			headPane.setBackground(mouseLineColor);
			quit.setBackground(mouseLineColor);
			minimize.setBackground(mouseLineColor);
			back.setBackground(mouseLineColor);

			outOfFocus = false;

			log("Frame gained focus");
		}
	}

	public void focusLost(FocusEvent e)
	{
		Object source = e.getSource();
		if(source == frame)
		{
			refresh.stop();
			mouseLineColor = new Color(220, 220, 220);
			headTitle.setBackground(mouseLineColor);
			buttonPane.setBackground(mouseLineColor);
			headPane.setBackground(mouseLineColor);
			quit.setBackground(mouseLineColor);
			minimize.setBackground(mouseLineColor);
			back.setBackground(mouseLineColor);
			outOfFocus = true;
			Runtime.getRuntime().gc();

			log("Frame lost focus");
		}
	}

	//Sets the back button's visibility
	public void setBackButton(boolean b)
	{
		back.setVisible(b);
	}

	public void log(String logTXT)
	{
		try
		{
			logWriter.write(logTXT + "\n");
			logWriter.flush();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void closerLogger()
	{
		try {
			logWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private WindowListener closer()
	{
		return new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				log("Exiting");
				log("-------");
				closerLogger();
				System.exit(0);
			}
		};
	}

	//Unused Listener Functions
	public void mouseClicked(MouseEvent e){}
	public void keyTyped(KeyEvent e){}
}