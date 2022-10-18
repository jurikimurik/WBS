package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class WBSDatabase {
	private static ArrayList<String> databaseNames;
	private static ArrayList<Integer> databaseAgility;
	private File databaseFile = new File("database.wbs");
	
	//Stałe przemienne
	public static final int NAME_INDEX = 0;
	public static final int AGILITY_INDEX = 1;
	
	//Nasłuchiwacz
	private static WBSDatabaseListener listener;
	
	public WBSDatabase() {
		try {
			readDatabase();
		} catch (FileNotFoundException ex) {
			createNewDatabase();
		}
	}
	
	//Metoda dodawania nowego wojownika do bazy danych
	//Potrzbuje zbiór w postaci [String][Integer]
	public void addNewWarrior(Object[] warrior) {
		//Jeśli wojownik którego chcemy dodać już istnieje - odświeżamy jego statystyki (nie zezwalamy na powtorzenia)
		if(databaseNames.contains((String)warrior[0])) {
			updateWarrior(warrior);
			return;
		}
		
		//Jeśli przesył danych będzie zły
		try {
			//Dodajemy do listy imion
			databaseNames.add((String) warrior[0]);
			//DOdajemy do listy szybkośći
			databaseAgility.add((Integer) warrior[1]);
		} catch (ClassCastException ex) {
			ex.printStackTrace();
			JOptionPane.showInternalMessageDialog(null, "Zły przesył danych!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
		}
		
		//Odświeżamy bazę danych
		updateDatabase();
		
	}
	
	//Metoda usuwająca wojownika z bazy danych
	public void removeWarrior(Object[] warrior) {
		//Pobiera indeks wojownika do usunięcia
		int warriorIndex = databaseNames.indexOf((String)warrior[0]);
		
		//Usuwa go z listy imion
		databaseNames.remove(warriorIndex);
		//Usuwa go z listy szybkośći
		databaseAgility.remove(warriorIndex);
		
		//Odświeżamy bazę danych
		updateDatabase();
	}
	
	//metoda zmieniająca konfigurację wojownika do porządanej postaci
	public void updateWarrior(Object[] warrior) {
		int warriorIndex = databaseNames.indexOf((String)warrior[0]);
		
		//Odświeżamy zręczność wojownika
		databaseAgility.set(warriorIndex, (Integer) warrior[1]);
		
		//Odświeżamy bazę danych
		updateDatabase();
	}
	
	//Metoda do sprawdzania czy podany wojownik istnieje w bazie danych
	public boolean isThereIsWarrior(String warriorName) {
		//Indeks wojownika (-1 jeśli nie ma)
		int warriorIndex = databaseNames.indexOf(warriorName);
		
		
		if(warriorIndex > -1) {
			return true;
		} else {
			return false;
		}
	}
	
	//metoda do otrzymania konfiguracji wojownika
	public Object[] getWarriorConfigurations(Object[] warrior) {
		int warriorIndex = databaseNames.indexOf((String)warrior[0]);
		
		Object[] conf = {databaseNames.get(warriorIndex) ,databaseAgility.get(warriorIndex)};
		return conf;
	}
	//przeciążona metoda do otrzymania konfiguracji wojownika
		public Object[] getWarriorConfigurations(String warrior) {
			int warriorIndex = databaseNames.indexOf(warrior);
			
			Object[] conf = {databaseNames.get(warriorIndex) ,databaseAgility.get(warriorIndex)};
			return conf;
		}
		//przeciążona metoda do otrzymania konfiguracji wojownika za pomocą jego miejsca
				public Object[] getWarriorConfigurations(int warriorIndex) {
					
					Object[] conf = {databaseNames.get(warriorIndex), databaseAgility.get(warriorIndex)};
					return conf;
		}
	
	//metoda do otrzymania z bazy danych wszystkich nazw wojownikow
	public ArrayList<String> getAllWarriorsNames() {
		return databaseNames;
	}
	
	//Dla wizualnego widoku wojoników razem z konfiguracją
		public ArrayList<String> warriorArrayWithConfigurations() {
			//Głowny zbiór na ktorym operujemy
			ArrayList<String> mainArray = new ArrayList<String>();
			//Zbiór z imionami wojowników
			ArrayList<String> warriorsNames = this.getAllWarriorsNames();
			
			//Dla każdego z imion tworzymy linijkę kodu z konfiguracją i imieniem
			for(String name: warriorsNames) {
				Object[] warriorConf = this.getWarriorConfigurations(name);
				
				//Pkazujemy tylko imie oraz szybkość
				mainArray.add(warriorConf[WBSDatabase.NAME_INDEX]+":"+warriorConf[WBSDatabase.AGILITY_INDEX]);
			}
			
			
			return mainArray;
		}
	
	
	//Metoda tworzenia nowej bazy danych
	private void createNewDatabase() {
		try {
			//Tworzymy plik za pomocą FileWriter
			FileWriter databaseFileWriter = new FileWriter(databaseFile);
			//Zapisujemy plik
			databaseFileWriter.close();
			
			
		} catch (IOException e) {
			//Tworzymy ranmkę z ostrzeżeniem że nie można utworzyć pliku database.wbs z powodów systemowych
			JOptionPane.showInternalMessageDialog(null, "Nie można utworzyć pliku database.wbs!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	//Metoda otczytu wojownikow z bazy danych
	private void readDatabase() throws FileNotFoundException {
		//Tworzymy połączenie z plikiem database.wbs dla odczytu
		FileReader databaseFileReader = new FileReader(databaseFile);
		//Strumień dla lepszej pracy z plikiem
		BufferedReader databaseFileBufferedReader = new BufferedReader(databaseFileReader);
		
		//Pojedynczy wiersz odczytu
		String line = null;
		//Oraz puste zbiory danych
		databaseNames = new ArrayList<String>();
		databaseAgility = new ArrayList<Integer>();
		
		try {
			//Dopóki istnieją jakieś wiersze
			while ((line = databaseFileBufferedReader.readLine()) != null) {
				//Dodaj imie które zaczyna się od początku wiersza i do symbolu ":"
				databaseNames.add(line.substring(0, line.indexOf(":")));
				//Dodaj zręczność, ltóra zaczyna się od "
				databaseAgility.add(Integer.parseInt(line.substring(line.indexOf(":")+1, line.indexOf(";"))));
			}
		} catch (IOException e) {
			JOptionPane.showInternalMessageDialog(null, "Nie można odczytać zawartości pliku database.wbs!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
		
	}
	
	//Metoda aktualizująca bazę danych
	private void updateDatabase() {
		try {
			//Tworzymy połaczenie z plikiem database.wbs dla zapisu danych
			FileWriter databaseFileWriter = new FileWriter(databaseFile);
			
			//Zapisujemy dane w postaci: "Imię:Szybkość;" dla każdego wiersza
			for(int x = 0; x < databaseNames.size(); x++) {
				databaseFileWriter.write(databaseNames.get(x)+":"+String.valueOf(databaseAgility.get(x)+";") + '\n');
			}
			
			//Zamykamy strumień dla zapisu
			databaseFileWriter.close();
			
			//Powiadamiamy że baza danych ulegla zmianie
			try {
				listener.databaseUpdated();
			} catch (NullPointerException ex) {
				System.out.println("No listener.");
			}
			
			
		} catch (IOException e) {
			JOptionPane.showInternalMessageDialog(null, "Nie można zapisać zawartość pliku database.wbs!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public static void  setWBSDatabaseListener(WBSDatabaseListener list) {
		listener = list;
	}
	
	public interface WBSDatabaseListener {
		public void databaseUpdated();
	}
}
