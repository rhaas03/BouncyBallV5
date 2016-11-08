package BouncyBallSource;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import resources.Encrypter;
import resources.JLabelWithFont;

public class Console extends JFrame implements ActionListener
{
	private BouncyBallV5 bb5;

	private ArrayList<String[]> admins = new ArrayList<String[]>();
	private ArrayList<String> commandHistory = new ArrayList<String>();
	private int historyCounter = 0;
	private File adminFile = new File(BouncyBallV5.FILE_PATH + "admins.txt");

	private CardLayout cl = new CardLayout();
	private JPanel paneContainer = new JPanel(cl);
	private JPanel loginPane = new JPanel();
	private JPanel consolePane = new JPanel();

	private JTextField username = new JTextField(15);
	private JPasswordField password = new JPasswordField(15);
	private JButton submitLogin = new JButton("Submit");

	private JScrollPane consoleScroll;
	private JTextPane display = new JTextPane();
	private JTextField commandField = new JTextField();

	private JLabel loginError = new JLabel("");

	private final String division = "- - - - - - - - - - - - - - - - - -";
	private final String[] help = {
			"-addAdmin <username> <password>: creates a new admin",
			"-clearAdmins: resets all admins",
			"-close: closes console",
			"-cls: clears the console",
			"-exit: closes console",
			"-help: lists all commands",
			"-logout: logs you out of the console",
			"-font <size>: sets the font size of text in the console",
			"-bg <color>: sets background color of console",
			"-encrypt <text>: encrypts text. ",
			"  If word is more than word, it must be surrounded in quotes",
			"-decrypt <text>: decrypts text. Should not have any spaces",
			"-listAdmins: lists all admins",
			"-delAdmin <admin username>: deletes admin",
			"-sdwn: closes program",
			"-version: returns BouncyBall version"
	};

	private Font displayFont;

	private String currentUser = "";

	private boolean checkingClear = false;

	private final Color error = Color.RED;
	private final Color completed = Color.GREEN.darker();

	public Console(BouncyBallV5 b)
	{
		bb5 = b;
		adminCheck();
		setPreferredSize(new Dimension(350, 200));
		setMinimumSize(new Dimension(350, 200));
		setLocation(
				(bb5.frame.getLocationOnScreen().x + bb5.frame.getLocationOnScreen().x + bb5.frame.getWidth())/2 - getPreferredSize().width/2,
				(bb5.frame.getLocationOnScreen().y + bb5.frame.getLocationOnScreen().y + bb5.frame.getHeight())/2 - getPreferredSize().height/2);
		setTitle("Bouncy Ball Console: Login");
		setResizable(false);
		addWindowListener(closer());

		displayFont = new Font("Consolas", Font.PLAIN, 12);
		createLogin();
		createConsole();

		add(paneContainer, BorderLayout.CENTER);
		add(loginError, BorderLayout.SOUTH);
		paneContainer.add(loginPane, "0");
		paneContainer.add(consolePane, "1");

		cl.show(paneContainer, "0");

		pack();
		setVisible(true);

		bb5.log("Console Created");
	}

	private void createLogin()
	{
		//Creates the login screen
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; //gbc.weightx = 1;
		loginPane.setLayout(new BorderLayout());
		JPanel loginContainer = new JPanel(new GridBagLayout());
		loginContainer.setBorder(new EmptyBorder(5, 25, 5, 5));

		gbc.ipady = 0;
		loginContainer.add(new JLabelWithFont("Username: ", 14), gbc);
		gbc.gridx = 1; gbc.ipady = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
		loginContainer.add(username, gbc);
		gbc.weightx = 0;
		gbc.gridx = 0; gbc.gridy = 1; gbc.ipady = 20;
		loginContainer.add(new JLabelWithFont("Password: ", 14), gbc);
		gbc.gridx = 1; gbc.ipady = 0;
		gbc.weightx = 1;
		loginContainer.add(password, gbc);
		gbc.weightx = 0;
		gbc.gridx = 0; gbc.gridy = 2;
		loginContainer.add(submitLogin, gbc);
		gbc.gridwidth = 1;

		password.setEchoChar('\u25CF');
		loginPane.add(loginContainer, BorderLayout.CENTER);
		submitLogin.setFont(submitLogin.getFont().deriveFont(14f));
		submitLogin.setFocusable(false);
		submitLogin.addActionListener(this);

		password.addActionListener(this);
		password.setFont(password.getFont().deriveFont(14f));
		password.addKeyListener(escape());
		username.addActionListener(this);
		username.setFont(password.getFont());
		username.addKeyListener(escape());
	}

