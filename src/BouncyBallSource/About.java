package BouncyBallSource;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import resources.JLabelWithFont;

public class About extends JPanel
{
	private BouncyBallV5 bb5;

	//IF BROKEN ON WINDOWS
	//set gbc.weightx = 0;

	private final String aboutText =
			"My BouncyBall game is largely modeled off of the very popular iOS and Android mobile game 'Bounce Ball'."
			+ "It was orignally going to be a small personal project that I expected to last a few days, but after a while I became"
			+ "very invested in it's development. It was primarily, and still is, a way for me to continue expanding my knowledge of"
			+ "Java, and to keep me from getting rusty with the language. I began working on BouncyBall Version 1 on March 17, 2016."
			+ "Since then I have completely rewritten the game several times, and have come a long way since V1. It is a project"
			+ "that I have become very proud of, and has been my most involved project. While tedious, once I've come as far"
			+ "as I think I can on a certain version I completely start over. This is because I know that I've comed a long way"
			+ "since I first started working on that Version, and can make the code better if I am to completely rewrite it."
			+ "I hope you enjoy playing!";

	public About(BouncyBallV5 b)
	{
		//super(new GridLayout(1, 2, 5, 0));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		//gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;

		setLayout(new GridBagLayout());
		setBackground(BouncyBallV5.BG_COLOR);

		this.bb5 = b;
		setBorder(new EmptyBorder(5, 5, 5, 5));

		JTextArea aboutArea = new JTextArea(aboutText);
		aboutArea.setEditable(false);
		aboutArea.setFocusable(false);
		aboutArea.setFont(bb5.samsung1.deriveFont(16f));
		//aboutArea.setBackground(getBackground());
		aboutArea.setLineWrap(true);
		aboutArea.setWrapStyleWord(true);
		aboutArea.setMargin(new Insets(5, 10, 5, 15));

		JScrollPane scrollPane = new JScrollPane(aboutArea);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		//scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(5);
		//gbc.gridwidth = 4;
		add(scrollPane, gbc);

		gbc.gridx = 2;
		gbc.weightx = .05;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridwidth = 1;

		JPanel information = new JPanel();
		information.setBorder(new EmptyBorder(5, 5, 5, 5));
		information.setLayout(new BoxLayout(information, BoxLayout.PAGE_AXIS));
		information.setBackground(Color.WHITE);

		JPanel version = new JPanel(new FlowLayout(FlowLayout.LEFT));
		version.add(new JLabelWithFont("Version: ", aboutArea.getFont().deriveFont(Font.BOLD)));
		version.add(new JLabelWithFont(BouncyBallV5.VERSION, aboutArea.getFont()));
		version.setOpaque(false);

		JPanel creator = new JPanel(new FlowLayout(FlowLayout.LEFT));
		creator.add(new JLabelWithFont("Creator: ", aboutArea.getFont().deriveFont(Font.BOLD)));
		creator.add(new JLabelWithFont("Ryan Haas", aboutArea.getFont()));
		creator.setOpaque(false);

		JPanel link = new JPanel(new FlowLayout(FlowLayout.LEFT));
		link.add(new JLabelWithFont("Link to all Downloads: ", aboutArea.getFont().deriveFont(Font.BOLD)));
		link.setOpaque(false);
		final JLabel dLink = new JLabel("Downloads");
		dLink.setFont(aboutArea.getFont());
		dLink.setForeground(new Color(0, 125, 255));
		dLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
		dLink.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				try
				{
					openWebpage(new URL("http://ryanhaas.weebly.com/bouncy-ball.html"));
					bb5.log("Open Website");
				} catch (MalformedURLException e1)
				{
					e1.printStackTrace();
					bb5.log("Error opening website");
				}
			}

			@SuppressWarnings("unchecked")
			public void mouseEntered(MouseEvent e)
			{
				dLink.setForeground(Color.BLUE);
				@SuppressWarnings("rawtypes")
				Map attributes = dLink.getFont().getAttributes();
				attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
				dLink.setFont(dLink.getFont().deriveFont(attributes));
			}

			@SuppressWarnings("unchecked")
			public void mouseExited(MouseEvent e)
			{
				dLink.setForeground(new Color(0, 109, 255));
				@SuppressWarnings("rawtypes")
				Map attributes = dLink.getFont().getAttributes();
				attributes.put(TextAttribute.UNDERLINE, null);
				dLink.setFont(dLink.getFont().deriveFont(attributes));
			}
		});
		link.add(dLink);

		JPanel lastUpdate = new JPanel(new FlowLayout(FlowLayout.LEFT));
		lastUpdate.add(new JLabelWithFont("Last Updated: ", aboutArea.getFont().deriveFont(Font.BOLD)));
		lastUpdate.add(new JLabelWithFont("September 18, 2016", aboutArea.getFont()));
		lastUpdate.setOpaque(false);

		information.add(version);
		information.add(creator);
		information.add(link);
		information.add(lastUpdate);

		add(information, gbc);

		//setVisible(false);
	}

	public static void openWebpage(URI uri)
	{
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
	    {
	        try
	        {
	            desktop.browse(uri);
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	}

	public static void openWebpage(URL url)
	{
	    try
	    {
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e)
	    {
	        e.printStackTrace();
	    }
	}
}
