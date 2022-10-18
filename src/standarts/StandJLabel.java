package standarts;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;

//Standartyzowana klasa dla tworzenia przyciskow o jednym stylu
public class StandJLabel extends JLabel {
	
	//Etykieta głowna
	public static final int ETYKIETA_GLOWNA = 1;
	public static final Dimension ROZMIAR_ETYKIETOW_GLOWNYCH = new Dimension(200,50);
	public static final Font CZCIONKA_ETYKIET_GLOWNYCH = new Font("Chalkduster", Font.BOLD, 25);
	
	//Etykieta zwykła
	public static final int ETYKIETA_ZWYKLA = 2;
	public static final Dimension ROZMIAR_ETYKIETY_ZWYKLEJ = new Dimension(100,50);
	public static final Font CZCIONKA_ETYKIETY_ZWYKLEJ = new Font("Chalkduster", Font.PLAIN, 15);
	
	public static final boolean NIE_NADAWAJ_ROZMIARU = false;
	public static final boolean NADAJ_ROZMIAR = true;
	
	//Etykieta bez rozmiarow
	public StandJLabel(String str, int mode, boolean needToSize) {
		this.setText(str);
		setDefaultLook(mode, needToSize);
	}
	
	public StandJLabel(String str, int mode) {
		this.setText(str);
		setDefaultLook(mode, true);
	}
	
	public StandJLabel(int mode) {
		this.setText("");
		setDefaultLook(mode, true);
	}
	
	
	public void setDefaultLook(int mode, boolean needToSize) {
		if (mode == ETYKIETA_GLOWNA) {
			if(needToSize) {
				this.setSize(ROZMIAR_ETYKIETOW_GLOWNYCH);
				this.setPreferredSize(ROZMIAR_ETYKIETOW_GLOWNYCH);
			}
			this.setFont(CZCIONKA_ETYKIET_GLOWNYCH);
		} else if (mode == ETYKIETA_ZWYKLA) {
			if(needToSize) {
				this.setSize(ROZMIAR_ETYKIETY_ZWYKLEJ);
				this.setPreferredSize(ROZMIAR_ETYKIETY_ZWYKLEJ);
			}
			this.setFont(CZCIONKA_ETYKIETY_ZWYKLEJ);
		}
	}
	
	
}