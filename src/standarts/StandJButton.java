package standarts;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

//Standartyzowana klasa dla tworzenia przyciskow o jednym stylu
public class StandJButton extends JButton {
	
	//Przycisk głowny
	public static final int PRZYCISK_GLOWNY = 1;
	public static final Dimension ROZMIAR_PRZYCISKOW_GLOWNYCH = new Dimension(200,100);
	public static final Font CZCIONKA_PRZYCISK_GLOWNY = new Font("Chalkduster", Font.BOLD, 20);
	
	//Przycisk zwykły
	public static final int PRZYCISK_ZWYKLY = 2;
	public static final Dimension ROZMIAR_PRZYCISKOW_ZWYKLYCH = new Dimension(150,50);
	public static final Font CZCIONKA_PRZYCISKOW_ZWYKLYCH = new Font("Chalkduster", Font.BOLD, 15);
	
	
	//przycisk bez rozmiarow
	public StandJButton(String str, int mode, boolean needToSize) {
		this.setText(str);
		setDefaultLook(mode, needToSize);
	}
	
	public StandJButton(String str, int mode) {
		this.setText(str);
		setDefaultLook(mode, true);
	}
	
	public StandJButton(int mode) {
		this.setText("");
		setDefaultLook(mode, true);
	}
	
	
	private void setDefaultLook(int mode, boolean needToSize) {
		if (mode == PRZYCISK_GLOWNY) {
				if(needToSize) {
					this.setSize(ROZMIAR_PRZYCISKOW_GLOWNYCH);
					this.setPreferredSize(ROZMIAR_PRZYCISKOW_GLOWNYCH);
				}
			this.setFont(CZCIONKA_PRZYCISK_GLOWNY);
		} else if (mode == PRZYCISK_ZWYKLY) {
				if(needToSize) {
					this.setSize(ROZMIAR_PRZYCISKOW_ZWYKLYCH);
					this.setPreferredSize(ROZMIAR_PRZYCISKOW_ZWYKLYCH);
				}
			this.setFont(CZCIONKA_PRZYCISKOW_ZWYKLYCH);
		}
	}
	
	
}