package standarts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import frames.RemoveWarriorBoostsFrame;

public class StandJTable extends JTable {
	
	
	private static int appSelectedRow = -1;
	private static boolean isAppSelectedRowTemporaryDeleted = false;
	
	private StandJTableModel standJTableModel = new StandJTableModel();
	
	public static final int WARRIOR_NAME_INDEX = 0;
	public static final int WARRIOR_AGILITY_AND_INITIATIVE_INDEX = 1;
	public static final int WARRIOR_BOOST_INDEX = 2;
	
	//Zmienne dla początkowego wojownika w tabeli
	public static final String WARRIOR_TO_DELETE = "WAR_TO_DEL";
	private static int WARRIOR_TO_DELETE_ROW_INDEX = 0;
	
	private JFrame mainFrame;
	
	public StandJTable(JFrame primaryFrame) {
		//Powiązujemy ramkę mainFrame z głowną ramką
		setMainFrame(primaryFrame);
		
		
		//Model + czcionka
		this.setModel(standJTableModel);
		this.setFont(new StandFont());
		
		//Render (dla zmiany koloru czcionki)
		CustomTableRenderer cTR = new CustomTableRenderer();
		this.setDefaultRenderer(String.class, cTR);
		this.setDefaultRenderer(Integer.class, cTR);
		
		
		
	}
	
	private void setMainFrame(JFrame primaryFrame) {
		mainFrame = primaryFrame;
	}
	
	public void resetBooleanIsAppSelectedRowTemporaryDeleted() {
		isAppSelectedRowTemporaryDeleted = false;
	}
	
	
	private void resetAppSelectedRow() {
		appSelectedRow = -1;
	}
	
	public void pauseBattle() {
		mainFrame.setEnabled(false);
	}
	
	public void resumeBattle() {
		mainFrame.setEnabled(true);
	}
	 
	public boolean nextWarrior() {
		//Przywracamy możliwość pokazu czerwonej linijki
		resetBooleanIsAppSelectedRowTemporaryDeleted();
		
		//Sprawdzamy czy istnieje dalej jakiś wojownik. Jeśli nie - zwracamy true dlatego, że zaczeła się nowa runda.
		if(appSelectedRow+1 < this.getRowCount()) {
			appSelectedRow++;
			this.repaint();
			return false;
		} else {
			appSelectedRow = 0;
			this.repaint();
			return true;
		}
	}
	
	public void addBoostWarrior(int warriorNumber, String boostSkillText, int boostSkill, int boostRounds) {
		//pobieramy bazę danych
		Object[][] tableData = standJTableModel.getData();
				
		//konwertujemy liczbę z typu view na model
		int realSelectedWarrior = this.getRowSorter().convertRowIndexToModel(warriorNumber);
		
		//Usuwamy znaczek "-"
		Object[] warrior = tableData[realSelectedWarrior];
		if(warrior[WARRIOR_BOOST_INDEX].equals("-")) {
			warrior[WARRIOR_BOOST_INDEX] = "";
		}
		
		//Tworzymy widocznego boosta
		warrior[WARRIOR_BOOST_INDEX] = warrior[WARRIOR_BOOST_INDEX] + " " + boostSkillText + "("+ boostSkill +")" + " - (R:" + boostRounds+ ");";
		
		//Na wszelki wypadek powiązujemy kolejny raz z wojownikiem wybranym
		tableData[realSelectedWarrior] = warrior;
		
		//Odśieżamy tabelę
		updateDataInTable(tableData);
	}
	
