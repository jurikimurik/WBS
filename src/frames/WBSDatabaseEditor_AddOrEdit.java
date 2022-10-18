package frames;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import data.WBSDatabase;
import standarts.InputJPanel;
import standarts.StandJButton;
import standarts.StandJLabel;
import standarts.StandJTextField;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;

public class WBSDatabaseEditor_AddOrEdit {

	private JFrame frame;
	
	//Stałe przemienne
	public static final String ADD_WARRIOR = "Dodaj wojownika";
	public static final String EDIT_WARRIOR = "Edytuj wojownika";
	
	//Tworzymy połączenie z bazą danych
	WBSDatabase database = new WBSDatabase();
	
	//Zbiór z polami tekstowymi, ktore da się odzyskać za pomocą klucza
	private HashMap<String, StandJTextField> polaTekstowe = new HashMap<String, StandJTextField>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WBSDatabaseEditor_AddOrEdit window = new WBSDatabaseEditor_AddOrEdit("Dodaj wojownika");
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
	public WBSDatabaseEditor_AddOrEdit(String str) {
		initialize(str);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String labelText) {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 200);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
		//Część górna
		JPanel panelGorny = new JPanel();
		frame.getContentPane().add(panelGorny, BorderLayout.NORTH);
		StandJLabel lblUpperLabel = new StandJLabel(labelText, StandJLabel.ETYKIETA_GLOWNA, false);
		panelGorny.add(lblUpperLabel);
		
		
		//Część centralna
		JPanel panelCentralny = new JPanel();
		frame.getContentPane().add(panelCentralny, BorderLayout.CENTER);
		
		 //Tworzymy obszar dla wprowazania wojownika
		InputJPanel inputCharacterPanel = new InputJPanel(InputJPanel.TYLKO_TEKST, InputJPanel.WARRIOR_WORD, InputJPanel.BEZ_LIMITU);
			//Dodajemy do zbioru pole tekstowe tego obszaru
			polaTekstowe.put(inputCharacterPanel.getStandJLabelText(), inputCharacterPanel.getStandJTextField());
		panelCentralny.add(inputCharacterPanel);
		
		//Tworzymy obszar dla wprowadzania zręczności
		InputJPanel inputAgilityPanel = new InputJPanel(InputJPanel.TYLKO_LICZBY, InputJPanel.AGILITY_WORD, InputJPanel.LIMIT_TRZY_LICZBY);
			//Dodajemy do zbioru pole tekstowe tego obszaru
			polaTekstowe.put(inputAgilityPanel.getStandJLabelText(), inputAgilityPanel.getStandJTextField());
		panelCentralny.add(inputAgilityPanel);
		
		//Dolna część
		StandJButton btnConfirm = new StandJButton("Podtwierdzam", StandJButton.PRZYCISK_ZWYKLY);
		
		btnConfirm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkTextFields()) {
					JOptionPane.showMessageDialog(frame, "Proszę wypełnić wszystkie pola!", "Uwaga!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				//Pobieramy imię oraz szybkość
				String warriorName = polaTekstowe.get(InputJPanel.WARRIOR_WORD).getText();
				Integer warriorAgility = Integer.parseInt(polaTekstowe.get(InputJPanel.AGILITY_WORD).getText());
				
				//Tworzymy zbiór, który należy wysłać
				Object[] toSend = {warriorName, warriorAgility};
				
				//Dodajemy wojownika
				database.addNewWarrior(toSend);
				
				//Oczyszczamy pola tekstowe i dajemy fokus
				resetTextFields();
			}
			
		});
		
		frame.getContentPane().add(btnConfirm, BorderLayout.SOUTH);
	}
	
	//Metoda dla ustawienia nazwy wojownika i dająca fokus na zręczność
	public void setWarriorName(String warrior) {
		polaTekstowe.get(InputJPanel.WARRIOR_WORD).setText(warrior);
		//Dajemy fokus
		if(polaTekstowe.get(InputJPanel.AGILITY_WORD).requestFocusInWindow()) {
			polaTekstowe.get(InputJPanel.AGILITY_WORD).requestFocus();
		}
	}
	
	
	//Metoda do sprawdzania czy użytkownik napewno uzupęłnił wszystkie pola 
		//Po koleji sprawdza każde pole tekstowe czy nie jest puste (nawet jeśli zawiera miejsca puste)
		private boolean checkTextFields() {
			boolean allGood = true;
			if(polaTekstowe.get(InputJPanel.WARRIOR_WORD).getText().isBlank()) {
				allGood = false;
			} else if (polaTekstowe.get(InputJPanel.AGILITY_WORD).getText().isBlank()) {
				allGood = false;
			}
			return allGood;
		}
		
		protected void resetTextFields() {
			//Dajemy fokus
			if(!polaTekstowe.get(InputJPanel.WARRIOR_WORD).requestFocusInWindow()) {
				polaTekstowe.get(InputJPanel.WARRIOR_WORD).requestFocus();
			}
			
			//Oczyszczamy pola
			polaTekstowe.get(InputJPanel.WARRIOR_WORD).setText("");
			polaTekstowe.get(InputJPanel.AGILITY_WORD).setText("");
			
		}
}
