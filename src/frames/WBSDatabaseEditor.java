package frames;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import data.WBSDatabase;
import data.WBSDatabase.WBSDatabaseListener;
import standarts.StandJButton;
import standarts.StandJLabel;

import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class WBSDatabaseEditor {

	private JFrame frame;

	//Model listu oraz lista
	private DefaultListModel<String> listModel = new DefaultListModel();
	private JList<String> lista;
	
	//Połączenie za bazą danych
	private WBSDatabase database  = new WBSDatabase();
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WBSDatabaseEditor window = new WBSDatabaseEditor();
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
	public WBSDatabaseEditor() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		//Górna cześć
		JPanel panelGorny = new JPanel();
		frame.getContentPane().add(panelGorny, BorderLayout.NORTH);
		
		//Górny napis
		StandJLabel lblNewLabel = new StandJLabel("Edytor bazy danych", StandJLabel.ETYKIETA_GLOWNA, StandJLabel.NIE_NADAWAJ_ROZMIARU);
		panelGorny.add(lblNewLabel);
		
		//Centralna część
		
		//Ustawiamy początkowy model
		listModel.addAll(database.warriorArrayWithConfigurations());
		
		//Ustaiwamy nasłuchiwacz
		WBSDatabaseListener listener = new WBSDatabaseListener() {

			@Override
			public void databaseUpdated() {
				//Odśieżamy listę bazy danych
				updateListData();
			}
			
		};
		WBSDatabase.setWBSDatabaseListener(listener);
		
		//Lista z wojownikami
		lista = new JList(listModel);
		//Zezwalamy tylko na wybor jednego z listy
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//Wertykalny widok z wypełnieniem nazw w środku
		lista.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		
		//Tworzymy przewijanie dla listy
		JScrollPane listPrzewijalny = new JScrollPane(lista);
		frame.getContentPane().add(listPrzewijalny);
		
		//Dolna część
		JPanel paneDolny = new JPanel();
		frame.getContentPane().add(paneDolny, BorderLayout.SOUTH);
		
		StandJButton btnAddWarrior = new StandJButton("Dodaj", StandJButton.PRZYCISK_ZWYKLY);
		btnAddWarrior.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//tworzymy ramkę z dodawaniem wojownika
				WBSDatabaseEditor_AddOrEdit addFrame = new WBSDatabaseEditor_AddOrEdit(WBSDatabaseEditor_AddOrEdit.ADD_WARRIOR);
				
			}
			
		});
		paneDolny.add(btnAddWarrior);
		
		StandJButton btnDeleteWarrior = new StandJButton("Usuń", StandJButton.PRZYCISK_ZWYKLY);
		btnDeleteWarrior.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Pobieramy nazwę wojownika
				String warriorName = (String) database.getWarriorConfigurations(lista.getSelectedIndex())[WBSDatabase.NAME_INDEX];
				
				//Spytaj się czy na pewno użytkownik jest pewien że chce usunąć
					//Tłumaczenie dla przycisków "yes"\"no"
					Object[] options = {"Tak", "Nie"};
					
				int option = JOptionPane.showOptionDialog(frame,"Jesteś pewien że chcesz usunąć"+'\n'+"wojownika " + warriorName + " z bazy danych?","Uwaga!",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE, null,options,  options[0]);	
				if(option == JOptionPane.YES_OPTION) {
					//Usuń wojownika
					database.removeWarrior(database.getWarriorConfigurations(warriorName));
				}
			}
			
		});
		paneDolny.add(btnDeleteWarrior);
		
		StandJButton btnEditWarrior = new StandJButton("Edytuj", StandJButton.PRZYCISK_ZWYKLY);
		btnEditWarrior.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Pobieramy nazwę wojownika
				String warriorName = (String) database.getWarriorConfigurations(lista.getSelectedIndex())[WBSDatabase.NAME_INDEX];
				
				//tworzymy ramkę z edycją wojownika i przekazujemy mu nazwę wojownika
				WBSDatabaseEditor_AddOrEdit addFrame = new WBSDatabaseEditor_AddOrEdit(WBSDatabaseEditor_AddOrEdit.EDIT_WARRIOR);
				addFrame.setWarriorName(warriorName);
			}
			
		});
		paneDolny.add(btnEditWarrior);
		
		
		//Górne menu
		JMenuBar menuGorne = new JMenuBar();
		frame.setJMenuBar(menuGorne);
		
		JMenuItem mbMainMenu = new JMenuItem("Menu Głowne");
		mbMainMenu.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Otwieramy ramkę z menu głownym
				MainMenu.main(null);
				
				//Zamykamy naszą ramkę
				frame.dispose();
				
			}
			
		});
		
		menuGorne.add(mbMainMenu);
	}
	
	

	
	
	private void updateListData() {
		//Tworzymy model z nową informacją
		listModel = new DefaultListModel<String>();
		listModel.addAll(database.warriorArrayWithConfigurations());
		
		//Ustawiamy model na nowo
		lista.setModel(listModel);
	}
	

}
