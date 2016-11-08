package Editor.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Editor.CustomEditor;
import resources.objects.TextBox;

public class TextDialog extends JDialog implements ActionListener
{
	private CustomEditor ce;

	private int type;
	public static final int NEW_TEXTBOX = 1;
	public static final int PROPS = 2;

	private JButton addText = new JButton("Add Text");
	private JButton cancel = new JButton("Cancel");

	private JTextArea textField = new JTextArea();
	private JTextField fontField = new JTextField();
	private JTextField sizeField = new JTextField();
	private JTextField xField = new JTextField();
	private JTextField yField = new JTextField();
	private JCheckBox bold = new JCheckBox("Is Bold", false);

	private String[] alignOps = {"Left", "Center", "Right"};
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox alignBox = new JComboBox(alignOps);

	private Font f = new Font("", Font.PLAIN, 14);

	private int oIndex = -1;

	public TextDialog(CustomEditor ce)
	{
		super(ce.frame, "New Text Box", Dialog.ModalityType.APPLICATION_MODAL);
		this.ce = ce;
		this.type = NEW_TEXTBOX;

		basicOps();

		pack();
		setVisible(true);
	}

	public TextDialog(CustomEditor ce, int type, TextBox tb, int oIndex)
	{
		super(ce.frame, "New Text Box", Dialog.ModalityType.APPLICATION_MODAL);
		this.ce = ce;
		this.type = type;
		this.oIndex = oIndex;

		basicOps();

		if(this.type == PROPS)
		{
			textField.setText(tb.getText());
			textField.setText(tb.getText().replace("\\n", "\n"));
			fontField.setText(tb.getFontName());
			sizeField.setText(Integer.toString(tb.getFontSize()));
			xField.setText(Integer.toString(tb.getX()));
			yField.setText(Integer.toString(tb.getY()));
			if(tb.getBold())
				bold.setSelected(true);
			else
				bold.setSelected(false);

			if(tb.getAlignment() == TextBox.LEFT)
				alignBox.setSelectedIndex(0);
			else if(tb.getAlignment() == TextBox.CENTER)
				alignBox.setSelectedIndex(1);
			else if(tb.getAlignment() == TextBox.RIGHT)
				alignBox.setSelectedIndex(2);
		}

		pack();
		setVisible(true);
	}

	private void basicOps()
	{
		addLayout();
		addButtons();
		setResizable(false);

		fontField.addActionListener(this);
		sizeField.addActionListener(this);
		xField.addActionListener(this);
		yField.addActionListener(this);

		setLocation((ce.frame.getX() + ce.frame.getWidth()/2) - getPreferredSize().width/2,
				(ce.frame.getY() + ce.frame.getHeight()/2) - getPreferredSize().height/2);
	}

	private void addLayout()
	{
		JPanel containerPane = new JPanel();

		JPanel propertyPane = new JPanel(new GridBagLayout());
		propertyPane.setBorder(new EmptyBorder(10,10,10,10));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2,2,2,2);

		gbc.anchor = GridBagConstraints.NORTHWEST; gbc.weighty = .05;
		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = .05; gbc.fill = GridBagConstraints.HORIZONTAL;
		JLabel textL = new JLabel("<html>Text<span style='color: red;'> *</span>:</html>");
		textL.setFont(f.deriveFont(Font.BOLD));
		propertyPane.add(textL, gbc);
		gbc.gridx = 1;
		gbc.weightx = .95;
		textField.setLineWrap(true);
		textField.setWrapStyleWord(true);
		gbc.fill = GridBagConstraints.BOTH;

		JScrollPane jsp = new JScrollPane(textField);
		jsp.setBorder(fontField.getBorder());
		jsp.setPreferredSize(new Dimension(220, 100));
		textField.setFont(f);
		propertyPane.add(jsp, gbc);

		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = .05; gbc.fill = GridBagConstraints.HORIZONTAL;
		JLabel fontLabel = new JLabel("Font: ");
		fontLabel.setFont(f.deriveFont(Font.BOLD));
		propertyPane.add(fontLabel, gbc);
		gbc.gridx = 1;
		gbc.weightx = .95;
		fontField.setFont(f);
		propertyPane.add(fontField, gbc);

		gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = .05;
		JLabel sizeLabel = new JLabel("Size: ");
		sizeLabel.setFont(f.deriveFont(Font.BOLD));
		propertyPane.add(sizeLabel, gbc);
		gbc.gridx = 1;
		gbc.weightx = .95;
		sizeField.setFont(f);
		propertyPane.add(sizeField, gbc);

		gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = .05;
		JLabel xLabel = new JLabel("<html>X Coord<span style='color: red;'> *</span>:</html>");
		xLabel.setFont(f.deriveFont(Font.BOLD));
		propertyPane.add(xLabel, gbc);
		gbc.gridx = 1;
		gbc.weightx = .95;
		xField.setFont(f);
		propertyPane.add(xField, gbc);

		gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = .05;
		JLabel yLabel = new JLabel("<html>Y Coord<span style='color: red;'> *</span>:</html>");
		yLabel.setFont(f.deriveFont(Font.BOLD));
		propertyPane.add(yLabel, gbc);
		gbc.gridx = 1;
		gbc.weightx = .95;
		yField.setFont(f);
		propertyPane.add(yField, gbc);

		gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = .05;
		JLabel boldLabel = new JLabel("Bold: ");
		boldLabel.setFont(f.deriveFont(Font.BOLD));
		propertyPane.add(boldLabel, gbc);
		gbc.gridx = 1;
		gbc.weightx = .95;
		bold.setFont(f);
		bold.setFocusable(false);
		propertyPane.add(bold, gbc);

		gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = .05;
		JLabel alignLabel = new JLabel("Align: ");
		alignLabel.setFont(f.deriveFont(Font.BOLD));
		propertyPane.add(alignLabel, gbc);
		gbc.gridx = 1;
		gbc.weightx = .95;
		alignBox.setFont(f);
		alignBox.setFocusable(false);
		propertyPane.add(alignBox, gbc);

		containerPane.add(propertyPane);
		add(propertyPane, BorderLayout.CENTER);
	}

	private void addButtons()
	{
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		cancel.setFocusable(false);
		cancel.addActionListener(this);

		addText.setFocusable(false);
		addText.addActionListener(this);

		buttonPane.add(cancel);
		buttonPane.add(addText);

		add(buttonPane, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == cancel)
			dispose();
		else if(source == addText || source == fontField || source == sizeField)
		{
			boolean showOnce = false;
			if(!textField.getText().isEmpty() && !xField.getText().isEmpty() && !yField.getText().isEmpty())
			{
				try
				{
					String txt = textField.getText();
					String fontFam = "";
					int fontSize = 22;
					int boldInt = Font.PLAIN;
					int align = TextBox.LEFT;

					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());

					if(bold.isSelected())
						boldInt = Font.BOLD;
					if(!sizeField.getText().isEmpty())
						fontSize = Integer.parseInt(sizeField.getText());
					if(!fontField.getText().isEmpty())
						fontFam = fontField.getText();

					Font f = new Font(fontFam, boldInt, fontSize);
					if(fontFam.toLowerCase().equals("black tuesday"))
						f = ce.bb5.blackTuesday.deriveFont(boldInt, (float)fontSize);
					else if(fontFam.toLowerCase().equals("comfortaa"))
						f = ce.bb5.comfortaa.deriveFont(boldInt, (float)fontSize);
					else if(fontFam.toLowerCase().equals("samsung1"))
						f = ce.bb5.samsung1.deriveFont(boldInt, (float)fontSize);
					else if(fontFam.toLowerCase().equals("time burner"))
						f = ce.bb5.timeburner.deriveFont(boldInt, (float)fontSize);
					else if(fontFam.toLowerCase().equals("apple2"))
						f = ce.bb5.apple2.deriveFont(boldInt, (float)fontSize);

					if(alignBox.getSelectedIndex() == 0)
						align = TextBox.LEFT;
					else if(alignBox.getSelectedIndex() == 1)
						align = TextBox.CENTER;
					else if(alignBox.getSelectedIndex() == 2)
						align = TextBox.RIGHT;

					if(type == NEW_TEXTBOX)
						ce.addObject(new TextBox(txt, x, y, f, align));
					else if(type == PROPS)
						ce.setObject(oIndex, new TextBox(txt, x, y, f, align));

					dispose();
				} catch(NumberFormatException nfe)
				{
					showOnce = true;
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			else
				if(!showOnce)
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error",
							JOptionPane.ERROR_MESSAGE);
		}
	}
}
