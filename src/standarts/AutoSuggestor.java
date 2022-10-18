package standarts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

/*How to use:
 * AutoSuggestor autoSuggestor = new AutoSuggestor(f, frame, null, Color.WHITE.brighter(), Color.BLUE, Color.RED, 0.75f) {
            @Override
            boolean wordTyped(String typedWord) {

                //create list for dictionary this in your case might be done via calling a method which queries db and returns results as arraylist
                ArrayList<String> words = new ArrayList<>();
                words.add("hello");
                words.add("heritage");
                words.add("happiness");
                words.add("goodbye");
                words.add("cruel");
                words.add("car");
                words.add("war");
                words.add("will");
                words.add("world");
                words.add("wall");


                setDictionary(words);
                //addToDictionary("bye");//adds a single word

                return super.wordTyped(typedWord);//now call super to check for any matches against newest dictionary
            }
        };
 */


public class AutoSuggestor extends StandJTextField {

	private WarriorChoosedListener listener = null;
    private StandJTextField textField;
    private JPanel suggestionsPanel;
    private JWindow autoSuggestionPopUpWindow;
    private String typedWord;
    private final ArrayList<String> dictionary = new ArrayList<>();
    private int currentIndexOfSpace, tW, tH;
    private DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }
    };
    private final Color suggestionsTextColor;
    private final Color suggestionFocusedColor;

    public AutoSuggestor(StandJTextField sendedTextField, ArrayList<String> words, Color popUpBackground, Color textColor, Color suggestionFocusedColor, float opacity) {
    	super(8);
    	this.textField = this;
        
        this.suggestionsTextColor = textColor;
        this.suggestionFocusedColor = suggestionFocusedColor;
        this.textField.getDocument().addDocumentListener(documentListener);

        setDictionary(words);

        typedWord = "";
        currentIndexOfSpace = 0;
        tW = 0;
        tH = 0;

        autoSuggestionPopUpWindow = new JWindow();
        autoSuggestionPopUpWindow.setOpacity(opacity);

        suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new GridLayout(0, 1));
        suggestionsPanel.setBackground(popUpBackground);

        addKeyBindingToRequestFocusInPopUpWindow();
    }

    private void addKeyBindingToRequestFocusInPopUpWindow() {
        textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
        textField.getActionMap().put("Down released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            	//System.out.println("txt - whn fcsd");
            	//focuses the first label on popwindow
                for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
                    if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
                        ((SuggestionLabel) suggestionsPanel.getComponent(i)).setFocused(true);
                        autoSuggestionPopUpWindow.toFront();
                        if(autoSuggestionPopUpWindow.requestFocusInWindow()) {
                        	 suggestionsPanel.requestFocusInWindow();
                             suggestionsPanel.getComponent(i).requestFocusInWindow();
                        } else { 
                        	autoSuggestionPopUpWindow.requestFocus();
                         	suggestionsPanel.requestFocus();
                         	suggestionsPanel.getComponent(i).requestFocus();
                        }
                        break;
                    }
                }
            }
        });
        textField.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
        textField.getActionMap().put("Down released", new AbstractAction() {
            int lastFocusableIndex = 0;

            @Override
            public void actionPerformed(ActionEvent ae) {//allows scrolling of labels in pop window (I know very hacky for now :))
            	
            	//System.out.println("panel");
                ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
                int max = sls.size();

                if (max > 1) {//more than 1 suggestion
                    for (int i = 0; i < max; i++) {
                        SuggestionLabel sl = sls.get(i);
                        if (sl.isFocused()) {
                            if (lastFocusableIndex == max - 1) {
                                lastFocusableIndex = 0;
                                sl.setFocused(false);
                                autoSuggestionPopUpWindow.setVisible(false);
                                setFocusToTextField();
                                checkForAndShowSuggestions();//fire method as if document listener change occured and fired it

                            } else {
                                sl.setFocused(false);
                                lastFocusableIndex = i;
                            }
                        } else if (lastFocusableIndex <= i) {
                            if (i < max) {
                                sl.setFocused(true);
                                autoSuggestionPopUpWindow.toFront();
                                autoSuggestionPopUpWindow.requestFocusInWindow();
                                suggestionsPanel.requestFocusInWindow();
                                suggestionsPanel.getComponent(i).requestFocusInWindow();
                                lastFocusableIndex = i;
                                break;
                            }
                        }
                    }
                } else {//only a single suggestion was given
                	//System.out.println("here");
                	
                	
                	
                	
                    autoSuggestionPopUpWindow.setVisible(false);
                    setFocusToTextField();
                    checkForAndShowSuggestions();//fire method as if document listener change occured and fired it
                    
                    	//focuses the first label on popwindow
                        if (suggestionsPanel.getComponent(0) instanceof SuggestionLabel) {
                            ((SuggestionLabel) suggestionsPanel.getComponent(0)).setFocused(true);
                            autoSuggestionPopUpWindow.toFront();
                            if(autoSuggestionPopUpWindow.requestFocusInWindow()) {
                            	 suggestionsPanel.requestFocusInWindow();
                                 suggestionsPanel.getComponent(0).requestFocusInWindow();
                            } else { 
                            	autoSuggestionPopUpWindow.requestFocus();
                             	suggestionsPanel.requestFocus();
                             	suggestionsPanel.getComponent(0).requestFocus();
                            }
                        }
                        
                }
            }
        });
        textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "Enter released");
        textField.getActionMap().put("Enter released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
               // replaceWithSuggestedText();
               // autoSuggestionsPopUpWindow.setVisible(false);
                
                ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
                int max = sls.size();
                
                for(int i = 0; i < max; i++) {
                	SuggestionLabel sl = sls.get(i);
                	if(sl.isFocused()) {
                		sl.actionOnEnter();
                	}
                }
                
            }
        });
    }

    private void setFocusToTextField() {
        if(!textField.requestFocusInWindow()) {
        	textField.requestFocus();
        }
    }

    public ArrayList<SuggestionLabel> getAddedSuggestionLabels() {
        ArrayList<SuggestionLabel> sls = new ArrayList<>();
        for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
            if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
                SuggestionLabel sl = (SuggestionLabel) suggestionsPanel.getComponent(i);
                sls.add(sl);
            }
        }
        return sls;
    }

    private void checkForAndShowSuggestions() {
        typedWord = getCurrentlyTypedWord();

        suggestionsPanel.removeAll();//remove previos words/jlabels that were added

        //used to calcualte size of JWindow as new Jlabels are added
        tW = 0;
        tH = 0;

        boolean added = wordTyped(typedWord);

        if (!added) {
            if (autoSuggestionPopUpWindow.isVisible()) {
                autoSuggestionPopUpWindow.setVisible(false);
            }
        } else {
            showPopUpWindow();
            setFocusToTextField();
        }
    }

    protected void addWordToSuggestions(String word) {
        SuggestionLabel suggestionLabel = new SuggestionLabel(word, suggestionFocusedColor, suggestionsTextColor, this);

        /*//Dodajemy nasłuchującą na wstawianie tekstu metodę
         listener = new WarriorChoosedListener() {

        	@Override
			public void onWarriorChoosed(String warrior) {
				//Wyślij zdarzenie dalej
				//System.out.println(warrior);
				
			}
        	
        };*/
        try {
        	suggestionLabel.setWarriorChoosedListener(listener);
        } catch (NullPointerException ex) {
        	//System.out.println("Null");
        }
        
        
        calculatePopUpWindowSize(suggestionLabel);

        suggestionsPanel.add(suggestionLabel);
    }

    public String getCurrentlyTypedWord() {//get newest word after last white spaceif any or the first word if no white spaces
        String text = textField.getText();
        String wordBeingTyped = "";
        if (text.contains(" ")) {
            int tmp = text.lastIndexOf(" ");
            if (tmp >= currentIndexOfSpace) {
                currentIndexOfSpace = tmp;
                wordBeingTyped = text.substring(text.lastIndexOf(" "));
            }
        } else {
            wordBeingTyped = text;
        }
        return wordBeingTyped.trim();
    }

    private void calculatePopUpWindowSize(JLabel label) {
        //so we can size the JWindow correctly
        if (tW < label.getPreferredSize().width) {
            tW = label.getPreferredSize().width;
        }
        tH += label.getPreferredSize().height;
    }

    private void showPopUpWindow() {
        autoSuggestionPopUpWindow.getContentPane().add(suggestionsPanel);
        autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textField.getWidth(), 30));
        autoSuggestionPopUpWindow.setSize(tW, tH);
        autoSuggestionPopUpWindow.setVisible(true);

        int windowX = 0;
        int windowY = 0;

        /*windowX = container.getX() + textField.getX() + 5;
        if (suggestionsPanel.getHeight() > autoSuggestionPopUpWindow.getMinimumSize().height) {
            windowY = container.getY() + textField.getY() + textField.getHeight() + autoSuggestionPopUpWindow.getMinimumSize().height;
        } else {
            windowY = container.getY() + textField.getY() + textField.getHeight() + autoSuggestionPopUpWindow.getHeight();
        }*/
        
        Point upperLeftCoordinate = textField.getLocationOnScreen();
        
        windowX = upperLeftCoordinate.x;
        windowY = upperLeftCoordinate.y  + textField.getHeight();
        /*if (suggestionsPanel.getHeight() > autoSuggestionPopUpWindow.getMinimumSize().height) {
            windowY = upperLeftCoordinate.y + textField.getHeight() + autoSuggestionPopUpWindow.getMinimumSize().height;
        } else {
        	 windowY = upperLeftCoordinate.y + textField.getHeight() + autoSuggestionPopUpWindow.getHeight();
        }*/
        
       

        autoSuggestionPopUpWindow.setLocation(windowX, windowY);
        autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textField.getWidth(), 30));
        autoSuggestionPopUpWindow.revalidate();
        autoSuggestionPopUpWindow.repaint();

    }

    public void setDictionary(ArrayList<String> words) {
        dictionary.clear();
        if (words == null) {
            return;//so we can call constructor with null value for dictionary without exception thrown
        }
        for (String word : words) {
            dictionary.add(word);
        }
    }

    public JWindow getAutoSuggestionPopUpWindow() {
        return autoSuggestionPopUpWindow;
    }

    public StandJTextField getStandJTextField() {
        return textField;
    }

    public void addToDictionary(String word) {
        dictionary.add(word);
    }

    boolean wordTyped(String typedWord) {

        if (typedWord.isEmpty()) {
            return false;
        }
        ////System.out.println("Typed word: " + typedWord);

        boolean suggestionAdded = false;

        for (String word : dictionary) {//get words in the dictionary which we added
            boolean fullymatches = true;
            for (int i = 0; i < typedWord.length(); i++) {//each string in the word
                if (!typedWord.toLowerCase().startsWith(String.valueOf(word.toLowerCase().charAt(i)), i)) {//check for match
                    fullymatches = false;
                    break;
                }
            }
            if (fullymatches) {
                addWordToSuggestions(word);
                suggestionAdded = true;
            }
        }
        return suggestionAdded;
    }
    
    public void setWarriorChoosedListener(WarriorChoosedListener listener2) {
    	this.listener = listener2;
    }
    
    public WarriorChoosedListener getWarriorChoosedListener() {
    	return listener;
    }
    
    
  
}

