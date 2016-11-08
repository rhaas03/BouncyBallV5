package Editor;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import BouncyBallSource.BouncyBallV5;
import Editor.Dialogs.*;
import resources.objects.*;

import java.io.*;
import java.util.ArrayList;

public class CustomEditor extends JPanel implements ActionListener, MouseListener, MouseMotionListener
{
	public BouncyBallV5 bb5;

	public JFrame frame;
	private Container canvas;

	private Timer repaintTimer = new Timer(1000/60, this);

	//Window Dimensions
	private int windowWidth = 500;
	private int windowHeight = 500;

	//File and Save Info
	private String docAbsoluteName = "Untitled";
	private String docName = "Untitled";
	private File docLocation;
	private boolean firstSave = true;

	private boolean changedName = false;

	//Type ints
	private int type = -1;
	public static final int NEW_LEVEL = 0;
	public static final int EDIT_LEVEL = 1;

	//Menu and Options
	private JMenuBar menu = new JMenuBar();
	private JMenu file = new JMenu("File");
	private JMenu edit = new JMenu("Edit");
	private JMenu objects = new JMenu("Objects");
	private JMenu runMenu = new JMenu("Run");
	private JMenu window = new JMenu("Window");
	private JMenu helpMenu = new JMenu("Help");

	//File Menu Items
	private JMenuItem newFile = new JMenuItem("New");
	private JMenuItem openFile = new JMenuItem("Open");
	private JMenuItem saveFile = new JMenuItem("Save");
	private JMenuItem saveFileAs = new JMenuItem("Save As");
	private JMenuItem exportLvl = new JMenuItem("Export Level");
	private JMenuItem backToBB = new JMenuItem("Return to Editor Menu");
	private JMenuItem exit = new JMenuItem("Exit");

	//Edit Menu Items
	private JMenuItem dimension = new JMenuItem("Level Dimensions");
	private JMenuItem renameItem = new JMenuItem("Rename");
	private JMenuItem clearAll = new JMenuItem("Clear All Objects");

	//Objects Menu Items
	private JMenuItem setBall = new JMenuItem("Set Ball Location");
	private JMenuItem addStarItem = new JMenuItem("Add New Star");
	private JMenuItem addPlatformItem = new JMenuItem("Add New Platform");
	private JMenuItem addTextBoxItem = new JMenuItem("Add New Text Box");

	//Run Menu Items
	private JMenuItem test = new JMenuItem("Test Level");

	//Window Menu Items
	private JCheckBoxMenuItem showGridItem = new JCheckBoxMenuItem("Show Grid", true);
	private JCheckBoxMenuItem lockScreen = new JCheckBoxMenuItem("Lock Dimensions", false);

	//Controls whether the grid is showing or not
	private boolean showGrid = true;

	//Level Objects
	private ArrayList<Object> allObjects = new ArrayList<Object>();
	private ArrayList<Object> savedObjects = new ArrayList<Object>();

	private Dimension savedDim;

	//For Moving Objects
	private boolean objectSelected = false;
	private int objectIndex = -1;
	private int preObjectX;
	private int preObjectY;

	private JLabel objectTypeLabel = new JLabel("Object: ");
	private JLabel objectXLabel = new JLabel("X: ");
	private JLabel objectYLabel = new JLabel("Y: ");
	private JLabel platformTypeLabel = new JLabel("Platform: ");
	private JLabel mouseCoordLabel = new JLabel();

	//Mouse Menu Items
	private JPopupMenu mouseMenu = new JPopupMenu();
	private JMenuItem deleteObj = new JMenuItem("Delete");
	private JMenuItem objProps = new JMenuItem("Properties");
	private JMenuItem bringToFront = new JMenuItem("Bring to Front");
	private JMenuItem sendToBack = new JMenuItem("Send to Back");
	private JMenuItem moveForward = new JMenuItem("Move Forward");
	private JMenuItem moveBack = new JMenuItem("Move Back");

	//Add Object Mouse Menu
	private JPopupMenu objectMouseMenu = new JPopupMenu();
	private JMenuItem mouseAddPlat = new JMenuItem("Add Platform");
	private JMenuItem mouseAddStar = new JMenuItem("Add Star");
	private JMenuItem mouseSetBall = new JMenuItem("Set Ball Location");
	private JMenuItem mouseAddTextBox = new JMenuItem("Add Text Box");

	//Ball Index
	private int ballIndex = -1;

	public CustomEditor(int type, BouncyBallV5 b)
	{
		basicOps(type, b);

		/*TextBox tb = new TextBox("Welcome to BouncyBall !\n"
				+ "Use A or the left arrow to move left\n"
				+ "Use D or the right arrow to move right", 0, 100);
		tb.setInsets(new Insets(0, 10, 0, 100));
		tb.setAlignment(TextBox.CENTER);
		//tb.setFont(bb5.blackTuesday.deriveFont(26f));
		allObjects.add(tb);*/

		frame.pack();
		frame.setVisible(true);
		firstSave = true;
		repaintTimer.start();

		bb5.log("Csutom Level environment setup completed");
	}

