package Editor.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.util.ArrayList;

import Editor.CustomEditor;
import Editor.Platforms.*;
import resources.objects.Platform;

public class PlatformDialog extends JDialog implements ActionListener
{
	private CustomEditor ce;

	private String[] options = {
		"Standard",
		"Boost",
		"Moving"
	};
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox platSelector = new JComboBox(options);

	private CardLayout platController = new CardLayout();
	private JPanel platContainer = new JPanel(platController);

	private StandardPlatform sp;
	private BoostPlatform bp;
	private MovingPlatform mp;

	private JButton addPlat = new JButton("Add Platform");
	private JButton cancel = new JButton("Cancel");

	private int type;
	public static final int NEW_PLAT = 1;
	public static final int PROPS = 2;

	private int oIndex = -1;

	private ArrayList<JTextField> fields = new ArrayList<JTextField>();

	public PlatformDialog(CustomEditor ce)
	{
		super(ce.frame, "New Platform", Dialog.ModalityType.APPLICATION_MODAL);
		this.ce = ce;
		this.type = NEW_PLAT;
		basicOps();

		platContainer.setPreferredSize(platContainer.getComponent(platSelector.getSelectedIndex()).getPreferredSize());
		setLocation((ce.frame.getX() + ce.frame.getWidth()/2) - getPreferredSize().width/2,
				(ce.frame.getY() + ce.frame.getHeight()/2) - getPreferredSize().height/2);

		pack();
		setVisible(true);
	}

	public PlatformDialog(CustomEditor ce, int type, Platform p, int oIndex)
	{
		super(ce.frame, Dialog.ModalityType.APPLICATION_MODAL);
		if(type == NEW_PLAT)
			setTitle("New Platform");
		else if(type == PROPS)
			setTitle("Platform Properties");

		this.ce = ce;
		this.oIndex = oIndex;
		this.type = type;

		basicOps();

		if(p.getType() == Platform.STANDARD)
		{
			if(type == PROPS)
				sp.setValues(new int[]{p.getX(), p.getY(), p.getW(), p.getH()});
			platSelector.setSelectedItem(options[0]);
			platController.show(platContainer, options[0]);
			platContainer.setPreferredSize(sp.getSize());
		}
		else if(p.getType() == Platform.BOOST)
		{
			if(type == PROPS)
				bp.setValues(new Object[]{p.getX(), p.getY(), p.getW(), p.getH(), p.getBoost()});
			platSelector.setSelectedItem(options[1]);
			platController.show(platContainer, options[1]);
			platContainer.setPreferredSize(bp.getSize());
		}
		else if(p.getType() == Platform.MOVING_HORIZONTAL)
		{
			if(type == PROPS)
			{
				mp.setValues(new int[]{p.getX() - p.getH(), p.getY(), p.getW(), p.getH(), p.getOtherCoord()});
				mp.setMoveType(MovingPlatform.options[0]);
			}
			platSelector.setSelectedItem(options[2]);
			platController.show(platContainer, options[2]);
			platContainer.setPreferredSize(bp.getSize());
		}
		else if(p.getType() == Platform.MOVING_VERTICAL)
		{
			if(type == PROPS)
			{
				mp.setValues(new int[]{p.getX(), p.getY() - p.getH(), p.getW(), p.getH(), p.getOtherCoord()});
				mp.setMoveType(MovingPlatform.options[1]);
			}
			platSelector.setSelectedItem(options[2]);
			platController.show(platContainer, options[2]);
			platContainer.setPreferredSize(bp.getSize());
		}

		add(platContainer);
		platContainer.setPreferredSize(platContainer.getComponent(platSelector.getSelectedIndex()).getPreferredSize());
		setLocation((ce.frame.getX() + ce.frame.getWidth()/2) - getPreferredSize().width/2,
				(ce.frame.getY() + ce.frame.getHeight()/2) - getPreferredSize().height/2);

		pack();
		setVisible(true);
	}

	private void basicOps()
	{
		setMinimumSize(new Dimension(300, 100));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		addSelector();
		addPanels();
		addConfirmButtons();

		for(JTextField tf : sp.getFields())
		{
			tf.addActionListener(this);
			fields.add(tf);
		}
		for(JTextField tf : bp.getFields())
		{
			tf.addActionListener(this);
			fields.add(tf);
		}
		for(JTextField tf : mp.getFields())
		{
			tf.addActionListener(this);
			fields.add(tf);
		}

		setResizable(false);
	}

	private void addSelector()
	{
		JPanel selectPane = new JPanel();
		selectPane.setLayout(new BoxLayout(selectPane, BoxLayout.X_AXIS));
		//selectPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		JLabel selectLabel = new JLabel("Platform Type: ");
		selectLabel.setFont(new Font("", Font.PLAIN, 14));
		selectPane.add(selectLabel);
		selectPane.add(platSelector);

		platSelector.setFont(selectLabel.getFont());
		platSelector.setFocusable(false);
		platSelector.addActionListener(this);

		if(this.type == PROPS)
			platSelector.setEnabled(false);
		selectPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		add(selectPane, BorderLayout.NORTH);
	}

