package BouncyBallSource;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import resources.JLabelWithFont;
import resources.RichJLabel;

public class Help extends JPanel
{
	private BouncyBallV5 bb5;
	private Font titleFont;

	private final String objectiveText =
			"    The objective of BouncyBall is to collect all the yellow stars on the screen. As you complete more"
			+ " levels, they increase in difficulty in several ways, such as new platforms and obstacles and more stars."
			+ "";

	public Help(BouncyBallV5 b)
	{
		bb5 = b;
		titleFont = bb5.comfortaa.deriveFont(Font.PLAIN, 22f);
		setBackground(BouncyBallV5.BG_COLOR);
		createLayout();
	}

	private void createLayout()
    {
    	setLayout(new GridBagLayout());
    	//getContentPane().setBackground(Color.YELLOW);
    	GridBagConstraints allGBC = new GridBagConstraints();
    	allGBC.gridx = 0;
    	allGBC.gridy = 0;
    	allGBC.insets = new Insets(5, 5, 5, 5);
    	allGBC.anchor = GridBagConstraints.NORTH;
    	allGBC.fill = GridBagConstraints.BOTH;
    	allGBC.weighty = .5;
    	allGBC.weightx = .5;

    	JPanel leftSide = new JPanel(new BorderLayout());
    	leftSide.setOpaque(false);
    	leftSide.setBorder(new EmptyBorder(5, 10, 2, 20));
    	RichJLabel leftTitle = new RichJLabel("Objective");
    	leftTitle.setFont(titleFont);
    	leftTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
    	leftSide.add(leftTitle, BorderLayout.NORTH);

    	JTextPane objectivePane = new JTextPane();
    	objectivePane.setText(objectiveText);
    	objectivePane.setEditable(false);
    	objectivePane.setFocusable(false);
    	objectivePane.setFont(new Font("", Font.PLAIN, 16));
    	objectivePane.setOpaque(false);
    	objectivePane.setMargin(new Insets(10, 5, 5, 5));

    	leftSide.add(objectivePane, BorderLayout.CENTER);

    	add(leftSide, allGBC);

    	allGBC.gridx = 1;
    	allGBC.anchor = GridBagConstraints.NORTH;
    	allGBC.fill = GridBagConstraints.HORIZONTAL;
    	allGBC.weighty = 0;
    	allGBC.weightx = 0;

    	JPanel rightSide = new JPanel(new BorderLayout());
    	rightSide.setOpaque(false);
    	rightSide.setBorder(new EmptyBorder(5, 0, 0, 10));
    	RichJLabel rightTitle = new RichJLabel("Controls");
    	rightTitle.setFont(titleFont);
    	rightTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
    	rightSide.add(rightTitle, BorderLayout.NORTH);

    	JPanel controlPanel = new JPanel();
    	controlPanel.setOpaque(false);
    	BoxLayout bl = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
    	controlPanel.setLayout(bl);
    	controlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

    	Font keyFont = new Font("", Font.BOLD, 18);
    	Font descFont = new Font("", Font.PLAIN, 16);

    	JPanel aLA = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	aLA.setOpaque(false);
    	aLA.add(new JLabelWithFont("A or Left Arrow: ", keyFont));
    	aLA.add(new JLabelWithFont("Moves ball left", descFont));

    	JPanel dAR = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	dAR.setOpaque(false);
    	dAR.add(new JLabelWithFont("D or Right Arrow: ", keyFont));
    	dAR.add(new JLabelWithFont("Moves ball right", descFont));

    	JPanel bQ = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	bQ.setOpaque(false);
    	bQ.add(new JLabelWithFont("Tilde  (~): ", keyFont));
    	bQ.add(new JLabelWithFont("Opens console", descFont));

    	controlPanel.add(aLA);
    	controlPanel.add(dAR);
    	controlPanel.add(bQ);

    	rightSide.add(controlPanel, BorderLayout.CENTER);

    	add(rightSide, allGBC);
    }
}