	//Zmiejsza liczbę rund każdego boost'a o jeden
	public void dekrementSkillRounds() {
		//pobieramy bazę danych
		Object[][] tableData = standJTableModel.getData();
		
		//dla każdego z tablicy
		for(int y = 0; y < tableData.length;y++) {
			Object[] warrior = tableData[y];
			//Pobieramy tekst boostu
			String warriorBoostText = (String) warrior[WARRIOR_BOOST_INDEX];
			
			//Za każdym razem szuka kolejnych R:, żeby zmniejszyć ilość rund
			for (int x = warriorBoostText.indexOf("R:"); warriorBoostText.indexOf("R:", x) != -1; x = warriorBoostText.indexOf("R:", x)) {
				//Jeśli nie ma więcej żadnego boosta - wychodzi z pętli
				if(x == -1) {
					break;
				}
				
				//Pobieramy kopię tekstu z boostami
				String modificatedWBT = warriorBoostText.substring(0);
				
				//Indeksy. Od - czyli od pierwszej liczby rund, do - ostatnia liczba rund lub taka sama jeśli liczba nie jest większa od 9
				int fromIndex = x+2;
				int toIndex = modificatedWBT.indexOf(")", x);
				
				//Pobieramy liczbę rund i zamieniamy ją na Integer żeby zmniejszyć o jeden
				int rounds = Integer.parseInt(modificatedWBT.substring(fromIndex, toIndex));
				rounds--;
				
				//Zamieniamy liczbę starych rund na liczbę nowych (zmniejszonych o jeden)
				StringBuilder SB_ModificatedWBT = new StringBuilder(modificatedWBT);
				SB_ModificatedWBT.replace(fromIndex, toIndex, String.valueOf(rounds));
				
				//Przekształtujemy StringBuilder na String i powiązujemy ten ciąg znaków z tekstem z bustami
				warriorBoostText = SB_ModificatedWBT.toString();
				
				//UWAGA! W CELACH UNIKNIECIA MARTWEJ PETWLI PRZESUWAMY INDEKS X DALEJ
				x += String.valueOf(rounds).length();
			}
			
			//Na wszełki wypadek z powrotem powiązujemy ciągni znaków
			warrior[WARRIOR_BOOST_INDEX] = warriorBoostText;
			tableData[y] = warrior;
		}
		
		//Odświeżamy tabelę
		updateDataInTable(tableData);
	}
	
	//Sprawdza czy wszystkie boosty jeszcze mogą istnieć. Jeśli nie - usuwa ich.
	private void checkBoostRounds() {
		
	}
	
	public Object[] getWarrior(int warrior) {
		//pobieramy bazę danych
		Object[][] tableData = standJTableModel.getData();
		
		//konwertujemy liczbę z typu view na model
		int viewAppSelectedWarrior = this.getRowSorter().convertRowIndexToModel(warrior);
		
		
		try {
			return tableData[viewAppSelectedWarrior];
		} catch (NullPointerException ex) {
			System.out.println("There is no warrior!");
			return null;
		}
	}
	
	public Object[] getActiveWarrior() {
		
		//pobieramy bazę danych i zwracamy wojownika z aktywną turą
		Object[][] tableData = standJTableModel.getData();
		
		
		
		//konwertujemy liczbę z typu view na model
		int viewAppSelectedRow  = this.getRowSorter().convertRowIndexToModel(appSelectedRow);
		
		
		
		try {
			return tableData[viewAppSelectedRow];
		} catch (NullPointerException ex) {
			System.out.println("There is no warrior!");
			return null;
		}
		
	}
	
	private void updateDataInTable(Object[][] newData) {
		//Sprawdzamy czy u jakiegoś wojownika rundy bustu nie są równe zero. Jeśli równe - usuwamy ich.
		newData = checkAndDeleteUselessWarriorBoostRounds(newData);
		
		
		//Ustawiamy model na nowo, usuwając stary
		standJTableModel = new StandJTableModel(newData);
		this.setModel(standJTableModel);
		this.repaint();
		
		//Sortowanie
		this.setDefaultSort();
	}
	
	private Object[] getWarriorBoost(int warriorModelIndex) {
		//Pobieramy datę oraz tworzymy zbior z boostami
		Object[][] data = standJTableModel.getData();
		ArrayList<String> boosts = new ArrayList<String>();
		
		//Bierzemy całą linijkę
		String warriorBoosts = (String) data[warriorModelIndex][WARRIOR_BOOST_INDEX];
		
		//Wprowadzenie potrzbnych zmiennych
		boolean isItEnd = false;
		int beginIndex = 0;
		
		while(isItEnd == false) {
			//Indeks końca określonego boosta
			int endIndex = warriorBoosts.indexOf(";", beginIndex);
			if(endIndex == -1) {
				isItEnd = true;
			}
			
			//Bierze jeden boost z całej linijki boostow wojownika, lub, jeśli nie ma już boostu, bierze niepotrzebną literę
			String oneWarriorBoostText = "";
			try {
				oneWarriorBoostText = warriorBoosts.substring(beginIndex, endIndex+1);
				//Dodajemy jeden boost do zbioru
				boosts.add(oneWarriorBoostText);
				//przesuwamy
				beginIndex = beginIndex + (endIndex-beginIndex) + 1;
			} catch (StringIndexOutOfBoundsException ex) {
				isItEnd = true;
			}
		}
		
		//zwracamy w postaci zbioru
		return boosts.toArray();
	}
	
