package frames;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import standarts.InputJPanel;
import standarts.StandJButton;
import standarts.StandJLabel;
import standarts.StandJTextField;
import standarts.WarriorChoosedListener;
import standarts.AutoSuggestor;

import javax.swing.SwingConstants;

import data.WBSDatabase;

public class AddWarriorFrame {

	private JFrame frame;
	
	//Standard rozmiarów
	public static final int FRAME_STANDARD_WIDTH = 900;
	public static final int FRAME_STANDARD_HEIGHT = 250;
	
	
	
	
	//Inna klasa związana z tą klasą
	private BattleFrame btlFrameClass = null;
	
	private JPanel panelCentralny;
	
	//Zbiór z polami tekstowymi, ktore da się odzyskać za pomocą klucza
	private HashMap<String, StandJTextField> polaTekstowe = new HashMap<String, StandJTextField>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Rectangle r = new Rectangle(100,100,FRAME_STANDARD_WIDTH,FRAME_STANDARD_HEIGHT);
					AddWarriorFrame window = new AddWarriorFrame(r);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AddWarriorFrame(Rectangle r) {
		initialize(r);
	}
	
	public AddWarriorFrame() {
		Rectangle r = new Rectangle(100,100,FRAME_STANDARD_WIDTH,FRAME_STANDARD_HEIGHT);
		initialize(r);
	}

