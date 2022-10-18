package standarts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.WBSDatabase;

//Standartyzowana klasa dla tworzenia przyciskow o jednym stylu
public class InputJPanel extends JPanel {
	public static final int TYLKO_TEKST = 1;
	public static final int TYLKO_LICZBY = 2;
	public static final Dimension ROZMIAR_PODSTAWOWY = new Dimension(200,80);
	public static final int BEZ_LIMITU = 0;
	public static final int LIMIT_DWIE_LICZBY = 2;
	public static final int LIMIT_TRZY_LICZBY = 3;
	
	//Standardy nazw
	public static final String COUNT_WORD = "Ilość";
	public static final String WARRIOR_WORD = "Wojownik";
	public static final String AGILITY_WORD = "Zreczność";
	public static final String INITIATIVE_WORD = "Inicjatywa";
		
	public static final String SKILL_WORD = "Umiejetność";
	public static final String SKILL_BOOST_WORD = "Liczba boostu";
	public static final String NUMBER_OF_ROUNDS_WORD = "Ilość rund";
	
	//Panele z komponentami
	private JPanel panelGorny;
	private JPanel panelCentralny;
	
	//Tryb wprowadzania
	private int inputMode;
	
	//Pole tekstowe i etykieta
	StandJLabel gornaEtykieta;
	StandJTextField poleTekstowe;
	
	//nasłuchiwacz przy wyborze wojownika
	WarriorChoosedListener listener = null;
	
	
	
	 public void setWarriorChoosedListener(WarriorChoosedListener listener) {
	    	this.listener = listener;
	 }
	
	public InputJPanel(String opis, WarriorChoosedListener listener2) {
		this.setWarriorChoosedListener(listener2);
		
		//Rozmiar panelu
		this.setSize(ROZMIAR_PODSTAWOWY);
		this.setPreferredSize(ROZMIAR_PODSTAWOWY);
				
		//Pionowy widok rzeczy
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(layout);
				
		//Tworzymy gorną etykietę oraz pole tekstowe
		gornaEtykieta = new StandJLabel(opis, StandJLabel.ETYKIETA_ZWYKLA, false);
		
	    poleTekstowe = new StandJTextField(StandJTextField.TYLKO_TEKST);
		
	  //Dodajemy caret który nie pozwoli na zaznaczanie tekstu
	    poleTekstowe.setCaret(new NoTextSelectionCaret(poleTekstowe));
	    
		AutoSuggestor poleTekstoweZLista = new AutoSuggestor(poleTekstowe, null, Color.WHITE.brighter(), Color.BLUE, Color.RED, 0.75f) {
			 @Override
	            boolean wordTyped(String typedWord) {

	                //create list for dictionary this in your case might be done via calling a method which queries db and returns results as arraylist
				 	
				 	//Tworzymy lub pobieramy znaczenia z bazy danych (operujemy na niej)
				 	WBSDatabase database = new WBSDatabase();
				 
	                ArrayList<String> words = database.getAllWarriorsNames();
	                setDictionary(words);
	                //addToDictionary("bye");//adds a single word

	                return super.wordTyped(typedWord);//now call super to check for any matches against newest dictionary
	            }
		};
		
		//Ustawiamy nasłuchiwacz
		poleTekstoweZLista.setWarriorChoosedListener(listener2);
		
		poleTekstowe = poleTekstoweZLista.getStandJTextField();
		
		 //Dodajemy komponent do osobego panelu w celu ich możliwego powiększenia w przyszłości
	    panelGorny = new JPanel(new FlowLayout(FlowLayout.LEADING));
	    panelGorny.add(gornaEtykieta);
	    panelCentralny = new JPanel(new FlowLayout(FlowLayout.LEADING));
	    panelCentralny.add(poleTekstoweZLista);
	   
	    
	    //Dodajemy komponenty do BoxLayout
	    this.add(panelGorny);
	    this.add(panelCentralny);
	}
	
	public InputJPanel(int mode, String opis, int lim) {
		
		
		//Rozmiar panelu
		this.setSize(ROZMIAR_PODSTAWOWY);
		this.setPreferredSize(ROZMIAR_PODSTAWOWY);
		
		//Zapisujemy tryb wprowadzania
		this.setInputMode(mode);
		
		//Pionowy widok rzeczy
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
	    this.setLayout(layout);
		
	    //Tworzymy gorną etykietę oraz pole tekstowe
	    gornaEtykieta = new StandJLabel(opis, StandJLabel.ETYKIETA_ZWYKLA, false);
	    
	    	//Jeżeli potrzebujamy limit - istawiamy go
		    if (!(lim == BEZ_LIMITU)) {
		    	poleTekstowe = new StandJTextField(mode, lim);
		    } else {
		    	poleTekstowe = new StandJTextField(mode);
		    }
		  //Dodajemy komponent do osobego panelu w celu ich możliwego powiększenia w przyszłości
		    panelCentralny = new JPanel(new FlowLayout(FlowLayout.LEADING));
		    panelCentralny.add(poleTekstowe);
	    
	    	
	    
	    //Dodajemy komponent do osobego panelu w celu ich możliwego powiększenia w przyszłości
	    panelGorny = new JPanel(new FlowLayout(FlowLayout.LEADING));
	    panelGorny.add(gornaEtykieta);
	   
	    
	    //Dodajemy komponenty do BoxLayout
	    this.add(panelGorny);
	    this.add(panelCentralny);
	}
	
	

	public void addRandomiserJButton() {
		//Sprawdzamy czy przycisk spełnia ktyteria: wprowadzanie tylko liczb
		if (inputMode == InputJPanel.TYLKO_LICZBY) {
			//Tworzymy przycisk z ikonką kostki który losuje liczbę od 1 do 10 i zapisuje to w polu tekstowym.
			
			//Graficzny obrazek
			Icon ikonka = new ImageIcon("icns/simple-dice-20.png");
			JButton przyciskLosujacy = new JButton(ikonka);
			
			//Działania losujące i zapisujące wynik w TextField tego Panelu
			przyciskLosujacy.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					newRandomInitiative();
					
				}
				
			});
			
			
			panelCentralny.add(przyciskLosujacy);
			panelCentralny.revalidate();
			panelCentralny.repaint();
			
			
		} else {
			switch(inputMode) {
			case TYLKO_TEKST: JOptionPane.showMessageDialog(null, "Nie można stworzyć losowego przycisku dla trybu wprowadzania: " + "TYLKO_TEKST");
			}
		}
	}
	
	private void setInputMode(int mode) {
		inputMode = mode;
	}
	
	
	public StandJTextField getStandJTextField() {
		return poleTekstowe;
	}
	
	public String getStandJLabelText() {
		return gornaEtykieta.getText();
	}
	
	public void newRandomInitiative() {
		int random = (int) ((Math.random() * 10) + 1);
		poleTekstowe.setText(String.valueOf(random));
	}
}