package Editor;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalButtonUI;

import BouncyBallSource.BouncyBallV5;

public class EditorMenu extends JPanel
{
	private BouncyBallV5 bb5;

	private JPanel container = new JPanel();
	private JPanel top = new JPanel();
	private JPanel bottom = new JPanel();

	private final Color defaultButtonColor = new Color(4, 137, 181);
	private final int dBCR = defaultButtonColor.getRed();
	private final int dBCG = defaultButtonColor.getGreen();
	private final int dBCB = defaultButtonColor.getBlue();

	private final Color pressedColor = new Color(2, 95, 126);

	private String createText =
			"<html>"
				+ "<p style = 'border-bottom: 1px solid rgb(" + Integer.toString(dBCR) + "," + Integer.toString(dBCG) + "," + Integer.toString(dBCB) + ");"
						+ " box-sizing: border-box;'>"
					+ "<span style = 'color: rgb(0, 255, 0); font-weight: bold;'>"
						+ "Create"
					+ "</span> "
					+ "a new level"
				+ "</p>"
		   + "</html>";

	private String editText =
			"<html>"
					+ "<p style = 'border-bottom: 1px solid rgb(" + Integer.toString(dBCR) + "," + Integer.toString(dBCG) + "," + Integer.toString(dBCB) + ");"
						+ " box-sizing: border-box;'>"
						+ "<span style = 'color: rgb(0, 255, 0); font-weight: bold;'>"
							+ "Edit"
						+ "</span> "
						+ "an existing level"
					+ "</p>"
			   + "</html>";

	private String playText =
			"<html>"
					+ "<p style = 'border-bottom: 1px solid rgb(" + Integer.toString(dBCR) + "," + Integer.toString(dBCG) + "," + Integer.toString(dBCB) + ");"
						+ " box-sizing: border-box;'>"
						+ "<span style = 'color: rgb(0, 255, 0); font-weight: bold;'>"
							+ "Play"
						+ "</span> "
						+ "your custom level"
					+ "</p>"
			   + "</html>";

	public JButton createButton, editButton, playButton;

	private boolean moveCreateBool = false;
	private boolean moveEditBool = false;
	private boolean movePlayBool = false;

	private int moveMax = 30;
	private int moveSpeed = 2;

	private int defaultInset;

	public EditorMenu(BouncyBallV5 b)
	{
		super(new BorderLayout());
		bb5 = b;
		setBackground(BouncyBallV5.BG_COLOR);

		createLayout();
		addButtons();
	}

