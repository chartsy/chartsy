package org.chartsy.main;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Stock;
import org.chartsy.main.data.StockNode;
import org.chartsy.main.data.StockSet;
import org.chartsy.main.events.StockEvent;
import org.chartsy.main.events.StockListener;
import org.chartsy.main.history.HistoryItem;
import org.chartsy.main.resources.ResourcesUtils;
import org.chartsy.main.utils.AutocompletePopup;
import org.chartsy.main.utils.UppercaseDocumentFilter;
import org.chartsy.main.utils.Word;

/**
 *
 * @author viorel.gheba
 */
public class SymbolChanger extends JToolBar implements Serializable
{

    private static final long serialVersionUID = 2L;
    private static final char[] WORD_SEPARATORS = {' ', '\n', '\t', ',', ';', '!', '?', '\'', '(', ')', '[', ']', '\"', '{', '}', '/', '\\', '<', '>'};

    private ChartFrame chartFrame;
    private JTextField txtSymbol;
    private JButton btnSubmit;
    private JButton btnBack;
    private JButton btnForward;
    private JButton btnBackHistory;
    private JButton btnForwardHistory;

    private DataProvider dataProvider;
    private AutocompletePopup menuWindow;
    private Word word;

    public SymbolChanger(ChartFrame frame)
    {
        super(JToolBar.HORIZONTAL);
        chartFrame = frame;
        addStockListeners(frame);
        setFloatable (false);
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        initComponents();
    }

