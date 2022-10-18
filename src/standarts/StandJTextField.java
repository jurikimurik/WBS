package standarts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

//Standartyzowana klasa dla tworzenia przyciskow o jednym stylu
public class StandJTextField extends JTextField {
	public static final int TYLKO_TEKST = 1;
	public static final int TYLKO_LICZBY = 2;
	public static final Dimension ROZMIAR_PODSTAWOWY = new Dimension(100,25);
	
	//Limitowana ilosc liczb
	private int limit;
	
	//Tryb tego pola tekstowego
	private int tryb;
	
	StandJTextField(int mode) {
		//Rozmiar pola tekstowego
		this.setSize(ROZMIAR_PODSTAWOWY);
		this.setPreferredSize(ROZMIAR_PODSTAWOWY);
		
		//Ustawiamy tryb
		setMode(mode);
		
	}
	
	StandJTextField(int mode, int lim) {
		//Rozmiar panelu
		this.setSize(ROZMIAR_PODSTAWOWY);
		this.setPreferredSize(ROZMIAR_PODSTAWOWY);
		
		//Ustawiamy limit liczb
		setLimit(lim);
		
		//Ustawiamy tryb
		setMode(mode);
		
		//Ustawiamy dokument dla limitu oraz ograniczenia wprowadzania liczb
		this.setDocument(new JTextFieldLimit(lim));
	}

	private void setMode(int mode) {
		tryb = mode;
	}
	private void setLimit(int lim) {
		limit = lim;
	}
}

class JTextFieldLimit extends PlainDocument {
	   private int limit;
	   JTextFieldLimit(int limit) {
	      super();
	      this.limit = limit;
	   }
	   JTextFieldLimit(int limit, boolean upper) {
	      super();
	      this.limit = limit;
	   }
	   public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		   try {
			   Integer.parseInt(str);
			   if (str == null)
			         return;
			      if ((getLength() + str.length()) <= limit) {
			         super.insertString(offset, str, attr);
			    }
		   } catch (NumberFormatException ex) {
			   
		   }
		   
	      
	   }
	}