	public CustomEditor(int type, BouncyBallV5 b, File objFile)
	{
		basicOps(type, b);
		frame.pack();
		frame.setVisible(true);
		repaintTimer.start();

		if(this.type == EDIT_LEVEL)
		{
			try
			{
				FileInputStream fis = new FileInputStream(objFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				CustomSaver cs = (CustomSaver)ois.readObject();
				allObjects = cs.getAllObjects();

				savedObjects = new ArrayList<Object>(allObjects.size());

				for(Object obj: allObjects)
				{
					if(obj instanceof Platform)
						savedObjects.add(((Platform)obj).clone());
					else if(obj instanceof BouncyBall)
						savedObjects.add(((BouncyBall)obj).clone());
					else if(obj instanceof StarShape)
						savedObjects.add(((StarShape)obj).clone());
					else if(obj instanceof TextBox)
						savedObjects.add(((TextBox)obj).clone());
				}

				setPreferredSize(cs.getSize());
				savedDim = getPreferredSize();
				canvas = frame.getContentPane();
				frame.pack();

				docLocation = objFile;
				docAbsoluteName = objFile.getAbsolutePath();
				docName = objFile.getName().substring(0, objFile.getName().lastIndexOf(".cbbl"));
				frame.setTitle("BouncyBall Editor - " + docAbsoluteName);

				firstSave = false;

				ois.close();
				fis.close();
			} catch(Exception e)
			{
				System.err.println("Error Loading File");
				bb5.log("Error Loading File");
				JOptionPane.showMessageDialog(frame, "Could Not Load Level", "Error", JOptionPane.ERROR_MESSAGE);
				firstSave = true;
			}
		}
		bb5.log("Csutom Level environment setup completed");
	}

	private void basicOps(int type, BouncyBallV5 b)
	{
		this.type = type;
		bb5 = b;

		frame = new JFrame("BouncyBall Editor - " + docAbsoluteName);
		frame.addWindowListener(closer());
		frame.addComponentListener(resizing());
		frame.setLocation(bb5.frame.getLocationOnScreen());
		frame.setIconImage(bb5.fav.getImage());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(windowWidth, windowHeight));
		addMouseListener(this);
		addMouseMotionListener(this);

		canvas = frame.getContentPane();
		canvas.add(this);

		setLF();
		createMenu();
		addStatusBar();

		if(System.getProperty("os.name").contains("Mac"))
			frame.setMinimumSize(new Dimension(250, 200));
		else
			frame.setMinimumSize(new Dimension(menu.getPreferredSize().width, 200));

		savedDim = getPreferredSize();
	}

	private void createMenu()
	{
		//Adds MenuBar to frame, and Menu Items to Menu Bar
		frame.setJMenuBar(menu);
		menu.add(file);
		menu.add(edit);
		menu.add(objects);
		menu.add(runMenu);
		menu.add(window);
		menu.add(helpMenu);

		//Add Items to File
		file.add(newFile);
		file.add(openFile);
		file.addSeparator();
		file.add(saveFile);
		file.add(saveFileAs);
		file.add(exportLvl);
		file.addSeparator();
		file.add(backToBB);
		file.add(exit);

		//Add Items to Edit
		edit.add(dimension);
		edit.add(renameItem);
		edit.addSeparator();
		edit.add(clearAll);

		//Add Items to Objects
		objects.add(setBall);
		objects.add(addStarItem);
		objects.add(addPlatformItem);
		objects.add(addTextBoxItem);

		//Add Items to Run
		runMenu.add(test);

		//Add Items to Window
		window.add(showGridItem);
		window.add(lockScreen);

		//Add Items to Mouse Menu
		mouseMenu.add(objProps);
		mouseMenu.add(deleteObj);
		mouseMenu.addSeparator();
		mouseMenu.add(bringToFront);
		mouseMenu.add(sendToBack);
		mouseMenu.add(moveForward);
		mouseMenu.add(moveBack);

		//Add Items to Object Mouse Menu
		objectMouseMenu.add(mouseSetBall);
		objectMouseMenu.add(mouseAddPlat);
		objectMouseMenu.add(mouseAddStar);
		objectMouseMenu.add(mouseAddTextBox);

		for(int x = 0; x < menu.getMenuCount(); x++)
		{
			if(menu.getMenu(x).getMenuComponentCount() > 0)
			{
				menu.getMenu(x).getMenuComponent(0).setPreferredSize(new Dimension(250, newFile.getPreferredSize().height));
				menu.getMenu(x).setText(" " + menu.getMenu(x).getText() + " ");
			}
		}

		for(Component item : mouseMenu.getComponents())
			item.setPreferredSize(new Dimension(200, item.getPreferredSize().height));
		for(Component item : objectMouseMenu.getComponents())
			item.setPreferredSize(new Dimension(200, item.getPreferredSize().height));

		addMenuListeners();
		addItemAccelerators();
	}

	private void addMenuListeners()
	{
		//File Items
		newFile.addActionListener(this);
		saveFile.addActionListener(this);
		saveFileAs.addActionListener(this);
		openFile.addActionListener(this);
		exportLvl.addActionListener(this);
		backToBB.addActionListener(this);
		exit.addActionListener(this);

		//Edit Items
		dimension.addActionListener(this);
		clearAll.addActionListener(this);
		renameItem.addActionListener(this);

		//Object Items
		addPlatformItem.addActionListener(this);
		addStarItem.addActionListener(this);
		setBall.addActionListener(this);
		addTextBoxItem.addActionListener(this);

		//Run Items
		test.addActionListener(this);

		//Window Items
		showGridItem.addActionListener(this);
		lockScreen.addActionListener(this);

		//MouseMenu Items
		objProps.addActionListener(this);
		deleteObj.addActionListener(this);
		bringToFront.addActionListener(this);
		sendToBack.addActionListener(this);
		moveForward.addActionListener(this);
		moveBack.addActionListener(this);

		//Object MouseMenu Items
		mouseAddPlat.addActionListener(this);
		mouseAddStar.addActionListener(this);
		mouseSetBall.addActionListener(this);
		mouseAddTextBox.addActionListener(this);
	}