	public void removeBoostWindow(int warriorIndex) {
		//konwertujemy liczbę z typu view na model
		int modelWarriorSelectedindex = this.getRowSorter().convertRowIndexToModel(warriorIndex);
		
		//Pobieramy zbior z bustami
		Object[] warriorBoosts = getWarriorBoost(modelWarriorSelectedindex);
		
		//Tworzymy okno z potrzebnymi do usunięcia rzeczami (boostami)
		if(!warriorBoosts[0].equals("")) {
			RemoveWarriorBoostsFrame rmvWarBoostFrame = new RemoveWarriorBoostsFrame(warriorBoosts, modelWarriorSelectedindex, this, mainFrame);
		} else {
			//Jeśli żadnych boostów nie ma - wróć do głownej ramki i pokaż ramkę z uwagą
			this.resumeBattle();
			JOptionPane.showMessageDialog(null, "Wojownik nie posiada żadnych boostów!", "Uwaga!", JOptionPane.ERROR_MESSAGE);
			
		}
		
		
	}
	
	public void removeSpecifiedWarriorBoost(int warriorModelIndex, String boostString) {
		//pobieramy datę
		Object[][] dataInTable = standJTableModel.getData();
		Object[][] modificatedData = Arrays.copyOf(dataInTable,0) ;
		
		//Pobieramy całą linijkę tekstu
		String boostText = (String) dataInTable[warriorModelIndex][WARRIOR_BOOST_INDEX];
		//Zamieniamy tekst boosta na nic
		boostText = boostText.replace(boostString, "");
		
		//Jeśli jest pusty - daj mu znaczek "-"
		if(boostText.isBlank()) {
			boostText = "-";
		}
		
		dataInTable[warriorModelIndex][WARRIOR_BOOST_INDEX] = boostText;		
		//Odświeżamy zbiór danych w tablicy
		updateDataInTable(dataInTable);
		
	}
	

	//Sprawdza czy któremuś z wojowników skonczył się boost w podanej bazie danych
	private Object[][] checkAndDeleteUselessWarriorBoostRounds(Object[][] newData) {
		
		
		for(int x = 0; x < newData.length; x++) {
			//Sprawdza czy jest w jakimkolwiek wierszu wojownikow liczba rund 0
			int indexZeroRounds = (((String)newData[x][WARRIOR_BOOST_INDEX])).indexOf("R:0");
			if(indexZeroRounds != -1) {
				
				//jeśli tak to inicjalizuje niezbędne przemienne
					//dla wyjścia z pętli while
				boolean isItEnd = false;
					//Wiersz wojownika z boostami
				String warriorBoostText = (String) newData[x][WARRIOR_BOOST_INDEX];
					//Indeks początku boosta
				int beginIndex = 0;
				
				
				while(isItEnd == false) {
					//Indeks końca określonego boosta
					int endIndex = warriorBoostText.indexOf(";", beginIndex);
						//jeżeli już więcej boostow nie ma - chce wyjść z pętli
					if(endIndex == -1) {
						isItEnd = true;
						
					}
					
					//Bierze jeden boost z całej linijki boostow wojownika, lub, jeśli nie ma już boostu, bierze niepotrzebną literę
					String oneWarriorBoostText;
					try {
						oneWarriorBoostText = warriorBoostText.substring(beginIndex, endIndex+1);
					} catch (StringIndexOutOfBoundsException ex) {
						oneWarriorBoostText = warriorBoostText.substring(beginIndex, beginIndex);
					}
					
					//Sprawdzamy czy określony boost musi już się skończyć
					if(endIndex != -1 && oneWarriorBoostText.indexOf("R:0") != -1) {
						//Jeśli to początek wiersza - daj mu 0, lub przesuń początek indeksu dalej
						if(warriorBoostText.indexOf(oneWarriorBoostText) == 0) {
							beginIndex = 0;
						} else {
							beginIndex = beginIndex + (endIndex - beginIndex) + 1;
						}
						//Zamień tekst określonego boostu w ogólnym wierszu na nic
						warriorBoostText = warriorBoostText.replace(oneWarriorBoostText, "");
						
					} else {
						//Lub, jeśli nie ma, przewiń dalej
						beginIndex = beginIndex + (endIndex-beginIndex) + 1;
					}
				}
				
				//Jeśli pole puste - zamień tekst w nim na '-'
				if(warriorBoostText.isBlank()) {
					warriorBoostText = "-";
				}
				
				//powiąż na wszelki wypadek z obiektem warriorBoostText
				newData[x][WARRIOR_BOOST_INDEX] = warriorBoostText;
			}
			
			
		}
		
		//zwróc zmodyfikowaną bazę danych
		return newData;
	}

