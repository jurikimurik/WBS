package frames;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import standarts.StandJButton;
import standarts.StandJLabel;
import standarts.StandJTable;
import standarts.StandJTable.StandJTableModel;

import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class BattleFrame {
	
	public static final int FRAME_STANDARD_WIDTH = 700;
	public static final int FRAME_STANDARD_HEIGHT = 500;
	private JFrame frame;
	
	private AddWarriorFrame addWarriorClass;
	private StandJTable table;
	
	private StandJLabel turaNapisDolny;
	
	private int roundNow = 0;
	private int roundNowVisible = 1;
	private int turnNow = 0;
	private int turnNowVisible = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Rectangle r = new Rectangle(100,100,FRAME_STANDARD_WIDTH,FRAME_STANDARD_HEIGHT);
					BattleFrame window = new BattleFrame(r);
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
	public BattleFrame(Rectangle r) {
		initialize(r);
	}

	public BattleFrame() {
		Rectangle r = new Rectangle(100,100,FRAME_STANDARD_WIDTH,FRAME_STANDARD_HEIGHT);
		initialize(r);
	}

	public BattleFrame(AddWarriorFrame addWarriorFrame) {
		this.setAddWarriorClass(addWarriorFrame);
		Rectangle r = new Rectangle(100,100,FRAME_STANDARD_WIDTH,FRAME_STANDARD_HEIGHT);
		initialize(r);
	}

	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(Rectangle r) {
		frame = new JFrame();
		frame.setBounds(r);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		//Tworzymy połączenie jeśli go niema
		if(addWarriorClass == null) {
			addWarriorClass = new AddWarriorFrame(this);
		}
		
		//Część gorna
		JPanel panelGorny = new JPanel();
		frame.getContentPane().add(panelGorny, BorderLayout.NORTH);
		
		StandJButton btnNextRoundOrBegin = new StandJButton("Rozpocznij", StandJButton.PRZYCISK_ZWYKLY);
		btnNextRoundOrBegin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if((roundNow == 0 & turnNow == 0) || (btnNextRoundOrBegin.getText().equals("Rozpocznij"))) {
					beginBattle(btnNextRoundOrBegin);
				} else {
					nextTurn();
				}
				
				
			}
			
		});
		panelGorny.add(btnNextRoundOrBegin);
		
		StandJButton btnBuffWarrior = new StandJButton("Zbustój", StandJButton.PRZYCISK_ZWYKLY);
		btnBuffWarrior.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boostWarrior();
				
				
				
			}
			
		});
		panelGorny.add(btnBuffWarrior);
		
		StandJButton btnDeleteWarrior = new StandJButton("Usuń", StandJButton.PRZYCISK_ZWYKLY);
		btnDeleteWarrior.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Object[] listaWyboru = {"Postać", "Busta"};
				
				int action = JOptionPane.showOptionDialog(frame, "Co dokładnie chcesz usunąć?", "Wybór usunięcia", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, listaWyboru, listaWyboru[0]);
				if(action == 0) {
					//Usuwanie z konwertacja na prawdziwą wybraną linijkę
					try {
						table.removeWarrior(table.getRowSorter().convertRowIndexToModel(table.getSelectedRow()));
					} catch (ArrayIndexOutOfBoundsException ex) {
						JOptionPane.showMessageDialog(frame, "Proszę wybrać wojownika najpierw!", "Uwaga!", JOptionPane.ERROR_MESSAGE);
					}
				} else if(action == 1) {
					//Otwórz ramkę z usuwaniem boostów
					table.removeBoostWindow(table.getSelectedRow());
					//Na razie nie pozwalaj na korzystanie z głownej ramki
					//table.pauseBattle();
					
				}
				
				
				
			}
			
		});
		panelGorny.add(btnDeleteWarrior);
		
		StandJButton btnAddWarrior = new StandJButton("Dodaj", StandJButton.PRZYCISK_ZWYKLY);
		btnAddWarrior.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addNewWarriorFrame();
				
			}
			
		});
		panelGorny.add(btnAddWarrior);
		
		
		
		//Część centralna
			//Tworzymy tablicę na podstawie modelu WBSTabelModel
		table = new StandJTable(frame);
			//Oraz dodajemy mu prewijanie (również pojawiają się nazwy kolumn)
		JScrollPane scrollPaneWithTabel = new JScrollPane(table);
		table.setFillsViewportHeight(true);
			//Zakaz na zmianę kolejności kolumn
		table.getTableHeader().setReorderingAllowed(false);
		
		//Tworzymy automatyczne sortowanie d
		table.setAutoCreateRowSorter(true);
		
		//Sortowanie
		((StandJTable) table).setDefaultSort();
		
		
		frame.getContentPane().add(scrollPaneWithTabel, BorderLayout.CENTER);
		
		//Część dolna
		JPanel panelDolny = new JPanel();
		frame.getContentPane().add(panelDolny, BorderLayout.SOUTH);
		turaNapisDolny = new StandJLabel("Tura: ", StandJLabel.ETYKIETA_GLOWNA, false);
		turaNapisDolny.setToolTipText("Tura: *wojownik*;(*Runda* - *tura*)");
		panelDolny.add(turaNapisDolny);
		
		//Tworzymy menu
		JMenuBar menuBar = new JMenuBar();
		
		//Tworzymy oraz dodajemy przycisk "Menu Głowne"
		JMenuItem mainMenuButton = new JMenuItem("Menu Głowne");
			//Dajemu mu działanie
			mainMenuButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					//Zamyka okno i otwiera menu głowne
					GoToMainMenu();
					
				}
				
			});
		menuBar.add(mainMenuButton);
		
		//Tworzymy oraz dodajemy przycisk "Zapisz"
		JMenuItem saveBattleButton = new JMenuItem("Zapisz");
			//Zapisujemy bitwę do pliku
			saveBattleButton.addActionListener( new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					//Ramka z wyborem pliku
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

						@Override
						//Akceptujemy tylko pliki z rozszerzeniem WBS_B
						public boolean accept(File f) {
							 if (f.getName().endsWith(".WBS_B")) {
					                return true;
					             }
							return false;
						}

						@Override
						//Dodatkowy opis
						public String getDescription() {
							// TODO Auto-generated method stub
							return "WBS Save File (.WBS_B)";
						}
						
					});
					//Ramkę czy chcemy to zapisać napewno
					int option = fileChooser.showSaveDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						try {
							File saveFile = new File(fileChooser.getSelectedFile()+".WBS_B");
							FileOutputStream fs = new FileOutputStream(saveFile);
							ObjectOutputStream os = new ObjectOutputStream(fs);
							
							//Najpierw zapisujemy zbiór niezbędnych danych do tabeli
							os.writeObject(table.getDataForSavedStandJTable());
							//Zapisujemy ilość rund i tur oraz wojownika (int,int,int,int,String)
							Object[] battleFrameData = new Object[] {roundNow, roundNowVisible, turnNow, turnNowVisible, turaNapisDolny.getText()};
							os.writeObject(battleFrameData);
							os.close();
						} catch (Exception ex) {
							System.out.println("Zapisywanie nie powiodło się.");
							ex.printStackTrace();
						}
					} else {
						System.out.println("Zapisywanie zostało anulowane.");
					}
				}
			});
		menuBar.add(saveBattleButton);
		
		//Tworzymy oraz dodajemy przycisk "Załaduj"
		JMenuItem loadBattleButton = new JMenuItem("Załaduj");
			//Odtwarzamy bitwę z pliku
			loadBattleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					loadBattleFromChoosedFile();
					
				}
				
			});
		menuBar.add(loadBattleButton);
		
		//Dodajemy menu do głownej ramki
		frame.setJMenuBar(menuBar);
		
		
		
	}
	
	protected void loadBattleFromChoosedFile() {
		//Ramka z wyborem pliku
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

			@Override
			//Akceptujemy tylko pliki z rozszerzeniem WBS_B
			public boolean accept(File f) {
				 if (f.getName().endsWith(".WBS_B")) {
		                return true;
		             }
				return false;
			}

			@Override
			//Dodatkowy opis
			public String getDescription() {
				// TODO Auto-generated method stub
				return "WBS Save File (.WBS_B)";
			}
			
		});
		int option = fileChooser.showOpenDialog(frame);
		if (option == JFileChooser.APPROVE_OPTION) {
			
			try {
				FileInputStream strumienPlk = new FileInputStream(fileChooser.getSelectedFile());
				ObjectInputStream os = new ObjectInputStream(strumienPlk);
				//odczytujemy zbiór danych tablicy z pliku
				Object[] newData = (Object[]) os.readObject();
					//Wczytujemy niezbędne dane
				table.loadStandJTable(newData[0], newData[1], newData[2]);
				//odczytujemy zbiór danych BattleFrame z pliku
				Object[] battleFrameData = (Object[]) os.readObject();
					//Wczytujemy do ramki dane
				loadBattleFrameData(battleFrameData);
				//Zamykamy strumień
				os.close();
				
			} catch (IOException | ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	
	//metoda do wczytywania danych z określonego pliku
	public void loadBattleFromFile(File loadedFile) {
		try {
			FileInputStream strumienPlk = new FileInputStream(loadedFile);
			ObjectInputStream os = new ObjectInputStream(strumienPlk);
			//odczytujemy zbiór danych tablicy z pliku
			Object[] newData = (Object[]) os.readObject();
				//Wczytujemy niezbędne dane
			table.loadStandJTable(newData[0], newData[1], newData[2]);
			//odczytujemy zbiór danych BattleFrame z pliku
			Object[] battleFrameData = (Object[]) os.readObject();
				//Wczytujemy do ramki dane
			loadBattleFrameData(battleFrameData);
			//Zamykamy strumień
			os.close();
			
		} catch (IOException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	//Metoda do wczytywania danych ze zbioru danych
	private void loadBattleFrameData(Object[] newData) {
		roundNow = (int) newData[0];
		roundNowVisible = (int) newData[1];
		turnNow = (int) newData[2];
		turnNowVisible = (int) newData[3];
		turaNapisDolny.setText((String) newData[4]);
	}
	
	protected void GoToMainMenu() {
		//Otwiera się menu główne
		MainMenu mainMenu = new MainMenu();
		mainMenu.setFrameVisible(true);
		
		//Zamykamy inne okna (na razie tylko z dodawaniem wojowników)
		CloseAnotherFrames();
		
		//Główna ramka znika
		frame.dispose();
		
	}

	private void CloseAnotherFrames() {
		//Zamykamy ramkę z dodawaniem wojowników
		addWarriorClass.getFrame().dispose();
		
	}

	protected void boostWarrior() {

		//PObieramy wojownika do zbustowania
		int wybranyWojownikPozycjaWidza = table.getSelectedRow();
		if(wybranyWojownikPozycjaWidza < 0) {
			JOptionPane.showMessageDialog(frame, "Proszę wybrać wojownika do zbustowania!", "Uwaga!", JOptionPane.ERROR_MESSAGE);
		} else {
			//Tworzymy kolejną ramkę z bustowaniem wojownika i przekazujemy do niej indeks wojownika oraz naszą klasę
			BoostWarriorFrame boostWarriorFrame = new BoostWarriorFrame(this, table, wybranyWojownikPozycjaWidza);
			
			//Pauzujemy rozgrywkę
			table.pauseBattle();
		}
		
	}

	//
	private void updateWarriorInRound(String wojownik) {
		turaNapisDolny.setText("Tura: " + wojownik +";("+ roundNowVisible + " - "+turnNowVisible+")");
	}
	
	private void nextTurnNumber() {
		turnNowVisible++;
	}
	
	private void nextRoundNumber() {
		roundNowVisible++;
	}
	
	private void nextTurn() {
		
		//Przesuwamy o jedną turę do przodu
		boolean isThereIsNewRound = table.nextWarrior();
		if(!isThereIsNewRound) {
			turnNow++;
			nextTurnNumber();
		} else {
			//Jeżeli zaczeła się kolejna runda - zrzuć licznik tur
			turnNow = 0;
			turnNowVisible = 1;
			nextRound();
		}
		
		//Odśwież napis dolny
		updateWarriorInRound((String) table.getActiveWarrior()[0]);
		
	}
	
	private void nextRound() {
		//Inkrementujemy liczniki rund
		roundNow++;
		
		//Dekrementujemy rundy w boostach
		table.dekrementSkillRounds();
		
		
		nextRoundNumber();
	}
	
	//Po kliknięciu rozpoczynamy bitwę
	protected void beginBattle(StandJButton btnNextRoundOrBegin) {
		if(btnNextRoundOrBegin.getText().equals("Rozpocznij")) {
			btnNextRoundOrBegin.setText("Kolejna tura");
		}
		nextTurn();
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
	
	public void setAddWarriorClass(AddWarriorFrame addWarCls) {
		addWarriorClass = addWarCls;
	}
	
	public void addNewWarriorFrame() {
		
		try {
			addWarriorClass.setVisible(true);
		} catch (NullPointerException ex) {
			addWarriorClass = new AddWarriorFrame();
			addWarriorClass.setVisible(true);
		}
	}
	
	public void addNewWarrior(Object[] warrior) {
		table.addWarrior(warrior);
	}
}
