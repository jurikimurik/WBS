package frames;


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import standarts.StandJButton;
import standarts.StandJLabel;
import standarts.StandJTable;

import java.awt.BorderLayout;

public class RemoveWarriorBoostsFrame {

	private JFrame frame;
	
	private ArrayList<JCheckBox> boostCheckBoxes = new ArrayList<JCheckBox>();
	private ArrayList<String> strBoosts = new ArrayList<String>();
	
	//Połączenie z podstawową tabelą
	private int warriorIndexModel;
	private StandJTable table;
	private JFrame mainFrame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RemoveWarriorBoostsFrame window = new RemoveWarriorBoostsFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @param standJTable 
	 * @param modelWarriorSelectedindex 
	 * @param warriorBoosts 
	 */
	public RemoveWarriorBoostsFrame() {
		initialize();
	}
	
	
	
	public RemoveWarriorBoostsFrame(Object[] boosts, int warriorModelIndex, StandJTable standJTable, JFrame mainFrm) {
		setStrBoosts(boosts);
		setWarriorModelIndex(warriorModelIndex);
		setStandJTable(standJTable);
		setMainFrame(mainFrm);
		initialize();
		
	}
	
	private void setMainFrame(JFrame mainFrm) {
		mainFrame = mainFrm;
		
	}

	private void setStandJTable(StandJTable standJTable) {
		table = standJTable;
		
	}

	private void setStrBoosts(Object[] boosts) {
		for (Object obj: boosts) {
			strBoosts.add((String)obj);
		}
	}
	
	private void setWarriorModelIndex(int warriorIndM) {
		warriorIndexModel = warriorIndM;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
		JPanel panelGorny = new JPanel();
		StandJLabel napisGorny = new StandJLabel("Wybierz, ktore boost'y należy usunąć", StandJLabel.ETYKIETA_ZWYKLA, StandJLabel.NIE_NADAWAJ_ROZMIARU);
		panelGorny.add(napisGorny);
		frame.getContentPane().add(panelGorny, BorderLayout.NORTH);
		
		
		JPanel panelCentralny = new JPanel();
		for(String str: strBoosts) {
			JCheckBox checkBox = new JCheckBox(str);
			panelCentralny.add(checkBox);
			boostCheckBoxes.add(checkBox);
		}
		frame.getContentPane().add(panelCentralny, BorderLayout.CENTER);
		
		StandJButton btnPodtwiedzam = new StandJButton("Podtwierdzam", StandJButton.PRZYCISK_ZWYKLY);
		btnPodtwiedzam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Dla każdego checkbox'a
				for(JCheckBox checkBox: boostCheckBoxes) {
					//Jeśli jest wybrany
					if(checkBox.isSelected()) {
						//Usuń ten wybrany boost i odśwież tabelę
						table.removeSpecifiedWarriorBoost(warriorIndexModel, checkBox.getText());
					}
				}
				table.resumeBattle();
				frame.dispose();
				
			}
			
		});
		frame.getContentPane().add(btnPodtwiedzam, BorderLayout.SOUTH);
		
		
		//Dodajemy żeby wznowić grę jeśli użytkownik zamknął ramkę
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				table.resumeBattle();
				frame.dispose();
			}
		});
	}
	
	
	

}