	private void addPanels()
	{
		sp = new StandardPlatform();
		bp = new BoostPlatform();
		mp = new MovingPlatform();
		int inset = 5;

		platContainer.add(sp, options[0]);
		platContainer.add(bp, options[1]);
		platContainer.add(mp, options[2]);
		TitledBorder tb = BorderFactory.createTitledBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK), "Properties");
		tb.setTitleFont(tb.getTitleFont().deriveFont(16f));
		platContainer.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(inset, inset, inset, inset),
				tb));
		platController.show(platContainer, options[0]);
		platContainer.setPreferredSize(sp.getSize());

		add(platContainer, BorderLayout.CENTER);
	}

	private void addConfirmButtons()
	{
		JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonContainer.add(cancel);
		buttonContainer.add(addPlat);

		addPlat.setFocusable(false);
		cancel.setFocusable(false);

		cancel.addActionListener(this);
		addPlat.addActionListener(this);

		add(buttonContainer, BorderLayout.SOUTH);
	}

	private void buttonFunctions(Object source)
	{
		for(JTextField tf : fields)
		{
			if(source == cancel)
				dispose();
			else if(source == addPlat || source == tf)
			{
				if(platSelector.getSelectedItem().equals(options[0]))
				{
					JTextField xField = sp.getFields()[0];
					JTextField yField = sp.getFields()[1];
					JTextField wField = sp.getFields()[2];
					JTextField hField = sp.getFields()[3];

					boolean errorOnce = false;

					if(!xField.getText().equals("") && !yField.getText().equals("") && !wField.getText().equals("") && !hField.getText().equals(""))
					{
						try
						{
							int x = Integer.parseInt(xField.getText());
							int y = Integer.parseInt(yField.getText());
							int w = Integer.parseInt(wField.getText());
							int h = Integer.parseInt(hField.getText());

							if(type == NEW_PLAT)
								ce.addObject(new Platform(x, y, w, h, Platform.STANDARD));
							else if(type == PROPS)
								ce.setObject(oIndex, new Platform(x, y, w, h, Platform.STANDARD));
							dispose();
						} catch(NumberFormatException nfe)
						{
							JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error",
									JOptionPane.ERROR_MESSAGE);
							errorOnce = true;
						}
					}
					else
					{
						if(!errorOnce)
							JOptionPane.showMessageDialog(this, "Empty Field or Improper Character", "Error",
									JOptionPane.ERROR_MESSAGE);
						System.out.println("asfa");
					}

					break;
				}
				else if(platSelector.getSelectedItem().equals(options[1]))
				{
					JTextField xField = bp.getFields()[0];
					JTextField yField = bp.getFields()[1];
					JTextField wField = bp.getFields()[2];
					JTextField hField = bp.getFields()[3];
					JTextField boostField = bp.getFields()[4];

					boolean errorOnce = false;

					if(!xField.getText().equals("") && !yField.getText().equals("") && !wField.getText().equals("") && !hField.getText().equals("")
							&& !boostField.getText().equals(""))
					{
						try
						{
							int x = Integer.parseInt(xField.getText());
							int y = Integer.parseInt(yField.getText());
							int w = Integer.parseInt(wField.getText());
							int h = Integer.parseInt(hField.getText());
							double b = Double.parseDouble(boostField.getText());

							if(type == NEW_PLAT)
								ce.addObject(new Platform(x, y, w, h, Platform.BOOST, b));
							else if(type == PROPS)
								ce.setObject(oIndex, new Platform(x, y, w, h, Platform.BOOST, b));
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

					break;
				}
				else if(platSelector.getSelectedItem().equals(options[2]))
				{
					JTextField xField = mp.getFields()[0];
					JTextField yField = mp.getFields()[1];
					JTextField wField = mp.getFields()[2];
					JTextField hField = mp.getFields()[3];
					JTextField otherCoordField = mp.getFields()[4];

					boolean errorOnce = false;

					if(!xField.getText().equals("") && !yField.getText().equals("") && !wField.getText().equals("") && !hField.getText().equals("")
							&& !otherCoordField.getText().equals(""))
					{
						try
						{
							int x = Integer.parseInt(xField.getText());
							int y = Integer.parseInt(yField.getText());
							int w = Integer.parseInt(wField.getText());
							int h = Integer.parseInt(hField.getText());
							int oC = Integer.parseInt(otherCoordField.getText());
							String moveType = mp.getMoveType();

							if(type == NEW_PLAT)
							{
								if(moveType.equals(MovingPlatform.options[0]))
									ce.addObject(new Platform(x, y, w, h, Platform.MOVING_HORIZONTAL, oC));
								else if(moveType.equals(MovingPlatform.options[1]))
									ce.addObject(new Platform(x, y, w, h, Platform.MOVING_VERTICAL, oC));
							}
							else if(type == PROPS)
							{
								if(moveType.equals(MovingPlatform.options[0]))
									ce.setObject(oIndex, new Platform(x, y, w, h, Platform.MOVING_HORIZONTAL, oC));
								else if(moveType.equals(MovingPlatform.options[1]))
									ce.setObject(oIndex, new Platform(x, y, w, h, Platform.MOVING_VERTICAL, oC));
							}
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

					break;
				}
			}
			else if(source == platSelector)
			{
				platController.show(platContainer, (String)platSelector.getSelectedItem());
				platContainer.setPreferredSize(platContainer.getComponent(platSelector.getSelectedIndex()).getPreferredSize());
				pack();
			}
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		buttonFunctions(e.getSource());
	}
}
