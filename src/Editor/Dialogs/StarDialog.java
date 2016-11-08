package Editor.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import Editor.CustomEditor;
import resources.objects.StarShape;

public class StarDialog extends JDialog implements ActionListener
{
	private CustomEditor ce;

	private int type;
	public static final int NEW_STAR = 1;
	public static final int PROPS = 2;

	private JButton addStar = new JButton("Add Star");
	private JButton cancel = new JButton("Cancel");

	private JTextField xField, yField, sizeField;

	private Font f = new Font("", Font.PLAIN, 14);

	private int oIndex = -1;

	public StarDialog(CustomEditor ce)
	{
		super(ce.frame, "New Star", Dialog.ModalityType.APPLICATION_MODAL);
		this.ce = ce;
		this.type = NEW_STAR;
		basicOps();

		setLocation((ce.frame.getX() + ce.frame.getWidth()/2) - getPreferredSize().width/2,
				(ce.frame.getY() + ce.frame.getHeight()/2) - getPreferredSize().height/2);

		pack();
		setVisible(true);
	}

	public StarDialog(CustomEditor ce, int type, StarShape s, int oIndex)
	{
		super(ce.frame, Dialog.ModalityType.APPLICATION_MODAL);
		if(type == NEW_STAR)
			setTitle("New Star");
		else if(type == PROPS)
			setTitle("Star Properties");

		this.ce = ce;
		this.type = type;
		this.oIndex = oIndex;

		basicOps();

		if(this.type == PROPS)
		{
			xField.setText(Integer.toString(s.getX()/2));
			yField.setText(Integer.toString(s.getY()/2));
			sizeField.setText(Integer.toString(s.getSize()));
		}

		setLocation((ce.frame.getX() + ce.frame.getWidth()/2) - getPreferredSize().width/2,
				(ce.frame.getY() + ce.frame.getHeight()/2) - getPreferredSize().height/2);

		pack();
		setVisible(true);
	}

	private void basicOps()
	{
		setMinimumSize(new Dimension(300, 100));
		addPane();
		addConfirmButtons();

		setResizable(false);
	}

	private void addPane()
	{
		JPanel container = new JPanel(new BorderLayout());
		int inset = 5;
		TitledBorder tb = BorderFactory.createTitledBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK), "Properties");
		tb.setTitleFont(tb.getTitleFont().deriveFont(16f));
		container.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(inset, inset, inset, inset),
				tb));

		JPanel newStarPane = new JPanel(new GridBagLayout());
		newStarPane.setOpaque(true);
		newStarPane.setBorder(new EmptyBorder(0, 5, 5, 5));

		initiateFields();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = .05; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = .05;
		gbc.ipady = 20;
		JLabel xLabel = new JLabel("X Coord: ");
		xLabel.setFont(f);
		newStarPane.add(xLabel, gbc);
		gbc.weightx = .95;
		gbc.gridx = 1;
		newStarPane.add(xField, gbc);

		gbc.gridx = 0; gbc.gridy = 1;
		gbc.weightx = .05;
		JLabel yLabel = new JLabel("Y Coord: ");
		yLabel.setFont(f);
		newStarPane.add(yLabel, gbc);
		gbc.weightx = .95;
		gbc.gridx = 1;
		newStarPane.add(yField, gbc);

		gbc.gridx = 0; gbc.gridy = 2;
		gbc.weightx = .05;
		JLabel sizeLabel = new JLabel("Size: ");
		sizeLabel.setFont(f);
		newStarPane.add(sizeLabel, gbc);
		gbc.weightx = .95;
		gbc.gridx = 1;
		newStarPane.add(sizeField, gbc);

		container.add(newStarPane, BorderLayout.CENTER);

		add(container, BorderLayout.CENTER);
		//setPreferredSize(new Dimension(container.getPreferredSize().width, container.getPreferredSize().height + 25));
		setPreferredSize(container.getPreferredSize());
	}

	private void initiateFields()
	{
		xField = new JTextField();
		xField.setFont(f);
		xField.addActionListener(this);
		yField = new JTextField();
		yField.setFont(f);
		yField.addActionListener(this);
		sizeField = new JTextField();
		sizeField.setFont(f);
		sizeField.addActionListener(this);
	}

	private void addConfirmButtons()
	{
		JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonContainer.add(cancel);
		buttonContainer.add(addStar);

		addStar.setFocusable(false);
		cancel.setFocusable(false);

		cancel.addActionListener(this);
		addStar.addActionListener(this);

		add(buttonContainer, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == cancel)
			dispose();
		else if(source == addStar || source == xField || source == yField || source == sizeField)
		{
			boolean errorOnce = false;
			if(!xField.getText().equals("") && !yField.getText().equals("") && !sizeField.getText().equals(""))
			{
				try
				{
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					int size = Integer.parseInt(sizeField.getText());

					if(type == NEW_STAR)
						ce.addObject(new StarShape(x, y, size));
					else if(type == PROPS)
						ce.setObject(oIndex, new StarShape(x, y, size));

					dispose();
				} catch(NumberFormatException nfe)
				{
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error",
							JOptionPane.ERROR_MESSAGE);
					errorOnce = true;
				}
			}
			else
				if(!errorOnce)
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error",
							JOptionPane.ERROR_MESSAGE);
		}
	}
}
