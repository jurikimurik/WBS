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
import standarts.StandJTable;
import standarts.StandJTextField;

import javax.swing.SwingConstants;

public class BoostWarriorFrame {

	private JFrame frame;
	
	//Standard rozmiarów
	public static final int FRAME_STANDARD_WIDTH = 600;
	public static final int FRAME_STANDARD_HEIGHT = 200;
	
	
	
	
	//Zbiór z polami tekstowymi, ktore da się odzyskać za pomocą klucza
	private HashMap<String, StandJTextField> polaTekstowe = new HashMap<String, StandJTextField>();
	
	//Niezbędne powiązane rzeczy pomiędzy klasami
	private BattleFrame btlFrameClass = null;
	private StandJTable connectedJTable = null;
	private int choosedWarrior= -1;
	
	private JPanel panelCentralny;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Rectangle r = new Rectangle(100,100,FRAME_STANDARD_WIDTH,FRAME_STANDARD_HEIGHT);
					BoostWarriorFrame window = new BoostWarriorFrame(r);
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
	public BoostWarriorFrame(Rectangle r) {
		initialize(r);
	}
	
	public BoostWarriorFrame(BattleFrame battleFrame, StandJTable table, int wybranyWojownikPozycjaWidza) {
		createConnection(battleFrame, table);
		setChoosedWarrior(wybranyWojownikPozycjaWidza);
		
		Rectangle r = new Rectangle(100,100,FRAME_STANDARD_WIDTH,FRAME_STANDARD_HEIGHT);
		initialize(r);
	}

	private void setChoosedWarrior(int wybranyWojownikPozycjaWidza) {
		this.choosedWarrior = wybranyWojownikPozycjaWidza;
		
	}

	private void createConnection(BattleFrame battleFrame, StandJTable table) {
		this.btlFrameClass = battleFrame;
		this.connectedJTable = table;
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(Rectangle r) {
		frame = new JFrame();
		frame.setBounds(r);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setVisible(true);
		
		//Górna część
		JPanel panelGorny = new JPanel();
			
			//Etykieta gorna
			String wojownikNazwa = (String) connectedJTable.getWarrior(choosedWarrior)[StandJTable.WARRIOR_NAME_INDEX];
			StandJLabel lblGornyNapis = new StandJLabel("Bustujesz: " + wojownikNazwa, StandJLabel.ETYKIETA_GLOWNA, StandJLabel.NIE_NADAWAJ_ROZMIARU);
		
			panelGorny.add(lblGornyNapis);
		frame.getContentPane().add(BorderLayout.NORTH, panelGorny);
		
		//Centralna część
		panelCentralny = new JPanel(); frame.getContentPane().add(BorderLayout.CENTER, panelCentralny);
		
		//Tworzymy obszar dla wprowazania umiejętności
				InputJPanel inputSkillPanel = new InputJPanel(InputJPanel.TYLKO_TEKST, InputJPanel.SKILL_WORD, InputJPanel.BEZ_LIMITU);
					//Dodajemy do zbioru pole tekstowe tego obszaru
					polaTekstowe.put(inputSkillPanel.getStandJLabelText(), inputSkillPanel.getStandJTextField());
				panelCentralny.add(inputSkillPanel);
		//Tworzymy obszar dla wprowazania umiejętności
				InputJPanel inputSkillBoostPanel = new InputJPanel(InputJPanel.TYLKO_LICZBY, InputJPanel.SKILL_BOOST_WORD, InputJPanel.LIMIT_TRZY_LICZBY);
					//Dodajemy do zbioru pole tekstowe tego obszaru
					polaTekstowe.put(inputSkillBoostPanel.getStandJLabelText(), inputSkillBoostPanel.getStandJTextField());
				panelCentralny.add(inputSkillBoostPanel);		
		
		//Tworzymy obszar dla wprowazania ilości rund
				InputJPanel inputRoundsPanel = new InputJPanel(InputJPanel.TYLKO_LICZBY, InputJPanel.NUMBER_OF_ROUNDS_WORD, InputJPanel.LIMIT_TRZY_LICZBY);
					//Dodajemy do zbioru pole tekstowe tego obszaru
					polaTekstowe.put(inputRoundsPanel.getStandJLabelText(), inputRoundsPanel.getStandJTextField());
				panelCentralny.add(inputRoundsPanel);
		
		
		//Dolna część
			//Przycisk z potwierdzeniem wprowadzenia
			StandJButton btnPodtwiedzam = new StandJButton("Bustuję", StandJButton.PRZYCISK_ZWYKLY);
			btnPodtwiedzam.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(!checkTextFields()) {
						JOptionPane.showMessageDialog(frame, "Proszę wypełnić wszystkie pola!", "Uwaga!", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					//Pobieramy informacje
					String skillBoostText = polaTekstowe.get(InputJPanel.SKILL_WORD).getText();
					int boostSkill = Integer.parseInt(polaTekstowe.get(InputJPanel.SKILL_BOOST_WORD).getText());
					int boostRounds = Integer.parseInt(polaTekstowe.get(InputJPanel.NUMBER_OF_ROUNDS_WORD).getText());
					
					//Bustujemy wojownika
					connectedJTable.addBoostWarrior(choosedWarrior, skillBoostText, boostSkill, boostRounds);
					
					//Zamykamy okno i przywracamy rozgrywkę
					frame.dispose();
					connectedJTable.resumeBattle();
				}
				
			});
			frame.getContentPane().add(BorderLayout.SOUTH, btnPodtwiedzam);
		
			//Dodajemy żeby wznowić grę jeśli użytkownik zamknął ramkę
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					connectedJTable.resumeBattle();
					frame.dispose();
				}
			});
		}
	
	//Metoda do sprawdzania czy użytkownik napewno uzupęłnił wszystkie pola 
		//Po koleji sprawdza każde pole tekstowe czy nie jest puste (nawet jeśli zawiera miejsca puste)
		private boolean checkTextFields() {
			boolean allGood = true;
			if(polaTekstowe.get(InputJPanel.SKILL_WORD).getText().isBlank()) {
				allGood = false;
			} else if (polaTekstowe.get(InputJPanel.SKILL_BOOST_WORD).getText().isBlank()) {
				allGood = false;
			} else if (polaTekstowe.get(InputJPanel.NUMBER_OF_ROUNDS_WORD).getText().isBlank()) {
				allGood = false;
			}
			return allGood;
		}
	}