	private void addItemAccelerators()
	{
		//Command Key on Mac
		final int cmdKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		if(System.getProperty("os.name").contains("Mac"))
		{
			//File Accelerators
			newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, cmdKey));
			openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, cmdKey));
			saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, cmdKey));
			saveFileAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK + cmdKey));
			exportLvl.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.SHIFT_MASK + cmdKey));

			//Object Accelerators
			addPlatformItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, cmdKey));
			addStarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, cmdKey));
			setBall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, cmdKey));
			addTextBoxItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, cmdKey));

			//Run Accelerator
			test.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, cmdKey));

			//Window Accelerators
			showGridItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, cmdKey));
			lockScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, cmdKey));
		}
		else
		{
			//File Accelerators
			newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
			saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			saveFileAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
			exportLvl.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));

			//Object Accelerators
			addPlatformItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
			addStarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
			setBall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
			addTextBoxItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));

			//Run Accelerator
			test.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, ActionEvent.CTRL_MASK));

			//Window Accelerators
			showGridItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
			lockScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		}

		//Mouse Object Accelerators
		mouseAddPlat.setAccelerator(addPlatformItem.getAccelerator());
		mouseAddStar.setAccelerator(addStarItem.getAccelerator());
		mouseSetBall.setAccelerator(setBall.getAccelerator());
	}

	private void addStatusBar()
	{
		int inset = 2;

		JPanel statusPane = new JPanel();
		statusPane.setLayout(new BoxLayout(statusPane, BoxLayout.LINE_AXIS));
		statusPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createBevelBorder(BevelBorder.LOWERED),
				new EmptyBorder(inset, inset, inset, inset)));

		Font stautsLabelFont = new Font("", Font.PLAIN, 12);
		objectTypeLabel.setFont(stautsLabelFont);
		objectXLabel.setFont(stautsLabelFont);
		objectYLabel.setFont(stautsLabelFont);
		platformTypeLabel.setFont(stautsLabelFont);
		mouseCoordLabel.setFont(stautsLabelFont);

		mouseCoordLabel.setText("0, 0px");
		platformTypeLabel.setVisible(false);

		statusPane.add(objectTypeLabel);
		statusPane.add(new JSeparator(JSeparator.VERTICAL));
		statusPane.add(Box.createHorizontalBox());
		statusPane.add(objectXLabel);
		statusPane.add(new JSeparator(JSeparator.VERTICAL));
		statusPane.add(Box.createHorizontalBox());
		statusPane.add(objectYLabel);
		statusPane.add(new JSeparator(JSeparator.VERTICAL));
		statusPane.add(Box.createHorizontalBox());
		statusPane.add(platformTypeLabel);
		statusPane.add(Box.createHorizontalGlue());
		statusPane.add(mouseCoordLabel);

		for(int x = 0; x < statusPane.getComponentCount(); x++)
			((JComponent)statusPane.getComponent(x)).setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

		frame.add(statusPane, BorderLayout.SOUTH);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		if(showGrid)
		{
			Stroke defaultS = g2d.getStroke();
			Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{7}, 0);
			g2d.setStroke(dashed);
			g2d.setFont(new Font("", Font.BOLD, 12));

			for(int x = 0; x <= getWidth(); x+=50)
			{
				g2d.setColor(Color.GRAY);
				g2d.drawLine(x - 1, 0, x - 1, getHeight());
				g2d.setColor(Color.BLACK);
				FontMetrics fm = g2d.getFontMetrics();
				g2d.drawString(Integer.toString(x), x - fm.stringWidth(Integer.toString(x)) - 5, 12);
			}
			for(int y = 0; y <= getHeight(); y+=50)
			{
				g2d.setColor(Color.GRAY);
				g2d.drawLine(0, y - 1, getWidth(), y - 1);
				g2d.setColor(Color.BLACK);
				g2d.drawString(Integer.toString(y), 2, y - 5);
			}

			g2d.setStroke(defaultS);
		}

		/*for(Platform p : plats)
			p.drawPlatform(g2d);*/
		for(Object o : allObjects)
		{
			if(o instanceof Platform)
				((Platform) o).drawPlatform(g2d);
			else if(o instanceof StarShape)
			{
				StarShape s = (StarShape)o;
				g2d.setColor(Color.YELLOW);
				g2d.fill(s.getShape());
				g2d.setColor(Color.BLACK);
				g2d.draw(s.getShape());
			}
			else if(o instanceof BouncyBall)
				((BouncyBall) o).drawBall(g2d);
			else if(o instanceof TextBox)
				((TextBox)o).drawText(g2d);
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == exit)
		{
			if(confirm())
			{
				bb5.log("Exiting");
				bb5.log("-------");
				bb5.closerLogger();
				System.exit(0);
			}
			else
			{
				int result = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Save First?", JOptionPane.YES_NO_CANCEL_OPTION);
				if(result == JOptionPane.YES_OPTION)
				{
					save(!firstSave);

					bb5.log("Exiting");
					bb5.log("-------");
					bb5.closerLogger();
					System.exit(0);
				}
				else if(result == JOptionPane.NO_OPTION)
				{
					bb5.log("Exiting");
					bb5.log("-------");
					bb5.closerLogger();
					System.exit(0);
				}
			}
		}
		else if(source == backToBB)
		{
			if(confirm())
			{
				bb5.frame.setVisible(true);
				bb5.frame.setLocation(frame.getLocationOnScreen());
				frame.dispose();

				bb5.log("Editor Closed");
			}
			else
			{
				int result = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Save First?", JOptionPane.YES_NO_CANCEL_OPTION);
				if(result == JOptionPane.YES_OPTION)
				{
					save(!firstSave);
					bb5.frame.setVisible(true);
					bb5.frame.setLocation(frame.getLocationOnScreen());
					frame.dispose();

					bb5.log("Editor Closed");
				}
				else if(result == JOptionPane.NO_OPTION)
				{
					bb5.frame.setVisible(true);
					bb5.frame.setLocation(frame.getLocationOnScreen());
					frame.dispose();

					bb5.log("Editor Closed");
				}
			}
		}
		else if(source == showGridItem)
			showGrid = !showGrid;
		else if(source == lockScreen)
		{
			frame.setResizable(!lockScreen.isSelected());
			if(System.getProperty("os.name").contains("Windows"))
				if(frame.isResizable())
					changeSize(windowWidth + 10, windowHeight + 10);
				else
					changeSize(windowWidth - 10, windowHeight - 10);

			bb5.log("Screen Locked");
		}
		else if(source == clearAll)
			clearObjects();
		else if(source == newFile)
		{
			bb5.frame.setVisible(true);
			bb5.frame.setLocation(frame.getLocationOnScreen());
			frame.dispose();
			new CustomEditor(NEW_LEVEL, bb5);
			bb5.frame.setVisible(false);

			bb5.log("New Level Editor created");
		}
		else if(source == dimension)
			createSizeDialog();
		else if(source == repaintTimer)
		{
			repaint();
			if(!confirm() && !changedName)
			{
				frame.setTitle("BouncyBall Editor - *" + docAbsoluteName);
				changedName = true;
			}
			else if(confirm() && changedName)
				frame.setTitle("BouncyBall Editor - " + docAbsoluteName);

		}
		else if(source == deleteObj)
		{
			if(allObjects.get(objectIndex) instanceof BouncyBall)
				ballIndex = -1;

			allObjects.remove(objectIndex);

			objectIndex = -1;
			objectSelected = false;
			updateStatus();

			bb5.log("Object deleted");
		}
		else if(source == bringToFront)
		{
			if(allObjects.size() > 1)
			{
				Object frontObject = allObjects.get(objectIndex);
				Object placeHolder = allObjects.get(allObjects.size() - 1);
				allObjects.set(allObjects.size() - 1, frontObject);
				allObjects.set(objectIndex, placeHolder);

				bb5.log("Object brought to front");
			}
		}
		else if(source == sendToBack)
		{
			if(allObjects.size() > 1)
			{
				Object lastObject = allObjects.get(objectIndex);
				Object placeHolder = allObjects.get(0);
				allObjects.set(0, lastObject);
				allObjects.set(objectIndex, placeHolder);

				bb5.log("Object sent to back");
			}
		}
		else if(source == moveForward)
		{
			if(allObjects.size() > 1 && objectIndex < allObjects.size() - 1)
			{
				Object forwardObject = allObjects.get(objectIndex);
				Object placeHolder = allObjects.get(objectIndex + 1);
				allObjects.set(objectIndex + 1, forwardObject);
				allObjects.set(objectIndex, placeHolder);

				bb5.log("Object moved forward");
			}
		}
		else if(source == moveBack)
		{
			if(allObjects.size() > 1)
			{
				Object backObject = allObjects.get(objectIndex);
				Object placeHolder = allObjects.get(objectIndex - 1);
				allObjects.set(objectIndex - 1, backObject);
				allObjects.set(objectIndex, placeHolder);

				bb5.log("Object moved back");
			}
		}
		else if(source == addPlatformItem || source == mouseAddPlat)
			new PlatformDialog(this);
		else if(source == objProps)
		{
			if(allObjects.get(objectIndex) instanceof Platform)
			{
				Platform p = (Platform)allObjects.get(objectIndex);
				new PlatformDialog(this, PlatformDialog.PROPS, p, objectIndex);
			}
			else if(allObjects.get(objectIndex) instanceof StarShape)
			{
				StarShape s = (StarShape)allObjects.get(objectIndex);
				new StarDialog(this, StarDialog.PROPS, s, objectIndex);
			}
			else if(allObjects.get(objectIndex) instanceof BouncyBall)
			{
				BouncyBall b = (BouncyBall)allObjects.get(objectIndex);
				new BallDialog(this, BallDialog.PROPS, b, objectIndex);
			}
			else if(allObjects.get(objectIndex) instanceof TextBox)
			{
				TextBox tb = (TextBox)allObjects.get(objectIndex);
				new TextDialog(this, TextDialog.PROPS, tb, objectIndex);
			}
		}
		else if(source == addStarItem || source == mouseAddStar)
			new StarDialog(this);
		else if(source == setBall || source == mouseSetBall)
			new BallDialog(this);
		else if(source == test)
			new LevelTester(allObjects.toArray(), getSize(), frame.getLocation());
		else if(source == saveFile)
			save(false);
		else if(source == saveFileAs)
			save(true);
		else if(source == openFile)
			open();
		else if(source == addTextBoxItem || source == mouseAddTextBox)
			new TextDialog(this);
		else if(source == renameItem)
		{
			if(!firstSave)
				createRenameDialog();
			else
				JOptionPane.showMessageDialog(frame, "File is yet to have been saved", "Rename Unavailable", JOptionPane.WARNING_MESSAGE);
		}
		else if(source == exportLvl)
		{
			boolean ballExists = false;
			boolean starExists = false;
			for(Object o : allObjects)
			{
				if(o instanceof BouncyBall)
					ballExists = true;
				if(o instanceof StarShape)
					starExists = true;
				if(starExists && ballExists)
					break;
			}

			if(ballExists && starExists)
				if(!firstSave)
					export();
				else
					JOptionPane.showMessageDialog(frame, "Please Save First", "Error: Save", JOptionPane.ERROR_MESSAGE);
			else
				JOptionPane.showMessageDialog(frame, "Missing Export Requirements: Ball must exist, and must have at least one star present",
						"Missing Export Requirements", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void save(boolean saveAs)
	{
		if(firstSave || saveAs)
		{
			if(System.getProperty("os.name").contains("Mac"))
			{
				FileDialog fd = new FileDialog(frame, "Save", FileDialog.SAVE);
				fd.setModalityType(ModalityType.APPLICATION_MODAL);
				fd.setFilenameFilter(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".cbbl");
					}
				});
				if(saveAs)
					fd.setFile(docLocation.getName());
				fd.setModalityType(ModalityType.APPLICATION_MODAL);
				fd.setVisible(true);
				if(!("" + fd.getDirectory() + fd.getFile()).equals("nullnull"))
				{
					String filePath = fd.getDirectory() + fd.getFile();

					//Adds extension if not already present
					if(!filePath.endsWith(".cbbl"))
						filePath += ".cbbl";

					//Writes to output file
					try
					{
						File f = new File(filePath);
						if(!f.exists())
							f.createNewFile();
						FileOutputStream fos = new FileOutputStream(f);
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(new CustomSaver(allObjects, getSize()));
						oos.close();

						firstSave = false;
						changedName = false;
						docLocation = new File(fd.getFile());
						docAbsoluteName = f.getAbsolutePath();
						docName = fd.getFile();

						savedObjects = new ArrayList<Object>(allObjects.size());

						for(Object obj: allObjects)
						{
							if(obj instanceof Platform)
								savedObjects.add(((Platform)obj).clone());
							else if(obj instanceof BouncyBall)
								savedObjects.add(((BouncyBall)obj).clone());
							else if(obj instanceof StarShape)
								savedObjects.add(((StarShape)obj).clone());
							else if(obj instanceof TextBox)
								savedObjects.add(((TextBox)obj).clone());
						}

						bb5.log("Level saved");
					} catch(Exception e)
					{
						bb5.log("Error saving level");
						e.printStackTrace();
					}

					frame.setTitle("BouncyBall Editor - " + docAbsoluteName);
				}
			}
			else
			{
				JFileChooser jfc = new JFileChooser();
				jfc.setFileFilter(new FileNameExtensionFilter("Custom Bouncy Ball Level(.cbbl)", "CBBL"));
				jfc.setSelectedFile(new File(docAbsoluteName));
				int returnVal = jfc.showSaveDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					String filePath = jfc.getSelectedFile().toString();

					//Adds extension if not already present
					if(!filePath.endsWith(".cbbl"))
						filePath += ".cbbl";

					//Writes to output file
					try
					{
						File f = new File(filePath);
						if(!f.exists())
							f.createNewFile();
						FileOutputStream fos = new FileOutputStream(f);
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(new CustomSaver(allObjects, getSize()));
						oos.close();


						firstSave = false;
						changedName = false;
						docLocation = jfc.getSelectedFile();
						docAbsoluteName = f.getAbsolutePath();
						docName = jfc.getSelectedFile().getAbsolutePath();

						savedObjects = new ArrayList<Object>(allObjects.size());

						for(Object obj: allObjects)
						{
							if(obj instanceof Platform)
								savedObjects.add(((Platform)obj).clone());
							else if(obj instanceof BouncyBall)
								savedObjects.add(((BouncyBall)obj).clone());
							else if(obj instanceof StarShape)
								savedObjects.add(((StarShape)obj).clone());
							else if(obj instanceof TextBox)
								savedObjects.add(((TextBox)obj).clone());
						}

						bb5.log("Level saved");
					} catch(Exception e)
					{
						bb5.log("Error saving level");
						e.printStackTrace();
					}

					frame.setTitle("BouncyBall Editor - " + docAbsoluteName);
				}
			}
		}
		else
		{
			try
			{
				File f = new File(docAbsoluteName);
				FileOutputStream fos = new FileOutputStream(f);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(new CustomSaver(allObjects, getSize()));
				oos.close();

				changedName = false;
				savedObjects = new ArrayList<Object>(allObjects.size());

				for(Object obj: allObjects)
					if(obj instanceof Platform)
						savedObjects.add(((Platform)obj).clone());
					else if(obj instanceof BouncyBall)
						savedObjects.add(((BouncyBall)obj).clone());
					else if(obj instanceof StarShape)
						savedObjects.add(((StarShape)obj).clone());
					else if(obj instanceof TextBox)
						savedObjects.add(((TextBox)obj).clone());

				frame.setTitle("BouncyBall Editor - " + docAbsoluteName);

				bb5.log("Level saved");
			} catch(Exception e)
			{
				bb5.log("Error saving level");
				e.printStackTrace();
			}
		}
	}

	private void export()
	{
		if(System.getProperty("os.name").contains("Mac"))
		{
			FileDialog fd = new FileDialog(frame, "Save", FileDialog.SAVE);
			fd.setModalityType(ModalityType.APPLICATION_MODAL);
			fd.setFilenameFilter(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".bb5");
				}
			});
			fd.setFile(docLocation.getAbsolutePath() + ".bb5");
			fd.setModalityType(ModalityType.APPLICATION_MODAL);
			fd.setVisible(true);
			if(!("" + fd.getDirectory() + fd.getFile()).equals("nullnull"))
			{
				String filePath = fd.getDirectory() + fd.getFile();

				//Adds extension if not already present
				if(!filePath.endsWith(".bb5"))
					filePath += ".bb5";

				//Writes to output file
				try
				{
					File f = new File(filePath);
					if(!f.exists())
						f.createNewFile();
					FileOutputStream fos = new FileOutputStream(f);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(new CustomSaver(allObjects, getSize()));
					oos.close();

					bb5.log("Level exported");
				} catch(Exception e)
				{
					bb5.log("Error exporting level");
					e.printStackTrace();
				}
			}
		}
		else
		{
			System.out.println("ebet");
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new FileNameExtensionFilter("BouncyBall 5(.bb5)", "BB5"));
			jfc.setSelectedFile(new File(docName));
			int returnVal = jfc.showSaveDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				String filePath = jfc.getSelectedFile().toString();

				//Adds extension if not already present
				if(!filePath.endsWith(".bb5"))
					filePath += ".bb5";

				//Writes to output file
				try
				{
					File f = new File(filePath);
					if(!f.exists())
						f.createNewFile();
					FileOutputStream fos = new FileOutputStream(f);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(new CustomSaver(allObjects, getSize()));
					oos.close();
					bb5.log("Level exported");
				} catch(Exception e)
				{
					bb5.log("Error exporting level");
					e.printStackTrace();
				}
			}
		}
	}


	private void open()
	{
		if(System.getProperty("os.name").contains("Mac"))
		{
			FileDialog fd = new FileDialog(frame, "Open", FileDialog.LOAD);
			fd.setModalityType(ModalityType.APPLICATION_MODAL);
			fd.setFilenameFilter(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".cbbl");
				}
			});
			fd.setVisible(true);

			if(!("" + fd.getDirectory() + fd.getFile()).equals("nullnull"))
			{
				bb5.frame.setVisible(true);
				bb5.frame.setLocation(frame.getLocationOnScreen());
				frame.dispose();
				new CustomEditor(EDIT_LEVEL, bb5, new File(fd.getDirectory() + fd.getFile()));
				bb5.frame.setVisible(false);

				bb5.log("Opening Level");
			}
		}
		else
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new FileNameExtensionFilter("Custom Bouncy Ball Level(.cbbl)", "CBBL"));
			int returnVal = jfc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				bb5.frame.setVisible(true);
				bb5.frame.setLocation(frame.getLocationOnScreen());
				frame.dispose();
				new CustomEditor(EDIT_LEVEL, bb5, jfc.getSelectedFile());
				bb5.frame.setVisible(false);

				bb5.log("Opening Level");
			}
		}
	}

	private void setLF()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void clearObjects()
	{
		allObjects.clear();
		objectIndex = -1;
		ballIndex = -1;

		bb5.log("Objects cleared");
	}

	public void mousePressed(MouseEvent e)
	{
		if(mouseMenu.isShowing())
			mouseMenu.setVisible(false);
		if(objectMouseMenu.isShowing())
			objectMouseMenu.setVisible(false);

		//Checks if an object has been pressed on, starting from the end of the array
		//in order to get the most recently added object
		for(int x = allObjects.size() - 1; x >= 0; x--)
		{
			if(allObjects.get(x) instanceof Platform)
			{
				Platform p = (Platform)allObjects.get(x);
				if(mouseShapeCol(e.getX(), e.getY(), p.getShape()))
				{
					objectSelected = true;
					objectIndex = x;

					if(e.getButton() == MouseEvent.BUTTON1)
					{
						preObjectX = p.getX() - e.getX();
						preObjectY = p.getY() - e.getY();
					}

					break;
				}
				else
				{
					objectSelected = false;
					objectIndex = -1;
				}
			}
			else if(allObjects.get(x) instanceof StarShape)
			{
				StarShape s = (StarShape)allObjects.get(x);
				if(mouseShapeCol(e.getX(), e.getY(), s.getShape()))
				{
					objectSelected = true;
					objectIndex = x;

					if(e.getButton() == MouseEvent.BUTTON1)
					{
						preObjectX = s.getX() - e.getX();
						preObjectY = s.getY() - e.getY();
					}

					break;
				}
				else
				{
					objectSelected = false;
					objectIndex = -1;
				}
			}
			else if(allObjects.get(x) instanceof BouncyBall)
			{
				BouncyBall b = (BouncyBall)allObjects.get(x);
				if(mouseShapeCol(e.getX(), e.getY(), b.getShape()))
				{
					objectSelected = true;
					objectIndex = x;

					if(e.getButton() == MouseEvent.BUTTON1)
					{
						preObjectX = b.getX() - e.getX();
						preObjectY = b.getY() - e.getY();
					}

					break;
				}
				else
				{
					objectSelected = false;
					objectIndex = -1;
				}
			}
			else if(allObjects.get(x) instanceof TextBox)
			{
				TextBox tb = (TextBox)allObjects.get(x);
				if(mouseShapeCol(e.getX(), e.getY(), tb.getShape()))
				{
					objectSelected = true;
					objectIndex = x;

					if(e.getButton() == MouseEvent.BUTTON1)
					{
						preObjectX = tb.getX() - e.getX();
						preObjectY = tb.getY() - e.getY();
					}

					break;
				}
				else
				{
					objectSelected = false;
					objectIndex = -1;
				}
			}
		}

		if(objectSelected && (e.getButton() == MouseEvent.BUTTON3 ||
				(e.getButton() == MouseEvent.BUTTON1 && e.isControlDown() && System.getProperty("os.name").contains("Mac"))))
			mouseMenu.show(e.getComponent(), e.getX(), e.getY());
		else if(e.getButton() == MouseEvent.BUTTON3 ||
				(e.getButton() == MouseEvent.BUTTON1 && e.isControlDown() && System.getProperty("os.name").contains("Mac")))
			objectMouseMenu.show(e.getComponent(), e.getX(), e.getY());

		updateStatus();
	}

	public void mouseReleased(MouseEvent e){
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			objectSelected = false;
			//objectIndex = -1;
		}
	}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}

	public void mouseMoved(MouseEvent e)
	{
		mouseCoordLabel.setText(e.getX() + ", " + e.getY() + "px");
	}

	public void mouseDragged(MouseEvent e)
	{
		if(SwingUtilities.isLeftMouseButton(e))
		{
			//Moves Objects
			if(objectSelected)
			{
				if(allObjects.get(objectIndex) instanceof Platform)
				{
					Platform p = (Platform)allObjects.get(objectIndex);
					p.setLocation(preObjectX + e.getX(), preObjectY + e.getY());
					preObjectX = p.getX() - e.getX();
					preObjectY = p.getY() - e.getY();
				}
				else if(allObjects.get(objectIndex) instanceof StarShape)
				{
					StarShape s = (StarShape)allObjects.get(objectIndex);
					s.setLocation(preObjectX + e.getX(), preObjectY + e.getY());
					preObjectX = e.getX();
					preObjectY = e.getY();
				}
				else if(allObjects.get(objectIndex) instanceof BouncyBall)
				{
					BouncyBall b = (BouncyBall)allObjects.get(objectIndex);
					b.setLocation(preObjectX + e.getX(), preObjectY + e.getY());
					preObjectX = b.getX() - e.getX();
					preObjectY = b.getY() - e.getY();
				}
				else if(allObjects.get(objectIndex) instanceof TextBox)
				{
					TextBox tb = (TextBox)allObjects.get(objectIndex);
					tb.setLocation(preObjectX + e.getX(), preObjectY + e.getY());
					preObjectX = tb.getX() - e.getX();
					preObjectY = tb.getY() - e.getY();
				}

				updateStatus();
			}
		}
		mouseCoordLabel.setText(e.getX() + ", " + e.getY() + "px");
	}

	public void updateStatus()
	{
		if(objectSelected)
		{
			if(allObjects.get(objectIndex) instanceof Platform)
			{
				objectTypeLabel.setText("Object: Platform");
				objectXLabel.setText("X: " + ((Platform)allObjects.get(objectIndex)).getX() + "px");
				objectYLabel.setText("Y: " + ((Platform)allObjects.get(objectIndex)).getY() + "px");
				platformTypeLabel.setVisible(true);
				platformTypeLabel.setText("Platform: " + ((Platform)allObjects.get(objectIndex)).getTypeString());
			}
			else if(allObjects.get(objectIndex) instanceof StarShape)
			{
				objectTypeLabel.setText("Object: Star");
				objectXLabel.setText("X: " + ((StarShape)allObjects.get(objectIndex)).getX()/2 + "px");
				objectYLabel.setText("Y: " + ((StarShape)allObjects.get(objectIndex)).getY()/2 + "px");
				platformTypeLabel.setVisible(false);
			}
			else if(allObjects.get(objectIndex) instanceof BouncyBall)
			{
				objectTypeLabel.setText("Object: Ball");
				objectXLabel.setText("X: " + ((BouncyBall)allObjects.get(objectIndex)).getX() + "px");
				objectYLabel.setText("Y: " + ((BouncyBall)allObjects.get(objectIndex)).getY() + "px");
				platformTypeLabel.setVisible(false);
			}
			else if(allObjects.get(objectIndex) instanceof TextBox)
			{
				objectTypeLabel.setText("Object: Text Box");
				objectXLabel.setText("X: " + ((TextBox)allObjects.get(objectIndex)).getX() + "px");
				objectYLabel.setText("Y: " + ((TextBox)allObjects.get(objectIndex)).getY() + "px");
				platformTypeLabel.setVisible(false);
			}
		}
		else
		{
			objectTypeLabel.setText("Object: ");
			objectXLabel.setText("X: ");
			objectYLabel.setText("Y: ");
			platformTypeLabel.setVisible(false);
			platformTypeLabel.setText("Platform: ");
		}
	}

	private boolean mouseShapeCol(int mouseX, int mouseY, Shape obj)
	{
		Rectangle mouseRect = new Rectangle(mouseX, mouseY, 1, 1);
		Area mA = new Area(mouseRect);
		Area oA = new Area(obj);
		mA.intersect(oA);
		return !mA.isEmpty();
	}

	public void addObject(Object obj)
	{
		if(obj instanceof Platform)
		{
			Platform p = (Platform)obj;
			allObjects.add(p);
		}
		else if(obj instanceof StarShape)
		{
			StarShape s = (StarShape)obj;
			s.setLocation(s.getX() * 2, s.getY() * 2);
			allObjects.add(s);
		}
		else if(obj instanceof BouncyBall)
		{
			if(ballIndex == -1)
			{
				BouncyBall b = (BouncyBall)obj;
				allObjects.add(b);
				ballIndex = allObjects.lastIndexOf(b);
			}
			else
			{
				BouncyBall b = (BouncyBall)obj;
				setObject(ballIndex, b);
			}
		}
		else if(obj instanceof TextBox)
		{
			TextBox tb = (TextBox)obj;
			allObjects.add(tb);
		}

		bb5.log("Added Object");
	}

	public void setObject(int oIndex, Object obj)
	{
		if(obj instanceof Platform)
		{
			Platform p = (Platform)obj;
			allObjects.set(oIndex, p);
		}
		else if(obj instanceof StarShape)
		{
			StarShape s = (StarShape)obj;
			s.setLocation(s.getX()*2, s.getY()*2);
			allObjects.set(oIndex, s);
		}
		else if(obj instanceof BouncyBall)
		{
			BouncyBall b = (BouncyBall)obj;
			allObjects.set(oIndex, b);
		}
		else if(obj instanceof TextBox)
		{
			TextBox tb = (TextBox)obj;
			allObjects.set(oIndex, tb);
		}

		bb5.log("Object changed");
	}

	private WindowListener closer()
	{
		return new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if(confirm())
				{
					repaintTimer.stop();
					repaintTimer = null;
					bb5.frame.setVisible(true);
					frame.setVisible(true);
					bb5.frame.setLocation(frame.getLocationOnScreen());
					frame.dispose();
					bb5.log("Closing Editor");
				}
				else
				{
					int result = JOptionPane.showConfirmDialog(frame, "Would you like to save first?", "Save First?", JOptionPane.YES_NO_CANCEL_OPTION);
					if(result == JOptionPane.YES_OPTION)
					{
						save(!firstSave);
						repaintTimer.stop();
						repaintTimer = null;
						bb5.frame.setVisible(true);
						frame.setVisible(true);
						bb5.frame.setLocation(frame.getLocationOnScreen());
						frame.dispose();
						bb5.log("Closing Editor");
					}
					else if(result == JOptionPane.NO_OPTION)
					{
						repaintTimer.stop();
						repaintTimer = null;
						bb5.frame.setVisible(true);
						frame.setVisible(true);
						bb5.frame.setLocation(frame.getLocationOnScreen());
						frame.dispose();
						bb5.log("Closing Editor");
					}
				}
			}
		};
	}

	private ComponentListener resizing()
	{
		return new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				windowWidth = getWidth();
				windowHeight = getHeight();
			}
		};
	}

	private boolean confirm()
	{
		boolean confirm = false;

		if(allObjects.size() != savedObjects.size() || !getSize().equals(savedDim))
			confirm = true;

		if(confirm != true)
		{
			for(int x = 0; x < allObjects.size(); x++)
			{
				if(!allObjects.get(x).toString().equals(savedObjects.get(x).toString()))
				{
					confirm = true;
					break;
				}
			}
		}

		return !confirm;
	}

	private void changeSize(int w, int h)
	{
		windowWidth = w;
		windowHeight = h;

		Dimension d = new Dimension(windowWidth, windowHeight);
		setPreferredSize(d);
		canvas = frame.getContentPane();
		frame.pack();
	}

	//Dialog for specifically setting the size of the level
	private void createSizeDialog()
	{
		final JDialog dialog = new JDialog(frame, "Set Level Dimensions", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setResizable(false);

		dialog.setMinimumSize(new Dimension(300,
				150));
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));

		final JTextField wField = new JTextField(5);
		final JTextField hField = new JTextField(wField.getColumns());

		JPanel widthPane = new JPanel(new FlowLayout());
		widthPane.add(new JLabel("Width (Greater Than " + frame.getMinimumSize().width + "): "));
		widthPane.add(wField);

		JPanel heightPane = new JPanel(new FlowLayout());
		heightPane.add(new JLabel("Height: (Greater Than " + frame.getMinimumSize().height + "): "));
		heightPane.add(hField);

		JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		final JButton ok = new JButton("Ok");
		final JButton cancel = new JButton("Cancel");
		ok.setPreferredSize(cancel.getPreferredSize());

		buttonContainer.add(ok);
		buttonContainer.add(cancel);

		//dialog.add(dimensionContainer);
		dialog.add(widthPane);
		dialog.add(heightPane);
		dialog.add(buttonContainer);

		ActionListener confirmButtons = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if(source == ok || source == wField || source == hField)
				{
					boolean showOnce = true;
					if(!wField.getText().equals("") && !hField.getText().equals(""))
					{
						try
						{
							int w = Integer.parseInt(wField.getText());
							int h = Integer.parseInt(hField.getText());

							if(w < 250) w = 250;
							if(h < 200) h = 200;

							changeSize(w, h);
							dialog.dispose();
							bb5.log("Custom Level dimensions changed: " + w + "x" + h);
						} catch(NumberFormatException nfe)
						{
							JOptionPane.showMessageDialog(frame, "Empty Field or Improper Character", "Error",
									JOptionPane.ERROR_MESSAGE);
							showOnce = false;
						}
					}
					else
						if(showOnce)
							JOptionPane.showMessageDialog(frame, "Empty Field or Improper Character", "Error",
									JOptionPane.ERROR_MESSAGE);
				}
				else if(source == cancel)
					dialog.dispose();
			}
		};

		ok.addActionListener(confirmButtons);
		cancel.addActionListener(confirmButtons);
		wField.addActionListener(confirmButtons);
		hField.addActionListener(confirmButtons);

		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void createRenameDialog()
	{
		final JDialog dialog = new JDialog(frame, "Rename Level", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setResizable(false);

		JPanel fieldPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JTextField newName = new JTextField(20);
		fieldPane.add(new JLabel("New Name: "));
		fieldPane.add(newName);

		JPanel totalContainer = new JPanel();
		int border = 10;
		totalContainer.setBorder(new EmptyBorder(border, border, border, border));
		JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		final JButton ok = new JButton("Ok");
		final JButton cancel = new JButton("Cancel");
		ok.setPreferredSize(cancel.getPreferredSize());

		buttonContainer.add(ok);
		buttonContainer.add(cancel);

		totalContainer.setLayout(new BoxLayout(totalContainer, BoxLayout.Y_AXIS));
		totalContainer.add(fieldPane);
		totalContainer.add(buttonContainer);

		dialog.add(totalContainer);

		ActionListener confirmButtons = new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				Object source = e.getSource();
				if(source == ok || source == newName)
				{
					boolean showOnce = true;
					if(!newName.getText().equals(""))
					{
						try
						{
							File f = docLocation;
							String nn = docLocation.getParent() + "\\" + newName.getText();
							if(!nn.endsWith(".cbbl")) nn += ".cbbl";
							File newFile = new File(nn);
							f.renameTo(newFile);
							docLocation = newFile;
							docAbsoluteName = newFile.getAbsolutePath();
							docName = nn;
							frame.setTitle("BouncyBall Editor - " + docAbsoluteName);
							dialog.dispose();
							bb5.log("Custom Level renamed");
						} catch(NumberFormatException nfe)
						{
							JOptionPane.showMessageDialog(frame, "Empty Field", "Error",
									JOptionPane.ERROR_MESSAGE);
							showOnce = false;
						}
					}
					else
						if(showOnce)
							JOptionPane.showMessageDialog(frame, "Empty Field", "Error",
									JOptionPane.ERROR_MESSAGE);
				}
				else if(source == cancel)
					dialog.dispose();
			}
		};

		ok.addActionListener(confirmButtons);
		cancel.addActionListener(confirmButtons);
		newName.addActionListener(confirmButtons);

		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}
}