	private void createConsole()
	{
		//Creates the console itself
		consolePane.setLayout(new BorderLayout());

		//Increase Scroll Speed
		consoleScroll = new JScrollPane(display);
		consoleScroll.setBorder(null);
		consoleScroll.getVerticalScrollBar().setUnitIncrement(3);

		display.setBackground(new  Color(250, 250, 250));
		display.setFont(displayFont);
		display.setBorder(new EmptyBorder(5, 5, 5, 5));
		display.setMargin(new Insets(5, 5, 5, 5));
		display.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		display.setEditable(false);

		DefaultCaret caret = (DefaultCaret) display.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		appendToPane(display, "Welcome To: Bouncy Ball Console", Color.BLUE);
		appendToPane(display, "Type \"help\" for commands", Color.BLUE);
		appendToPane(display, division, Color.BLACK);

		commandField.addActionListener(this);
		commandField.setFont(displayFont);
		commandField.requestFocus();
		commandField.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				int k = e.getKeyCode();
				if(k == KeyEvent.VK_UP)
				{
					if(historyCounter > 0)
					{
						historyCounter--;
						commandField.setText(commandHistory.get(historyCounter));
					}
				}
				else if(k == KeyEvent.VK_DOWN)
				{
					if(historyCounter < commandHistory.size() - 1)
					{
						historyCounter++;
						commandField.setText(commandHistory.get(historyCounter));
					}
				}
				else
					historyCounter = commandHistory.size();
			}
		});

		consolePane.add(consoleScroll, BorderLayout.CENTER);
		consolePane.add(commandField, BorderLayout.SOUTH);
	}

	private void resetDisplay()
	{
		display.setText("");
		//displayFont = displayFont.deriveFont(12f);
		display.setFont(displayFont);
		commandField.setFont(displayFont);
		display.setBackground(Color.WHITE);
		appendToPane(display, "Welcome To the Bouncy Ball Console", Color.BLUE);
		appendToPane(display, "Type \"help\" for commands", Color.BLUE);
		appendToPane(display, division, Color.BLACK);

		bb5.log("Console display reset");
	}

	private void checkLogin(String u, String p)
	{
		//Checks the login credentials against the known admins
		adminCheck();
		boolean loginSuccess = false;
		for(int x = 0; x < admins.size(); x++)
		{
			bb5.encrypter.setEncryptionLevel(Encrypter.MAX_ENCRYPTION);
			String dAdminU = bb5.encrypter.decryptString(admins.get(x)[0]);
			String dAdminP = bb5.encrypter.decryptString(admins.get(x)[1]);
			if(u.equals(dAdminU))
				if(p.equals(dAdminP))
				{
					loginSuccess = true;
					currentUser = dAdminU;
					break;
				}
		}

		if(loginSuccess)
		{
			bb5.log("Succesful console log in");
			moveToConsole();
		}
		else
		{
			bb5.log("Unsuccesful console log in");
			loginError.setText("Error Logging In");
			loginError.setForeground(Color.RED);
			loginError.setHorizontalAlignment(SwingConstants.CENTER);;
			loginError.setVerticalAlignment(SwingConstants.NORTH);
			loginError.setFont(new Font("", Font.PLAIN, 12));
			loginError.setBorder(new EmptyBorder(5, 5, 5, 5));

			username.setText("");
			password.setText("");
			username.requestFocus();
		}
	}

	private void moveToConsole()
	{
		//Moves from the Login Screen to the Console
		username.setText("");
		password.setText("");
		loginError.setText("");
		cl.show(paneContainer, "1");
		loginError.setBorder(null);
		commandField.requestFocus();
		commandField.setText("");
		resetDisplay();
		setResizable(true);
		setPreferredSize(new Dimension(500, 300));
		setMinimumSize(new Dimension(500, 300));
		setLocation(
				(bb5.frame.getLocationOnScreen().x + bb5.frame.getLocationOnScreen().x + bb5.frame.getWidth())/2 - getPreferredSize().width/2,
				(bb5.frame.getLocationOnScreen().y + bb5.frame.getLocationOnScreen().y + bb5.frame.getHeight())/2 - getPreferredSize().height/2);
		pack();
		setTitle("Bouncy Ball Administrative Console");
	}

	private void adminCheck()
	{
		//Checks the available admins from an admins file
		try
		{
			FileInputStream fis = new FileInputStream(adminFile);
			ObjectInputStream ois = new ObjectInputStream(fis);

			String[][] info = (String[][])ois.readObject();
			admins.clear();
			for(int x = 0; x < info.length; x++)
				admins.add(info[x]);

			if(admins.isEmpty())
				addDefaultAdmin();

			bb5.encrypter.setEncryptionLevel(Encrypter.MAX_ENCRYPTION);
			boolean adminExists = false;
			for(int x = 0; x < admins.size(); x++)
			{
				if(admins.get(x)[0].equals(bb5.encrypter.encryptString("admin")))
				{
					adminExists = true;
					bb5.log("Default admin exists");
					break;
				}
			}

			if(!adminExists)
				addDefaultAdmin();

			ois.close();
			fis.close();

			Collections.sort(admins, new Comparator<String[]>() {
				@Override
				public int compare(String[] o1, String[] o2) {
					return o1[0].compareTo(o2[0]);
				}
			});
		} catch(FileNotFoundException e)
		{
			adminFile.getParentFile().mkdir();
			try {
				adminFile.createNewFile();
				bb5.log("Admins file created");
				addDefaultAdmin();
			} catch (IOException e1) {
				bb5.log("Error creating admins file");
				e.printStackTrace();
			}
		} catch (IOException e)
		{
			System.out.println("Error(IOException 2: \n");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Error(ClassNotFoundException: \n");
			e.printStackTrace();
		}
	}

	private void addDefaultAdmin()
	{
		//Creates a default admin so there is always a way into the console
		try {
			addAdmin("admin", "admin");
			adminCheck();
			bb5.log("Default admin created");
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean addAdmin(String u, String p)
	{
		//Adds a new admin
		bb5.log("Creating a new admin");
		boolean adminAlreadyExists = false;
		for(int x = 0; x < admins.size(); x++)
		{
			bb5.encrypter.setEncryptionLevel(Encrypter.MAX_ENCRYPTION);
			String decryptedAdminU = bb5.encrypter.decryptString(admins.get(x)[0]);
			if(u.equals(decryptedAdminU))
			{
				adminAlreadyExists = true;
				break;
			}
		}
		if(!adminAlreadyExists)
		{
			try
			{
				FileOutputStream fos = new FileOutputStream(adminFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				bb5.encrypter.setEncryptionLevel(Encrypter.MAX_ENCRYPTION);
				String eU = bb5.encrypter.encryptString(u);
				String eP = bb5.encrypter.encryptString(p);

				bb5.log("New Admin:");
				bb5.log("\tUsername: " + eU);
				bb5.log("\tPassword: " + eP);

				admins.add(new String[]{eU, eP});
				String[][] temp = new String[admins.size()][2];
				for(int x = 0; x < admins.size(); x++)
				{
					temp[x][0] = admins.get(x)[0];
					temp[x][1] = admins.get(x)[1];
				}

				oos.writeObject(temp);
				oos.close();
				fos.close();
				adminCheck();
				bb5.log("Admin succesfully created");
				return true;
			} catch(Exception e)
			{
				e.printStackTrace();
				bb5.log("Error creating admin");
				return false;
			}
		}
		else
		{
			bb5.log("Admin already exists");
			return false;
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == submitLogin || source == username || source == password)
		{
			checkLogin(username.getText(), new String(password.getPassword()));
		}
		else if(source == commandField)
		{
			checkCommand(commandField.getText());
			consoleScroll.getVerticalScrollBar().setValue(consoleScroll.getVerticalScrollBar().getMaximum() + 10);
			display.setCaretPosition(display.getDocument().getLength());
		}
	}

	private WindowListener closer()
	{
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				dispose();
				bb5.consoleOpen = false;
				bb5.frame.requestFocus();
			}
		};
	}

	private void logout()
	{
		//Logs the current admin out and returns to the login screen
		commandField.setText("");
		resetDisplay();
		cl.show(paneContainer, "0");
		username.requestFocus();
		setPreferredSize(new Dimension(350, 200));
		setMinimumSize(new Dimension(350, 200));
		setResizable(false);
		setLocation(
				(bb5.frame.getLocationOnScreen().x + bb5.frame.getLocationOnScreen().x + bb5.frame.getWidth())/2 - getPreferredSize().width/2,
				(bb5.frame.getLocationOnScreen().y + bb5.frame.getLocationOnScreen().y + bb5.frame.getHeight())/2 - getPreferredSize().height/2);
		pack();
		setTitle("Bouncy Ball Console: Login");
		bb5.log("Logged Out");
	}

	private void checkCommand(String command)
	{
		//ArrayList to organize command parameters
		ArrayList<String> comParams = new ArrayList<String>();

		//fullCom is only ever used to tell the user the command they typed
		String fullCom = "";
		//Splits command into its parameters
		for(String s : command.trim().split("\\s++"))
		{
			fullCom += s + " ";
			comParams.add(s);
		}
		//To remove ending whitespace
		fullCom = fullCom.substring(0, fullCom.length() - 1);
		if(!checkingClear)
			appendToPane(display, currentUser +": " + fullCom, Color.BLACK);
		else
			appendToPane(display, fullCom, Color.BLACK);

		String primaryCom = comParams.get(0);

		if(!checkingClear)
		{
			if(primaryCom.toLowerCase().equals("exit") || primaryCom.toLowerCase().equals("close"))
			{
				dispose();
				bb5.log("Console closed");
				bb5.consoleOpen = false;
			}
			else if(primaryCom.equals("addAdmin"))
			{
				try
				{
					String adminUser = comParams.get(1);
					String adminPass = comParams.get(2);

					if(addAdmin(adminUser, adminPass))
						appendToPane(display, "New Admin Created", completed);
					else
						appendToPane(display, "Error: Admin already exists", error);
				} catch(IndexOutOfBoundsException e)
				{
					appendToPane(display, "Error: Missing Parameters", error);
				}
			}
			else if(primaryCom.equals("logout"))
				if(comParams.size() < 2)
					logout();
				else
					appendToPane(display, "Unnecessary Parameters", error);
			else if(primaryCom.equals("clearAdmins")) {
				if(comParams.size() < 2)
				{
					appendToPane(display, "Are you sure you want to clear all admins(Y/N): ", error, "no line");
					checkingClear = true;
				}
				else
					appendToPane(display, "Unnecessary Parameters", error);
			}
			else if(primaryCom.equals("cls"))
				if(comParams.size() < 2)
					resetDisplay();
				else
					appendToPane(display, "Unnecessary Parameters", error);
			else if(primaryCom.equals("help"))
			{
				if(comParams.size() < 2)
				{
					appendToPane(display, "List of Available Commands", completed);
					for(int x = 0; x < help.length; x++)
						appendToPane(display, "    " + help[x], completed);
				}
				else
					appendToPane(display, "Unnecessary Parameters", error);
			}
			else if(primaryCom.equals("font"))
			{
				try
				{
					int fontSize = Integer.parseInt(comParams.get(1));
					displayFont = displayFont.deriveFont((float)fontSize);
					display.setFont(displayFont);
					commandField.setFont(display.getFont());
					setPreferredSize(getSize());
					pack();
					appendToPane(display, "Set font to " + fontSize, completed);
				} catch(NumberFormatException nfe)
				{
					appendToPane(display, "Error: No font set", error);
				} catch(IndexOutOfBoundsException iobe)
				{
					appendToPane(display, "Error: Missing Parameters", error);
				}
			}
			else if(primaryCom.equals("bg"))
			{
				try
				{
					Color color = parseColor(comParams.get(1));
					if(color == Color.BLACK)
					{
						display.setForeground(Color.WHITE);
						System.out.println("called");
					}
					else if(color == Color.WHITE)
						display.setForeground(Color.BLACK);
					display.setBackground(color);
					appendToPane(display, "Color set to: " + color, completed);
				} catch(IndexOutOfBoundsException iobe)
				{
					appendToPane(display, "Error: Missing Parameters", error);
				}
			}
			else if(primaryCom.equals("encrypt"))
			{
				if(comParams.size() > 1)
				{
					boolean beginQuote = false;
					boolean errorEncountered = false;
					int beginIndex = -1;
					int endIndex = -1;
					String toEncrpyt = "";
					if(comParams.size() > 2)
					{
						for(int x = 1; x < comParams.size(); x++)
						{
							if(!beginQuote)
							{
								if(comParams.get(x).startsWith("\""))
								{
									beginQuote = true;
									beginIndex = x;
								}
							}
							else
							{
								if(comParams.get(x).endsWith("\""))
								{
									endIndex = x;
									break;
								}
							}

						}

						if(beginIndex != -1 && endIndex != -1)
							for(int x = beginIndex; x < endIndex + 1; x++)
									toEncrpyt += comParams.get(x) + " ";
						else
							errorEncountered = true;
					}
					else if(comParams.size() > 0)
						toEncrpyt = comParams.get(1);

					if(!errorEncountered)
					{
						if(toEncrpyt.endsWith(" "))
							toEncrpyt = toEncrpyt.substring(0, toEncrpyt.length() - 1);

						appendToPane(display, "Encrpyting String: " + toEncrpyt, completed);
						bb5.encrypter.setEncryptionLevel(Encrypter.LOW_ENCRYPTION);
						appendToPane(display, "Encrypted String with Low Level Encryption: " + bb5.encrypter.encryptString(toEncrpyt.replaceAll("\"", "")), completed);
					}
					else
						appendToPane(display, "Error: Extra Parameter or missing endquote", error);
				}
				else
					appendToPane(display, "Error: Missing Parameters", error);
			}
			else if(primaryCom.equals("decrypt"))
			{
				if(comParams.size() > 1)
				{
					if(comParams.size() < 3)
					{
						String toDecString = comParams.get(1);
						appendToPane(display, "Decrypting String: " + toDecString, completed);
						bb5.encrypter.setEncryptionLevel(Encrypter.LOW_ENCRYPTION);
						appendToPane(display, "Decrypted String with Low Level Encryption: " + bb5.encrypter.decryptString(toDecString), completed);
					}
					else
						appendToPane(display, "Error: Unnecessary Parameters", error);
				}
				else
					appendToPane(display, "Error: Missing Parameters", error);
			}
			else if(primaryCom.equals("listAdmins"))
			{
				if(comParams.size() < 2)
				{
					appendToPane(display, "All Admins", completed);
					bb5.encrypter.setEncryptionLevel(Encrypter.MAX_ENCRYPTION);
					for(int x = 0; x < admins.size(); x++)
						appendToPane(display, "    " + bb5.encrypter.decryptString(admins.get(x)[0]), completed);
				}
				else
					appendToPane(display, "Unnecessary Parameters", error);
			}
			else if(primaryCom.equals("delAdmin"))
			{
				try
				{
					String adminToDel = comParams.get(1);
					System.out.println("Admin to delete: " + adminToDel);
					if(deleteAdmin(adminToDel))
						appendToPane(display, "Admin removed", completed);
					else
						appendToPane(display, "Error Removing Admin", error);
				} catch(IndexOutOfBoundsException ioobe)
				{
					appendToPane(display, "Error: Missing Parameter", error);
				}
			}
			else if(primaryCom.equals("sdwn"))
			{
				bb5.log("Exiting");
				bb5.log("-------");
				bb5.closerLogger();
				System.exit(0);
			}
			else if(primaryCom.equals("screen"))
			{
				if(comParams.size() > 1 && comParams.size() < 3)
				{
					String screenTxt = comParams.get(1);
					int screen = -1;
					if(screenTxt.equals("about"))
						screen = BouncyBallV5.ABOUT;
					else if(screenTxt.equals("help"))
						screen = BouncyBallV5.HELP;
					else if(screenTxt.equals("editor"))
						screen = BouncyBallV5.EDITOR_MENU;
					else if(screenTxt.equals("menu"))
						screen = BouncyBallV5.GAME_MENU;

					if(screen != -1)
					{
						bb5.setScreenManually(screen);
						bb5.repaint();
						appendToPane(display, "Screen Set", completed);
					}
					else
						appendToPane(display, "Error: Could not set screen", error);
				}
				else
					appendToPane(display, "Error: Parameter Issue", error);
			}
			else if(primaryCom.equals("version"))
			{
				if(comParams.size() == 1)
				{
					appendToPane(display, "Version: " + BouncyBallV5.VERSION, completed);
				}
				else
					appendToPane(display, "Unnecessary Parameters", error);
			}
			else
				appendToPane(display, "Unrecognized command", error);
		}
		else
		{
			if(primaryCom.toLowerCase().equals("y"))
			{
				clearAdmins();
				appendToPane(display, "Admins Cleared", completed);
			}
			else if(primaryCom.toLowerCase().equals("n"))
				appendToPane(display, "Operation Canceled", completed);
			else
				appendToPane(display, "Improper Character: Operation Canceled", error);
			checkingClear = false;
		}

		commandHistory.add(fullCom);
		historyCounter = commandHistory.size();
		commandField.setText("");
	}

	private void clearAdmins()
	{
		admins.clear();
		bb5.log("Admins Cleared");
		if(!currentUser.toLowerCase().equals("admin"))
			logout();
		addDefaultAdmin();
	}

	private void appendToPane(JTextPane pane, String text, Color color)
	{
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		try
		{
		    StyledDocument doc = pane.getStyledDocument();
		    display.setDocument(doc);
		    doc.insertString(doc.getLength(), text + "\n", aset);
		}
		catch(BadLocationException e) { System.out.println(e); }
	}

	private void appendToPane(JTextPane pane, String text, Color color, Object noLine)
	{
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

		try
		{
		    StyledDocument doc = pane.getStyledDocument();
		    display.setDocument(doc);
		    doc.insertString(doc.getLength(), text, aset);
		}
		catch(BadLocationException e) { System.out.println(e); }
	}

	@SuppressWarnings("unused")
	private void appendToPane(JTextPane pane, String text, Color color, Font f)
	{
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, f.getFamily());
		aset = sc.addAttribute(aset, StyleConstants.FontSize, f.getSize());
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		try
		{
		    StyledDocument doc = pane.getStyledDocument();
		    display.setDocument(doc);
		    doc.insertString(doc.getLength(), text + "\n", aset);
		}
		catch(BadLocationException e) { System.out.println(e); }
	}

	private boolean deleteAdmin(String adminU)
	{
		boolean adminExists = false;
		int adminIndex = -1;
		for(int x = 0; x < admins.size(); x++)
		{
			bb5.encrypter.setEncryptionLevel(Encrypter.MAX_ENCRYPTION);
			String decryptedAdminU = bb5.encrypter.decryptString(admins.get(x)[0]);
			if(adminU.equals(decryptedAdminU))
			{
				adminExists = true;
				adminIndex = x;
				break;
			}
		}

		if(adminExists)
		{
			admins.remove(adminIndex);
			try
			{
				FileOutputStream fos = new FileOutputStream(adminFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				String[][] temp = new String[admins.size()][2];
				for(int x = 0; x < admins.size(); x++)
				{
					temp[x][0] = admins.get(x)[0];
					temp[x][1] = admins.get(x)[1];
				}

				oos.writeObject(temp);
				oos.close();
				fos.close();
				adminCheck();
				bb5.log("Admin removed");
				if(adminU.equals(currentUser) && !adminU.equals("admin"))
					logout();
				return true;
			} catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			bb5.log("Error Removing Admin");
			return false;
		}
	}

	private KeyListener escape()
	{
		return new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					dispose();
					bb5.log("Console closed");
					bb5.consoleOpen = false;
				}
			}
		};
	}

	private Color parseColor(String color)
	{
		String col = color.toLowerCase();
		if(col.equals("black"))
			return Color.BLACK;
		else if(col.equals("blue"))
			return Color.BLUE;
		else if(col.equals("red"))
			return Color.RED;
		else if(col.equals("white"))
			return Color.WHITE;
		else if(col.equals("purple"))
			return Color.MAGENTA;
		else if(col.equals("cyan"))
			return Color.CYAN;
		else
			return null;
	}
}