	public AddWarriorFrame(BattleFrame battleFrame) {
		this.setBattleClass(battleFrame);
		Rectangle r = new Rectangle(100,100,FRAME_STANDARD_WIDTH,FRAME_STANDARD_HEIGHT);
		initialize(r);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(Rectangle r) {
		frame = new JFrame();
		frame.setBounds(r);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		//Tworzymy połączenie jeśli go niema
				if(btlFrameClass == null) {
					btlFrameClass = new BattleFrame(this);
				}
		
		//Gorny napis
		StandJLabel lblGornyOpis = new StandJLabel("Wprowadz wojownika", StandJLabel.ETYKIETA_GLOWNA);
		lblGornyOpis.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblGornyOpis, BorderLayout.NORTH);
		
		//Centralna część
		panelCentralny = new JPanel(); frame.getContentPane().add(panelCentralny);
		
		//Tworzymy obszar dla wprowadzania ilości wojowników
		InputJPanel inputCountPanel = new InputJPanel(InputJPanel.TYLKO_LICZBY, InputJPanel.COUNT_WORD, InputJPanel.LIMIT_DWIE_LICZBY);
			//Dodajemy do zbioru pole tekstowe tego obszaru
			polaTekstowe.put(inputCountPanel.getStandJLabelText(), inputCountPanel.getStandJTextField());
			//Ustawiamy domyślną liczbę wojowników
			inputCountPanel.getStandJTextField().setText("1");
		panelCentralny.add(inputCountPanel);
		
		//Tworzymy obszar dla wprowazania wojownika
			//Stara wersja
		/*InputJPanel inputCharacterPanel = new InputJPanel(InputJPanel.TYLKO_TEKST, InputJPanel.WARRIOR_WORD, InputJPanel.BEZ_LIMITU);
			//Dodajemy do zbioru pole tekstowe tego obszaru
			polaTekstowe.put(inputCharacterPanel.getStandJLabelText(), inputCharacterPanel.getStandJTextField());
		panelCentralny.add(inputCharacterPanel);*/
		
		
		//Tworzymy nasłuchujące zdarzenie
		WarriorChoosedListener listener = new WarriorChoosedListener() {

        	@Override
			public void onWarriorChoosed(String warrior) {
        		
        		//Tworzymy połączenie z aktualną bazą danych
				WBSDatabase database = new WBSDatabase();
				//Pobieramy konfiguracje potrzebnego wojownika
				Object[] configurations = database.getWarriorConfigurations(warrior);
				//Dla lepszego rozumienia kodu tworzymy odwolanie do pola tekstowego szybkośći oraz inicjatywy
				StandJTextField poleTekstoweSzybkości = polaTekstowe.get(InputJPanel.AGILITY_WORD);
				StandJTextField poleTekstoweInicjatywy = polaTekstowe.get(InputJPanel.INITIATIVE_WORD);
				
				//Ustawiamy tekst z bazy danych 
				poleTekstoweSzybkości.setText(String.valueOf(configurations[WBSDatabase.AGILITY_INDEX]));
				
				//Dajemy fokus na pole inicjatywy dla lepszego wprowadzania
				if(!poleTekstoweInicjatywy.requestFocusInWindow()) {
					poleTekstoweInicjatywy.requestFocus();
				}
				
			}
        	
        };
        //Tworzymy unikalny(!) obszar dla wprowazania wojownika przekazując mu również nasłuchiwacz
		InputJPanel inputCharacterPanel = new InputJPanel(InputJPanel.WARRIOR_WORD, listener);
			polaTekstowe.put(inputCharacterPanel.getStandJLabelText(), inputCharacterPanel.getStandJTextField());
			//Ustawiamy nasłuchiwacz
			inputCharacterPanel.setWarriorChoosedListener(listener);
		panelCentralny.add(inputCharacterPanel);
		
		//Tworzymy obszar dla wprowadzania zręczności
		InputJPanel inputAgilityPanel = new InputJPanel(InputJPanel.TYLKO_LICZBY, InputJPanel.AGILITY_WORD, InputJPanel.LIMIT_TRZY_LICZBY);
			//Dodajemy do zbioru pole tekstowe tego obszaru
			polaTekstowe.put(inputAgilityPanel.getStandJLabelText(), inputAgilityPanel.getStandJTextField());
		panelCentralny.add(inputAgilityPanel);
		
		//Tworzymy obszar dla wprowadzania inicjatywy
		InputJPanel inputInitiativePanel = new InputJPanel(InputJPanel.TYLKO_LICZBY, InputJPanel.INITIATIVE_WORD, InputJPanel.LIMIT_DWIE_LICZBY);
			//Dodajemy do zbioru pole tekstowe tego obszaru
			polaTekstowe.put(inputInitiativePanel.getStandJLabelText(), inputInitiativePanel.getStandJTextField());
				//Tworzymy przycisk losujący
				inputInitiativePanel.addRandomiserJButton();
		panelCentralny.add(inputInitiativePanel);
		
		
		//Dolna cześć z przyciskiem
		StandJButton btnPodtwiedzam = new StandJButton("Potwiedzam", StandJButton.PRZYCISK_ZWYKLY);
		btnPodtwiedzam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkTextFields()) {
					JOptionPane.showMessageDialog(frame, "Proszę wypełnić wszystkie pola!", "Uwaga!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
					int warriorCounts = Integer.parseInt(polaTekstowe.get(InputJPanel.COUNT_WORD).getText());
				
					for(int x = 0; x < warriorCounts & warriorCounts >= 1; x++) {
						//Dodajemy jednego wojownika do tablicy pobierając tekst oraz liczby ze zbioru HashMap polaTekstowe
						Object[] objects = {polaTekstowe.get(InputJPanel.WARRIOR_WORD).getText(), (Integer.parseInt(polaTekstowe.get(InputJPanel.AGILITY_WORD).getText())+Integer.parseInt(polaTekstowe.get(InputJPanel.INITIATIVE_WORD).getText())), "-"};
						try {
							btlFrameClass.addNewWarrior(objects);
						} catch (NullPointerException ex) {
							btlFrameClass = new BattleFrame();
							btlFrameClass.setVisible(true);
							btlFrameClass.addNewWarrior(objects);
						}
						
						//Dodajemy różną inicjatywe dla każdego z wojowników
						inputInitiativePanel.newRandomInitiative();
					}
					
					//Sparwdzamy czy nie ma tego wojownika w bazie danych
					WBSDatabase database = new WBSDatabase();
					
					//Dla lepszego rozumienia kodu wprowadzamy przemienne
					String warriorName = polaTekstowe.get(InputJPanel.WARRIOR_WORD).getText();
					int warriorAgilityInWindow = Integer.parseInt(polaTekstowe.get(InputJPanel.AGILITY_WORD).getText());
					
					
					if(database.isThereIsWarrior(polaTekstowe.get(InputJPanel.WARRIOR_WORD).getText())) {
						//Jeśli jednak taki już istnieje - to sprawdzamy czy użytkownik wprowadził inną liczbę szybkości niż jest zapisana w bazie danych
							//Przemienna do korzystania tylko jeśli wojownik już istnieje w bazie danych
							int warriorAgilityInDatabase = (int) database.getWarriorConfigurations(warriorName)[WBSDatabase.AGILITY_INDEX];
						//Jeśli liczba rzeczywista oraz liczba podana w bazie danych się różnią
						if(warriorAgilityInWindow != warriorAgilityInDatabase) {
							//Spytaj się czy użytkownik chciałby ją zaktualizować
							int option = JOptionPane.showConfirmDialog(frame, "Wykryto zmianę szybkości zapisanego w bazie danych wojownika."+'\n'+"Chcesz ja zaktualizować?", "Uwaga!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							if(option == 0) {
								database.addNewWarrior(new Object[] {
									warriorName.trim(), warriorAgilityInWindow	
								});
							}
						}
						
					} else {
						//Jeśli takiego nie istnieje - to się pytamy czy użytkownik chciałby go dodać
						int option = JOptionPane.showConfirmDialog(frame, "Chcesz dodać wojownika " + warriorName + " do bazy danych?", "Uwaga!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (option == 0) {
							//Jeśli tak - to dodajemy
							database.addNewWarrior(new Object[] {
								warriorName.trim(),warriorAgilityInWindow
							});
						}
						
					}
					
					//Oczyszczamy pola tekstowe
					clearTextFields();
					
					
					
				
				
			}
			
		});
		frame.getContentPane().add(btnPodtwiedzam, BorderLayout.SOUTH);
		
		
		
		
	}
	
	protected void clearTextFields() {
		polaTekstowe.get(InputJPanel.WARRIOR_WORD).setText("");
		polaTekstowe.get(InputJPanel.AGILITY_WORD).setText("");
		polaTekstowe.get(InputJPanel.INITIATIVE_WORD).setText("");
		polaTekstowe.get(InputJPanel.COUNT_WORD).setText("1");
		
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public void setVisible(boolean option) {
		if (option == true) {
			frame.setVisible(true);
		} else {
			frame.setVisible(false);
		}
	}
	
	public void setBattleClass(BattleFrame btlClass) {
		btlFrameClass = btlClass;
	}
	
	//Metoda do sprawdzania czy użytkownik napewno uzupęłnił wszystkie pola 
	//Po koleji sprawdza każde pole tekstowe czy nie jest puste (nawet jeśli zawiera miejsca puste)
	private boolean checkTextFields() {
		boolean allGood = true;
		if(polaTekstowe.get(InputJPanel.WARRIOR_WORD).getText().isBlank()) {
			allGood = false;
		} else if (polaTekstowe.get(InputJPanel.AGILITY_WORD).getText().isBlank()) {
			allGood = false;
		} else if (polaTekstowe.get(InputJPanel.INITIATIVE_WORD).getText().isBlank()) {
			allGood = false;
		} else if(polaTekstowe.get(InputJPanel.COUNT_WORD).getText().isBlank()) {
			allGood = false;
		}
		return allGood;
	}
}
