package standarts;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;

//Standartyzowana klasa dla tworzenia przyciskow o jednym stylu
public class StandFont extends Font {
	public StandFont(String name, int style, int size) {
		super(name, style, size);
		// TODO Auto-generated constructor stub
	}
	
	public StandFont() {
		super("Chalkduster", Font.PLAIN, 15);
	}
}