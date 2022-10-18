package frames;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import standarts.StandJButton;
import standarts.StandJLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainMenu {

	private JFrame frame;
	
	private static final File NEW_BATTLE = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu window = new MainMenu();
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
	public MainMenu() {
		initialize();
	}
	
	public void setFrameVisible(boolean option) {
		frame.setVisible(option);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 280, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblText = new StandJLabel("WBS", StandJLabel.ETYKIETA_GLOWNA);
		lblText.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblText);
		
		JButton btnNewBattle = new StandJButton("Nowa bitwa", StandJButton.PRZYCISK_GLOWNY);
		btnNewBattle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startNewBattle(NEW_BATTLE);
			}
			
		});
		frame.getContentPane().add(btnNewBattle);
		
		JButton btnLoadBattle = new StandJButton("Zaladuj bitwe", StandJButton.PRZYCISK_GLOWNY);
			btnLoadBattle.addActionListener(new ActionListener() {

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
					int option = fileChooser.showOpenDialog(frame);
					if (option == JFileChooser.APPROVE_OPTION) {
						startNewBattle(fileChooser.getSelectedFile());
					}
					
				}
				
			});
		frame.getContentPane().add(btnLoadBattle);
		
		//Przycisk dla odpalenia edytora bazy danych wojowników
		JButton btnEditDatabase = new StandJButton("Baza danych", StandJButton.PRZYCISK_GLOWNY);
		btnEditDatabase.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				openDatabaseEditor();
				
			}
			
		});
		frame.getContentPane().add(btnEditDatabase);
		
		//Działanie dla przycisku wyjścia
		JButton btnExit = new StandJButton("Wyjść", StandJButton.PRZYCISK_GLOWNY);
		btnExit.addActionListener(new ActionListener () {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.exit(0);
			}
			
		});
		frame.getContentPane().add(btnExit);
	}


	protected void openDatabaseEditor() {
		//Tworzymy ramkę z edycją bazy danych
		WBSDatabaseEditor wbsDatabaseEditor = new WBSDatabaseEditor();
		
		//Zamykamy okno MainMenu
		frame.dispose();
		
	}

	protected void startNewBattle(File selectedFile) {
		//Tworzymy dwa nowe okna, pierwsze to jest podstawowe okno bitwy
		
		//Rectangle z pozycją okna MainMenu i rozmiarami BattleFrame
		//Rectangle recForBtlFrame = new Rectangle(frame.getX(), frame.getY(), BattleFrame.FRAME_STANDARD_WIDTH, BattleFrame.FRAME_STANDARD_HEIGHT);
		BattleFrame btlFrameClass = new BattleFrame();
		btlFrameClass.setVisible(true);
		
		//Tworzymy drugie okno wprowadzenia postaci do bitwy
		
		//Na podstawie pierwszego okna tworzymy drugie w określonym miejscu
		JFrame btlFrame = btlFrameClass.getFrame();
		//Rectangle recForInputFrame = new Rectangle(btlFrame.getX()-(BattleFrame.FRAME_STANDARD_HEIGHT/2), btlFrame.getY()-BattleFrame.FRAME_STANDARD_HEIGHT, AddWarriorFrame.FRAME_STANDARD_WIDTH, AddWarriorFrame.FRAME_STANDARD_HEIGHT);
		AddWarriorFrame addWarriorClass = new AddWarriorFrame();
		addWarriorClass.setVisible(true);
		
		//Tworzymy związek pomiędzy tymi dwoma klasami
		btlFrameClass.setAddWarriorClass(addWarriorClass);
		addWarriorClass.setBattleClass(btlFrameClass);
		
		//Jeśli jest jakiś plik do wgrania - wgraj go
		if(selectedFile != NEW_BATTLE) {
			btlFrameClass.loadBattleFromFile(selectedFile);
		}
		
		//Zamykamy okno MainMenu
		frame.dispose();
		
	}
}
