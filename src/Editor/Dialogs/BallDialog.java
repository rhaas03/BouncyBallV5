package Editor.Dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import Editor.CustomEditor;
import resources.objects.BouncyBall;

public class BallDialog extends JDialog implements ActionListener
{
	private CustomEditor ce;

	private JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP);

	private int type;
	public static final int NEW_BALL = 1;
	public static final int PROPS = 2;

	private JButton addBall = new JButton("Add Ball");
	private JButton cancel = new JButton("Cancel");

	private Font f = new Font("", Font.PLAIN, 14);

	private BasicBallOptions bbo = new BasicBallOptions();
	private AdvancedBallOptions abo = new AdvancedBallOptions();

	private int oIndex = -1;

	private final String[] paneTitles = {
		"Basic",
		"Advanced"
	};

	public BallDialog(CustomEditor ce)
	{
		super(ce.frame, "New Ball", Dialog.ModalityType.APPLICATION_MODAL);
		this.ce = ce;
		this.type = NEW_BALL;

		basicOps();

		setLocation((ce.frame.getX() + ce.frame.getWidth()/2) - getPreferredSize().width/2,
				(ce.frame.getY() + ce.frame.getHeight()/2) - getPreferredSize().height/2);

		pack();
		setVisible(true);
	}

	public BallDialog(CustomEditor ce, int type, BouncyBall b, int oIndex)
	{
		super(ce.frame, "New Ball", Dialog.ModalityType.APPLICATION_MODAL);
		this.ce = ce;
		this.type = type;
		this.oIndex = oIndex;

		if(this.type == PROPS)
		{
			bbo.xField.setText(Integer.toString(b.getX()));
			bbo.yField.setText(Integer.toString(b.getY()));
			bbo.dField.setText(Integer.toString(b.getDiameter()));
			abo.rlField.setText(Double.toString(b.getRLSpeed()));
			abo.dusField.setText(Double.toString(b.getDefaultUpSpeed()));
		}

		basicOps();

		setLocation((ce.frame.getX() + ce.frame.getWidth()/2) - getPreferredSize().width/2,
				(ce.frame.getY() + ce.frame.getHeight()/2) - getPreferredSize().height/2);

		pack();
		setVisible(true);
	}

	private void basicOps()
	{
		setMinimumSize(new Dimension(300, 170));
		addPane();
		addConfirmButtons();

		bbo.xField.addActionListener(this);
		bbo.yField.addActionListener(this);
		bbo.dField.addActionListener(this);
		abo.rlField.addActionListener(this);
		abo.dusField.addActionListener(this);

		setResizable(false);
	}

	private void addConfirmButtons()
	{
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		cancel.setFocusable(false);
		cancel.addActionListener(this);

		addBall.setFocusable(false);
		addBall.addActionListener(this);

		buttonPane.add(cancel);
		buttonPane.add(addBall);

		add(buttonPane, BorderLayout.SOUTH);
	}

	public void addPane()
	{
		tab.setFocusable(false);
		tab.setTabPlacement(JTabbedPane.LEFT);
		tab.setOpaque(true);
		tab.setBackground(new Color(230, 230, 230));
		tab.add(paneTitles[0], bbo);
		tab.add(paneTitles[1], abo);
		add(tab, BorderLayout.CENTER);
	}

	private class BasicBallOptions extends JPanel
	{
		public JTextField xField = new JTextField();
		public JTextField yField = new JTextField();
		public JTextField dField = new JTextField();

		public BasicBallOptions()
		{
			xField.setFont(f);
			yField.setFont(f);
			dField.setFont(f);

			setOpaque(true);
			setLayout(new GridBagLayout());
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(2, 5, 2, 5);

			gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = .05; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weighty = .05;
			//gbc.ipady = 10;
			JLabel xLabel = new JLabel("X Coord: ");
			xLabel.setFont(f);
			add(xLabel, gbc);
			gbc.gridx = 1;
			gbc.weightx = .55;
			add(xField, gbc);

			gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = .05;
			JLabel yLabel = new JLabel("Y Coord: ");
			yLabel.setFont(f);
			add(yLabel, gbc);
			gbc.gridx = 1;
			gbc.weightx = .95;
			add(yField, gbc);

			gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = .05;
			JLabel dLabel = new JLabel("Diameter: ");
			dLabel.setFont(f);
			add(dLabel, gbc);
			gbc.gridx = 1;
			gbc.weightx = .95;
			add(dField, gbc);
		}
	}

	private class AdvancedBallOptions extends JPanel
	{
		public JTextField rlField = new JTextField();
		public JTextField dusField = new JTextField();

		public AdvancedBallOptions()
		{
			rlField.setFont(f);
			dusField.setFont(f);

			setOpaque(true);
			setLayout(new GridBagLayout());
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(2, 5, 2, 5);

			gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = .05; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weighty = .05;
			//gbc.ipady = 10;
			JLabel rlLabel = new JLabel("Right/Left Speed: ");
			rlLabel.setFont(f);
			add(rlLabel, gbc);
			gbc.gridx = 1;
			gbc.weightx = .55;
			add(rlField, gbc);

			gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = .05;
			JLabel dusLabel = new JLabel("Upwards Speed: ");
			dusLabel.setFont(f);
			add(dusLabel, gbc);
			gbc.gridx = 1;
			gbc.weightx = .95;
			add(dusField, gbc);
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == cancel)
			dispose();
		else if(source == addBall || source == bbo.xField || source == bbo.yField || source == bbo.dField ||
				source == abo.rlField || source == abo.dusField)
		{
			boolean showOnce = false;

			if(!bbo.xField.getText().equals("") || !bbo.yField.getText().equals("") || !bbo.dField.getText().equals(""))
			{
				try
				{
					int x = Integer.parseInt(bbo.xField.getText());
					int y = Integer.parseInt(bbo.yField.getText());
					int d = Integer.parseInt(bbo.dField.getText());

					double rlSpeed, upSpeed;

					if(!abo.rlField.getText().equals(""))
						rlSpeed = Double.parseDouble(abo.rlField.getText());
					else
						rlSpeed = BouncyBall.STANDARD_RLSPEED;

					if(!abo.dusField.getText().equals(""))
						upSpeed = Double.parseDouble(abo.dusField.getText());
					else
						upSpeed = BouncyBall.STANDARD_DEFALT_UPSPEED;

					if(type == NEW_BALL)
						ce.addObject(new BouncyBall(x, y, d, Color.BLUE, rlSpeed, upSpeed));
					else if(type == PROPS)
						ce.setObject(oIndex, new BouncyBall(x, y, d, Color.BLUE, rlSpeed, upSpeed));
					dispose();
				} catch(NumberFormatException nfe)
				{
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error",
							JOptionPane.ERROR_MESSAGE);
					showOnce = true;
				}
			}
			else
				if(!showOnce)
					JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error",
							JOptionPane.ERROR_MESSAGE);
		}
	}
}
