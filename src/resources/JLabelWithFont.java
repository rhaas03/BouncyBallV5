package resources;

import java.awt.Font;
import javax.swing.*;

public class JLabelWithFont extends JLabel {
	public JLabelWithFont(String text, Font font) {
		super(text);
		this.setFont(font);
	}
	public JLabelWithFont(String text, int fontSize) {
		super(text);
		this.setFont(getFont().deriveFont((float)fontSize));;
	}
}