    private void initComponents()
    {
        Insets margins = new Insets(0,2,0,2);

        // symbol text field
        txtSymbol = new JTextField(6);
        ((AbstractDocument)txtSymbol.getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
        txtSymbol.setMargin(margins);
        txtSymbol.setToolTipText("Press 'Ctrl+SPACE' for symbol search");
        txtSymbol.setText(chartFrame.getChartData().getStock().getKey());
        Dimension d = new Dimension(50, 20);
        txtSymbol.setPreferredSize(d);
        txtSymbol.setMinimumSize(d);
        txtSymbol.setMaximumSize(d);

        // submit button
        btnSubmit = getButton(getSubmitAction(), "Submit Symbol");

        // back button
        btnBack = getButton(getBackAction(), "Go Back");

        // forward button
        btnForward = getButton(getForwardAction(), "Go Forward");

        // back history button
        btnBackHistory = getButton(getBackListAction(), "Go Back");

        // forward history button
        btnForwardHistory = getButton(getForwardListAction(), "Go Forward");

        if (!chartFrame.getHistory().hasBackHistory())
        {
            btnBack.setEnabled(false);
            btnBackHistory.setEnabled(false);
        }
        if (!chartFrame.getHistory().hasFwdHistory())
        {
            btnForward.setEnabled(false);
            btnForwardHistory.setEnabled(false);
        }

        add(txtSymbol);
        add(btnSubmit);
        add(btnBack);
        add(btnForward);
        add(btnBackHistory);
        add(btnForwardHistory);

        dataProvider = chartFrame.getChartData().getDataProvider();
        menuWindow = new AutocompletePopup(txtSymbol);
        word = new Word(txtSymbol);
        setEventManagement();
    }

    private void setEventManagement()
    {
        txtSymbol.addFocusListener(new FocusAdapter()
        {
            public @Override void focusLost(FocusEvent e)
            {
                if (menuWindow.isVisible())
                    txtSymbol.requestFocus();
            }
        });

        txtSymbol.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK), "controlEspace");
        txtSymbol.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.CTRL_MASK), "home");
        txtSymbol.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.CTRL_MASK), "end");

        txtSymbol.getActionMap().put("controlEspace", new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            { onControlSpace(); }
        });

        txtSymbol.getActionMap().put("home", new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            { menuWindow.moveStart(); }
        });

        txtSymbol.getActionMap().put("end", new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            { menuWindow.moveEnd(); }
        });

        txtSymbol.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if (menuWindow.isVisible())
                    menuWindow.setVisible(false);
            }
        });

        txtSymbol.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.isConsumed())
                    return;
                if (menuWindow.isVisible())
                {
                    switch (e.getKeyCode())
                    {
                        case KeyEvent.VK_ENTER:
                            menuWindow.onSelected();
                            e.consume();
                            break;
                        case KeyEvent.VK_DOWN:
                            menuWindow.moveDown();
                            e.consume();
                            break;
                        case KeyEvent.VK_UP:
                            menuWindow.moveUp();
                            e.consume();
                            break;
                        case KeyEvent.VK_PAGE_DOWN:
                            menuWindow.movePageDown();
                            e.consume();
                            break;
                        case KeyEvent.VK_PAGE_UP:
                            menuWindow.movePageUp();
                            e.consume();
                            break;
                    }
                }
                else
                {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                        btnSubmit.doClick();
                }
            }
        });

        txtSymbol.getDocument().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                if (menuWindow.isVisible())
                {
                    int beginIndex = e.getOffset();
                    int endIndex = beginIndex + e.getLength();
                    String newCharacters = txtSymbol.getText().substring(beginIndex, endIndex);
                    for (int i = 0; i < WORD_SEPARATORS.length; i++)
                    {
                        if (newCharacters.indexOf(WORD_SEPARATORS[i]) != -1)
                        {
                            word.setBounds(-1, 0);
                            menuWindow.setWords(new StockNode[] {});
                            menuWindow.setVisible(false);
                            return;
                        }
                    }
                    word.increaseLength(e.getLength());
                    updateMenu();
                }
            }
            public void removeUpdate(DocumentEvent e)
            {
                if (menuWindow.isVisible())
                {
                    word.decreaseLength(e.getLength());
                    if (word.getLength() == 0)
                    {
                        menuWindow.setWords(new StockNode[] {});
                        menuWindow.setVisible(false);
                        return;
                    }
                    updateMenu();
                }
            }
            public void changedUpdate(DocumentEvent e)
            {}
        });
    }

    private StockNode[] getWords(String word)
    {
        word = word.trim().toLowerCase();
        StockSet returnSet = getSet(word);
        return returnSet.toArray(new StockNode[returnSet.size()]);
    }

    private boolean isWordSeparator(char aChar)
    {
        for (int i = 0; i < WORD_SEPARATORS.length; i++)
        {
            if (aChar == WORD_SEPARATORS[i])
                return true;
        }
        return false;
    }

    private void onControlSpace()
    {
        word = getCurrentTypedWord();
        if (word.getLength() == 0)
            return;
        int index = word.getStart();
        Rectangle rect = null;
        try
        {
            rect = txtSymbol.getUI().modelToView(txtSymbol, index);
        }
        catch (BadLocationException e)
        {}
        if (rect == null)
            return;

        menuWindow.show(txtSymbol, rect.x, rect.y + rect.height);
        updateMenu();
        txtSymbol.requestFocus();
    }

    private void updateMenu()
    {
        if (word.getLength() == 0)
            return;
        StockNode[] words = getWords(word.toString());
        menuWindow.setWords(words);
    }

    private Word getCurrentTypedWord()
    {
        Word w = new Word(txtSymbol);
        int position = txtSymbol.getCaretPosition();
        if (position == 0)
            return w;
        int index = position - 1;
        boolean found = false;
        while ((index > 0) && (!found))
        {
            char current = txtSymbol.getText().charAt(index);
            if (isWordSeparator(current))
            {
                found = true;
                index++;
            }
            else
            {
                index--;
            }
        }
        w.setBounds(index, position - index);
        return w;
    }

    private StockSet getSet(String word)
    {
        StockSet returnSet = dataProvider.getAutocomplete(word);
        return returnSet;
    }

    private JButton getButton(AbstractAction action, String tooltip)
    {
        JButton button = new JButton(action);
        button.setText("");
        button.setToolTipText(tooltip);
        button.setMargin(new Insets(0,2,0,2));
        button.setBorderPainted(false);
        return button;
    }

    public void updateToolbar()
    {
        removeAll();
        initComponents();
        validate();
        repaint();
    }

    private AbstractAction getSubmitAction()
    {
        return new AbstractAction("Submit", ResourcesUtils.getIcon("ok"))
        {
            public void actionPerformed(ActionEvent e)
            {
                String oldSymbol = chartFrame.getChartData().getStock().getKey();
                String newSymbol = txtSymbol.getText().trim();
                if (!newSymbol.equals(oldSymbol))
                {
                    chartFrame.getTimer().cancel();
                    HistoryItem current = chartFrame.getHistory().getCurrent();
                    chartFrame.getHistory().addHistoryItem(current);
                    chartFrame.getHistory().clearForwardHistory();

                    String delimiter = "\\.";
                    String[] temp = null;

                    if (newSymbol.contains(delimiter))
                        temp = newSymbol.split(delimiter,2);

                    Stock newStock;
                    if (temp != null)
                    {
                        newStock = new Stock(temp[0], temp[1]);
                    } 
                    else
                    {
                        newStock = new Stock(newSymbol);
                    }
                    HistoryItem item = new HistoryItem(newStock, chartFrame.getChartData().getInterval());
                    fireStockEvent(new StockEvent(item));
                }
            }
        };
    }

    private AbstractAction getBackAction() {
        return new AbstractAction("Go Back", ResourcesUtils.getIcon("back")) {
            public void actionPerformed(ActionEvent e) {
                HistoryItem item = chartFrame.getHistory().go(-1);
                if (item != null)
                {
                    chartFrame.getTimer().cancel();
                    fireStockEvent(new StockEvent(item));
                }
            }
        };
    }

    private AbstractAction getForwardAction() {
        return new AbstractAction("Go Forward", ResourcesUtils.getIcon("forward")) {
            public void actionPerformed(ActionEvent e) {
                HistoryItem item = chartFrame.getHistory().go(1);
                if (item != null)
                {
                    chartFrame.getTimer().cancel();
                    fireStockEvent(new StockEvent(item));
                }
            }
        };
    }

    private AbstractAction getBackListAction() {
        return new AbstractAction("Go Back", ResourcesUtils.getIcon("backHistory")) {
            public void actionPerformed(ActionEvent e) {
                List<HistoryItem> list = chartFrame.getHistory().getBackHistory();
                if (list.size() > 0)
                {
                    JButton btn = (JButton) e.getSource();
                    JPopupMenu popup = new JPopupMenu();

                    for (int i = list.size() - 1; i >= 0; i--)
                    {
                        final int index = i - list.size();
                        JMenuItem item = new JMenuItem(getMenuItemAction(list.get(i).toString(), index));
                        item.setToolTipText("Back");
                        item.setMargin(new Insets(0,0,0,0));
                        popup.add(item);
                    }

                    popup.addSeparator();

                    JMenuItem item = new JMenuItem(getClearAction(true));
                    item.setToolTipText("Clear");
                    item.setMargin(new Insets(0,0,0,0));
                    popup.add(item);

                    popup.show(btn, 0, btn.getHeight());
                }
            }
        };
    }

    private AbstractAction getForwardListAction() {
        return new AbstractAction("Go Forward", ResourcesUtils.getIcon("forwardHistory")) {
            public void actionPerformed(ActionEvent e) {
                List<HistoryItem> list = chartFrame.getHistory().getFwdHistory();
                if (list.size() > 0)
                {
                    JButton btn = (JButton) e.getSource();
                    JPopupMenu popup = new JPopupMenu();

                    for (int i = 0; i < list.size(); i++)
                    {
                        final int index = i + 1;
                        JMenuItem item = new JMenuItem(getMenuItemAction(list.get(i).toString(), index));
                        item.setToolTipText("Forward");
                        item.setMargin(new Insets(0,0,0,0));
                        popup.add(item);
                    }

                    popup.addSeparator();

                    JMenuItem item = new JMenuItem(getClearAction(false));
                    item.setToolTipText("Clear");
                    item.setMargin(new Insets(0,0,0,0));
                    popup.add(item);

                    popup.show(btn, 0, btn.getHeight());
                }
            }
        };
    }

    private AbstractAction getMenuItemAction(String name, final int index) {
        return new AbstractAction(name) {
            public void actionPerformed(ActionEvent e) {
                HistoryItem item = chartFrame.getHistory().go(index);
                if (item != null)
                {
                    chartFrame.getTimer().cancel();
                    fireStockEvent(new StockEvent(item));
                }
            }
        };
    }

    private AbstractAction getClearAction(final boolean back) {
        return new AbstractAction("Clear") {
            public void actionPerformed(ActionEvent e) {
                if (back)
                    chartFrame.getHistory().clearBackHistory();
                else 
                    chartFrame.getHistory().clearForwardHistory();
                
                updateToolbar();
            }
        };
    }

    private transient EventListenerList stockListeners = new EventListenerList();

    public void addStockListeners(StockListener listener)
    {
        if (stockListeners == null)
            stockListeners = new EventListenerList();
        stockListeners.add(StockListener.class, listener);
    }

    public void removeStockListeners(StockListener listener)
    {
        if (stockListeners == null)
        {
            stockListeners = new EventListenerList();
            return;
        }
        stockListeners.remove(StockListener.class, listener);
    }

    public void removeStockListeners()
    {
        if (stockListeners == null)
        {
            stockListeners = new EventListenerList();
            return;
        }

        StockListener[] listeners = stockListeners.getListeners(StockListener.class);
        for (int i = 0; i < listeners.length; i++)
            stockListeners.remove(StockListener.class, listeners[i]);
    }

    public void fireStockEvent(StockEvent evt)
    {
        if (stockListeners != null)
        {
            StockListener[] listeners = stockListeners.getListeners(StockListener.class);
            for (int i = 0; i < listeners.length; i++)
                listeners[i].stockChanged(evt);
        }
    }

}