class SuggestionLabel extends JLabel {
	
	private WarriorChoosedListener listener = null;

    private boolean focused = false;
    private final JWindow autoSuggestionsPopUpWindow;
    private final JTextField textField;
    private final AutoSuggestor autoSuggestor;
    private Color suggestionsTextColor, suggestionBorderColor;

    public SuggestionLabel(String string, final Color borderColor, Color suggestionsTextColor, AutoSuggestor autoSuggestor) {
        super(string);

        this.suggestionsTextColor = suggestionsTextColor;
        this.autoSuggestor = autoSuggestor;
        this.textField = autoSuggestor.getStandJTextField();
        this.suggestionBorderColor = borderColor;
        this.autoSuggestionsPopUpWindow = autoSuggestor.getAutoSuggestionPopUpWindow();

        initComponent();
    }

    private void initComponent() {
        setFocusable(true);
        setForeground(suggestionsTextColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                replaceWithSuggestedText();

                autoSuggestionsPopUpWindow.setVisible(false);
            }
        });

        
    }
    
    public void actionOnEnter() {
    	 replaceWithSuggestedText();
         autoSuggestionsPopUpWindow.setVisible(false);
    }

    public void setFocused(boolean focused) {
        if (focused) {
            setBorder(new LineBorder(suggestionBorderColor));
        } else {
            setBorder(null);
        }
        repaint();
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    private void replaceWithSuggestedText() {
        String suggestedWord = getText();
        String text = textField.getText();
        String typedWord = autoSuggestor.getCurrentlyTypedWord();
        String t = text.substring(0, text.lastIndexOf(typedWord));
        String tmp = t + text.substring(text.lastIndexOf(typedWord)).replace(typedWord, suggestedWord);
        textField.setText(tmp);
        
        listener.onWarriorChoosed(suggestedWord);
    }
    
    public void setWarriorChoosedListener(WarriorChoosedListener listener) {
    	this.listener = listener;
    }
    
    
    
}




class NoTextSelectionCaret extends DefaultCaret {
	public NoTextSelectionCaret(JTextComponent textComponent) {
		textComponent.setHighlighter(null);
	}
	
	@Override
	public int getMark() {
		return getDot();
	}
	
}