	public void removeWarrior(int toRemoveWarrior) {
		//Sprawdzamy czy linijka potrzebna do usunięcia jest wojownikiem aktywnym
		if(appSelectedRow != -1 && this.getRowSorter().convertRowIndexToModel(appSelectedRow) == toRemoveWarrior ) {
			//Jeśli tak, to nie chcemy żeby jakakolwiek inna linijka byla na czerwono
				//Codamy do tylu dlatego iż inaczej on przeskakuje przez jednego pierwszego wojownika
				appSelectedRow--;
			isAppSelectedRowTemporaryDeleted = true;
			
		}
		
		//Pobieramy bazę danych
		Object[][] previousData = standJTableModel.getData();
		
		//Dzielimy na dwie części i usuwamy wojownika. W przyszłości musimy te dwa zbiory połączyć
		Object[][] modificatedDataPartFirst = Arrays.copyOfRange(previousData, 0, toRemoveWarrior);
		Object[][] modificatedDataPartTwo = Arrays.copyOfRange(previousData, toRemoveWarrior+1, previousData.length);
		
		Object[][] modificatedData = new Object[previousData.length-1][previousData[0].length];
		
		//Łaczymy dwie bazy danych bez tego co chcemy usunąć
		for(int x = 0; x < modificatedDataPartFirst.length; x++) {
				modificatedData[x] = modificatedDataPartFirst[x];
		}
		for(int x = modificatedDataPartFirst.length, y = 0; x < (modificatedDataPartTwo.length + modificatedDataPartFirst.length); x++, y++) {
			modificatedData[x] = modificatedDataPartTwo[y];
		}
		
		//ktoś musi być w tabeli żeby apka działała dobrze 
		if(modificatedData.length > 0) {
			//Odświeżamy tabele z nową datą
			updateDataInTable(modificatedData);
		} else {
			JOptionPane.showMessageDialog(null, "Ktoś musi pozostać na tabeli!", "Uwaga!", JOptionPane.ERROR_MESSAGE);
			this.resumeBattle();
			}
		
	}
	