	private void createLayout()
	{
		top.setLayout(new BorderLayout());

		container.setOpaque(false);
		top.setOpaque(false);
		bottom.setOpaque(false);
		//bottom.setBackground(Color.CYAN);

		SpringLayout sl = new SpringLayout();
		container.setLayout(sl);
		container.add(top);
		container.add(bottom);

		top.setPreferredSize(new Dimension(0, 30));

		int padding = 10;

		sl.putConstraint(SpringLayout.WEST, top, padding, SpringLayout.WEST, container);
		sl.putConstraint(SpringLayout.NORTH, top, padding, SpringLayout.NORTH, container);
		sl.putConstraint(SpringLayout.EAST, top, -padding, SpringLayout.EAST, container);

		sl.putConstraint(SpringLayout.NORTH, bottom, padding, SpringLayout.SOUTH, top);
		sl.putConstraint(SpringLayout.WEST, bottom, padding, SpringLayout.WEST, container);
		sl.putConstraint(SpringLayout.EAST, bottom, -padding, SpringLayout.EAST, container);
		sl.putConstraint(SpringLayout.SOUTH, bottom, -padding, SpringLayout.SOUTH, container);

		JLabel title = new JLabel("Bouncy Ball Custom Level Editor");
		title.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK), new EmptyBorder(10, 10, 10, 10)));
		//title.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK)));
		title.setFont(new Font("", Font.BOLD, 22));
		//title.setForeground(new Color(0, 150, 255));
		title.setForeground(new Color(0, 50, 255));

		top.add(title, BorderLayout.CENTER);

		add(container);
	}

	private void addButtons()
	{
		Font buttonFont = new Font("", Font.PLAIN, 18);

		JSplitPane splitPane;

		JPanel buttonContainer = new JPanel();
		buttonContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
		buttonContainer.setLayout(new GridBagLayout());
		buttonContainer.setOpaque(false);
		buttonContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.ipady = 10;
		gbc.insets = new Insets(0, 0, 5, 0);

		createButton = new JButton(createText);
		createButton.setFont(buttonFont);
		createButton.setFocusable(false);
		createButton.setBackground(defaultButtonColor);
		createButton.addMouseListener(bb5);
		createButton.setForeground(Color.BLACK);
		createButton.setUI(new MetalButtonUI());
		createButton.setHorizontalAlignment(SwingConstants.LEFT);
		createButton.setAlignmentX(LEFT_ALIGNMENT);
		defaultInset = createButton.getMargin().left;
		buttonContainer.add(createButton, gbc);
		gbc.insets = new Insets(5, 0, 5, 0);

		gbc.gridx = 0; gbc.gridy = 1;
		editButton = new JButton(editText);
		editButton.setFont(buttonFont);
		editButton.setFocusable(false);
		editButton.addMouseListener(bb5);
		editButton.setBackground(defaultButtonColor);
		editButton.setForeground(Color.BLACK);
		editButton.setUI(new MetalButtonUI());
		editButton.setHorizontalAlignment(SwingConstants.LEFT);
		editButton.setAlignmentX(LEFT_ALIGNMENT);
		buttonContainer.add(editButton, gbc);

		gbc.gridx = 0; gbc.gridy = 2;
		playButton = new JButton(playText);
		playButton.setFont(buttonFont);
		playButton.setFocusable(false);
		playButton.addMouseListener(bb5);
		playButton.setBackground(defaultButtonColor);
		playButton.setForeground(Color.BLACK);
		playButton.setUI(new MetalButtonUI());
		playButton.setHorizontalAlignment(SwingConstants.LEFT);
		playButton.setAlignmentX(LEFT_ALIGNMENT);
		buttonContainer.add(playButton, gbc);

		//bigContainer.add(buttonContainer);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonContainer, new TransparentPanel());
		splitPane.setOpaque(false);
		splitPane.setDividerLocation(420);
		splitPane.setBorder(null);
		splitPane.setEnabled(false);
		splitPane.setDividerSize(0);

		bottom.setLayout(new BorderLayout());
		bottom.add(splitPane, BorderLayout.NORTH);
	}

	public void createPressed()
	{
		createButton.setBackground(pressedColor);
		new CustomEditor(CustomEditor.NEW_LEVEL, bb5);
		bb5.frame.setVisible(false);
		createExited();
		createReleased();
	}

	public void createReleased()
	{
		createButton.setBackground(defaultButtonColor);
	}

	public void createEntered()
	{
		createText =
				"<html>"
					+ "<p style = 'border-bottom: 1px solid black; box-sizing: border-box;'>"
						+ "<span style = 'color: rgb(0, 255, 0); font-weight: bold;'>"
							+ "Create"
						+ "</span> "
						+ "a new level"
					+ "</p>"
			   + "</html>";

		createButton.setText(createText);
		moveCreateBool = true;
	}

	public void createExited()
	{
		createText =
			"<html>"
				+ "<p style = 'border-bottom: 1px solid rgb(" + Integer.toString(dBCR) + "," + Integer.toString(dBCG) + "," + Integer.toString(dBCB) + ");"
						+ " box-sizing: border-box;'>"
					+ "<span style = 'color: rgb(0, 255, 0); font-weight: bold;'>"
						+ "Create"
					+ "</span> "
					+ "a new level"
				+ "</p>"
		   + "</html>";
		createButton.setText(createText);
		moveCreateBool = false;
	}

	public void moveCreate()
	{
		if(moveCreateBool)
			if(createButton.getMargin().left < moveMax)
				createButton.setMargin(new Insets(createButton.getMargin().top, createButton.getMargin().left + moveSpeed, createButton.getMargin().bottom,
						createButton.getMargin().right));
			else;
		else
			if(createButton.getMargin().left > defaultInset)
				createButton.setMargin(new Insets(createButton.getMargin().top, createButton.getMargin().left - moveSpeed, createButton.getMargin().bottom,
						createButton.getMargin().right));
			else;
	}

	public void editPressed()
	{
		editButton.setBackground(pressedColor);

		if(System.getProperty("os.name").contains("Mac"))
		{
			FileDialog fd = new FileDialog(bb5.frame, "Open Level", FileDialog.LOAD);
			fd.setModalityType(ModalityType.APPLICATION_MODAL);
			fd.setFilenameFilter(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return name.endsWith(".cbbl");
				}
			});
			fd.setVisible(true);

			if(!("" + fd.getDirectory() + fd.getFile()).equals("nullnull"))
			{
				new CustomEditor(CustomEditor.EDIT_LEVEL, bb5, new File(fd.getDirectory() + fd.getFile()));
				bb5.frame.setVisible(false);
			}
		}
		else
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new FileNameExtensionFilter("Custom Bouncy Ball Level(.cbbl)", "CBBL"));
			int returnVal = jfc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				new CustomEditor(CustomEditor.EDIT_LEVEL, bb5, jfc.getSelectedFile());
				bb5.frame.setVisible(false);
			}
		}

		editExited();
		editReleased();
	}

	public void editReleased()
	{
		editButton.setBackground(defaultButtonColor);
	}

	public void editEntered()
	{
		editText =
			"<html>"
					+ "<p style = 'border-bottom: 1px solid black; box-sizing: border-box;'>"
						+ "<span style = 'color: rgb(0, 255, 0); font-weight: bold;'>"
							+ "Edit"
						+ "</span> "
						+ "an existing level"
					+ "</p>"
			   + "</html>";
		editButton.setText(editText);
		moveEditBool = true;
	}

	public void editExited()
	{
		editText =
			"<html>"
					+ "<p style = 'border-bottom: 1px solid rgb(" + Integer.toString(dBCR) + "," + Integer.toString(dBCG) + "," + Integer.toString(dBCB) + ");"
						+ " box-sizing: border-box;'>"
						+ "<span style = 'color: rgb(0, 255, 0); font-weight: bold;'>"
							+ "Edit"
						+ "</span> "
						+ "an existing level"
					+ "</p>"
			   + "</html>";
		editButton.setText(editText);
		moveEditBool = false;
	}

	public void moveEdit()
	{
		if(moveEditBool)
			if(editButton.getMargin().left < moveMax)
				editButton.setMargin(new Insets(editButton.getMargin().top, editButton.getMargin().left + moveSpeed, editButton.getMargin().bottom,
						editButton.getMargin().right));
			else;
		else
			if(editButton.getMargin().left > defaultInset)
				editButton.setMargin(new Insets(editButton.getMargin().top, editButton.getMargin().left - moveSpeed, editButton.getMargin().bottom,
						editButton.getMargin().right));
			else;
	}

	public void playPressed()
	{
		playButton.setBackground(pressedColor);

		if(System.getProperty("os.name").contains("Mac"))
		{
			FileDialog fd = new FileDialog(bb5.frame, "Play Level", FileDialog.LOAD);
			fd.setModalityType(ModalityType.APPLICATION_MODAL);
			fd.setFilenameFilter(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return name.endsWith(".bb5");
				}
			});
			fd.setVisible(true);

			if(!("" + fd.getDirectory() + fd.getFile()).equals("nullnull"))
				new LevelTester(new File(fd.getDirectory() + fd.getFile()), bb5.frame.getLocation());
				//new CustomEditor(CustomEditor.EDIT_LEVEL, bb5, new File(fd.getDirectory() + fd.getFile()));
		}
		else
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new FileNameExtensionFilter("BouncyBall 5(.bb5)", "BB5"));
			int returnVal = jfc.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
				new LevelTester(jfc.getSelectedFile(), bb5.frame.getLocation());
		}

		playExited();
		playReleased();
	}

	public void playReleased()
	{
		playButton.setBackground(defaultButtonColor);
	}

	public void playEntered()
	{
		playText =
			"<html>"
					+ "<p style = 'border-bottom: 1px solid black; box-sizing: border-box;'>"
						+ "<span style = 'color: rgb(0, 255, 0); font-weight: bold;'>"
							+ "Play"
						+ "</span> "
						+ "your custom level"
					+ "</p>"
			   + "</html>";
		playButton.setText(playText);
		movePlayBool = true;
	}

	public void playExited()
	{
		playText =
			"<html>"
					+ "<p style = 'border-bottom: 1px solid rgb(" + Integer.toString(dBCR) + "," + Integer.toString(dBCG) + "," + Integer.toString(dBCB) + ");"
						+ " box-sizing: border-box;'>"
						+ "<span style = 'color: rgb(0, 255, 0); font-weight: bold;'>"
							+ "Play"
						+ "</span> "
						+ "your custom level"
					+ "</p>"
			   + "</html>";
		playButton.setText(playText);
		movePlayBool = false;
	}

	public void movePlay()
	{
		if(movePlayBool)
			if(playButton.getMargin().left < moveMax)
				playButton.setMargin(new Insets(playButton.getMargin().top, playButton.getMargin().left + moveSpeed, playButton.getMargin().bottom,
						playButton.getMargin().right));
			else;
		else
			if(playButton.getMargin().left > defaultInset)
				playButton.setMargin(new Insets(playButton.getMargin().top, playButton.getMargin().left - moveSpeed, playButton.getMargin().bottom,
						playButton.getMargin().right));
			else;
	}

	public boolean getMoveCreate()
	{
		return moveCreateBool;
	}

	private class TransparentPanel extends JPanel
	{
		public TransparentPanel()
		{
			setOpaque(false);
		}
	}
}