	public void addWarrior(Object[] warrior) {
		
		try {
			//Jeżeli dodany wojownik posiada większą zręczność to indeks aktywnego wojownika musi iść dalej bo nie jest to runda dodanego wojownika
			if((int)getActiveWarrior()[WARRIOR_AGILITY_AND_INITIATIVE_INDEX] < (int)warrior[WARRIOR_AGILITY_AND_INITIATIVE_INDEX]) {
				appSelectedRow++;
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			
		}
		
		
		
		//Kopiowanie starego żeby go poszerzyć o 1 i dodać wojownika
		Object[][] previousData = standJTableModel.getData();
		
		Object[][] modificatedData = Arrays.copyOf(previousData, previousData.length+1);
		modificatedData[modificatedData.length-1] = warrior;
		
		//Odświeżamy tabele z nową datą
		updateDataInTable(modificatedData);
		
		if(WARRIOR_TO_DELETE_ROW_INDEX >= 0) {
			//Jeśli istnieje wojownik "WAR_TO_DEL" - należy go usunąć
			Object[][] data = standJTableModel.getData();
			for (int i = 0; i < data.length; i++) {
				if(data[i][WARRIOR_NAME_INDEX] == WARRIOR_TO_DELETE) {
					this.removeWarrior(i);
					//Zmieniamy zmienna bo już usunęliśmy wojownika
					WARRIOR_TO_DELETE_ROW_INDEX = -1;
				}
			}
		}
		
		
		
	}
	
	
	
	public void setDefaultSort() {
		//Sortowanie
		this.setAutoCreateRowSorter(true);
		this.getRowSorter().toggleSortOrder(1); this.getRowSorter().toggleSortOrder(1);
		
		
		
		for (int col = 0; col < this.getColumnCount(); col++) {
			((DefaultRowSorter) this.getRowSorter()).setSortable(col, false);
		}
		
	}
	
	
	public void setRowSelected(int row) { 
		//Wybrana rzecz zmienia swoj kolor na czerwony
		appSelectedRow = row;
		this.repaint();
	}
	
	public void clearRowSelection() {
		//Zmienia wszystkie kolory wbranej/ych czcionek na standardowy.
		appSelectedRow = -1;
		this.repaint();
	}
	
	//Metoda do wcztywania tabeli
	public void loadStandJTable(Object loadedAppSelectedRow, Object loadedIsAppSelectedRowTemporaryDeleted, Object loadedData) {
		appSelectedRow = (int) loadedAppSelectedRow;
		isAppSelectedRowTemporaryDeleted = (boolean) loadedIsAppSelectedRowTemporaryDeleted;
		updateDataInTable((Object[][]) loadedData);
		
		
		//Założymy że wojownika do usunięcia początkowego w wgranej bitwie nie istnieje
		WARRIOR_TO_DELETE_ROW_INDEX = -1;
	}
	
	//Metoda do zapisywanie niezbędnych danych w tabeli do pliku w celu odtworzenia ich w tabeli w przyszłości
	//[0] - int appSelectedRow
	//[1] - boolean isAppSelectedRowTemporaryDeleted
	//[2] - Object[][] data
	public Object[] getDataForSavedStandJTable() {
		Object[] neededData = {appSelectedRow, isAppSelectedRowTemporaryDeleted, standJTableModel.getData()};
		return neededData;
	}
	
	//Standartyzowana klasa dla tworzenia wzorowca tablic w jednym stylu
	public class StandJTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		private String[] columnNames = {"Imie", "Szybkość", "Ulepszenia"};
		public final Object[][] DEFAULT_DATA = {};
		private Object[][] data = {{"Wojownik1", 51, "-"}};
		
		StandJTableModel() {
			data = DEFAULT_DATA;
		}
		
		StandJTableModel(Object[][] object) {
			data = object;
		}
		
		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return data.length;
		}
		
		@Override
		public String getColumnName(int col) {
	        return columnNames[col];
	    }

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			try {
				return data[rowIndex][columnIndex];
			} catch (ArrayIndexOutOfBoundsException ex) {
				//Ustawiamy pierwszego wojownika oraz zmienną (coś musi być w tabeli, jednak będzie niewidoczne)    
				data = new Object[][] {{WARRIOR_TO_DELETE, 51, "-"}};
				WARRIOR_TO_DELETE_ROW_INDEX = 0;
				return data[rowIndex][columnIndex];
			}
			
				
			
			
		}
		
		public Class getColumnClass(int c) {
			return getValueAt(0,c).getClass();
		}
		public boolean isCellEditable(int row, int col) {
			return false;
		}
		
		public Object[][] getData() {
			return data;
		}
		public void removeRow(int row) {
		    // remove a row from your internal data structure
		    fireTableRowsDeleted(row, row);
		}
		
		
	}
	
	
	
	//Custom DefaultTableCellRenderer
    public static class CustomTableRenderer extends DefaultTableCellRenderer {
    	@Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component c = super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            
            //Jeśli to jest wiersz do usunięcia - zamaskuj go
            if(WARRIOR_TO_DELETE_ROW_INDEX == row) {
            	c.setForeground(Color.WHITE);
            	return c;
            }
            
            if(appSelectedRow == row && isAppSelectedRowTemporaryDeleted == false) {
                //set to red bold font
                c.setForeground(Color.RED);
                c.setFont(new StandFont());
            } else {
                //stay at default
                c.setForeground(Color.BLACK);
                c.setFont(new StandFont());
            }
            return c;
        }

    }
	
}


