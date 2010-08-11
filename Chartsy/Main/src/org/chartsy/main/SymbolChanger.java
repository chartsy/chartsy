package org.chartsy.main;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Stock;
import org.chartsy.main.data.StockNode;
import org.chartsy.main.data.StockSet;
import org.chartsy.main.events.StockEvent;
import org.chartsy.main.history.HistoryItem;
import org.chartsy.main.resources.ResourcesUtils;
import org.chartsy.main.utils.AutocompletePopup;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.UppercaseDocumentFilter;
import org.chartsy.main.utils.Word;
import org.chartsy.main.utils.autocomplete.StockAutoCompleter;

/**
 *
 * @author viorel.gheba
 */
public class SymbolChanger extends JToolBar implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;
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
        txtSymbol.setText(chartFrame.getChartData().getStock().getKey());
        Dimension d = new Dimension(50, 20);
        txtSymbol.setPreferredSize(d);
        txtSymbol.setMinimumSize(d);
        txtSymbol.setMaximumSize(d);

        // submit button
        btnSubmit = new JButton(new ChangeStock());
		btnSubmit.setText("");

        // back button
        btnBack = new JButton(new BackAction());
		btnBack.setText("");

        // forward button
        btnForward = new JButton(new ForwardAction());
		btnForward.setText("");

        // back history button
        btnBackHistory = new JButton(new BackListAction());
		btnBackHistory.setText("");

        // forward history button
        btnForwardHistory = new JButton(new ForwardListAction());
		btnForwardHistory.setText("");

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
		final StockAutoCompleter completer = new StockAutoCompleter(txtSymbol);
		completer.setDataProvider(dataProvider);
        //menuWindow = new AutocompletePopup(txtSymbol);
        //word = new Word(txtSymbol);
        //setEventManagement();
    }

	public Action submit = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			btnSubmit.doClick();
		}
	};

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
                            if (menuWindow.hasSelected())
                                menuWindow.onSelected();
                            else
                            {
                                menuWindow.setVisible(false);
                                btnSubmit.doClick();
                            }
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
                    switch (e.getKeyCode())
                    {
                        case KeyEvent.VK_ENTER:
                            btnSubmit.doClick();
                            e.consume();
                            break;
                        default:
                            if (!dataProvider.getName().equals("Yahoo"))
                                onControlSpace();
                            break;
                    }
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

    public void updateToolbar()
    {
        removeAll();
        initComponents();
        validate();
        repaint();
    }

	private void buttonsStatus(boolean status)
	{
		btnBack.setEnabled(status);
		btnBackHistory.setEnabled(status);
		btnForward.setEnabled(status);
		btnForwardHistory.setEnabled(status);
		btnSubmit.setEnabled(status);
	}

	private static abstract class SymbolChangerAction extends AbstractAction
	{

		public SymbolChangerAction(String name, String tooltip, String icon)
		{
			putValue(NAME, name);
			putValue(SHORT_DESCRIPTION, tooltip);
			if (icon != null)
			{
				putValue(SMALL_ICON, ResourcesUtils.getIcon(icon));
				putValue(LARGE_ICON_KEY, ResourcesUtils.getIcon(icon));
			}
		}

	}

	public class ChangeStock extends SymbolChangerAction
	{

		public ChangeStock()
		{
			super("Submit", "Submit Symbol", "ok");
		}

		public void actionPerformed(ActionEvent e)
		{
			buttonsStatus(false);
			String oldSymbol = chartFrame.getChartData().getStock().getKey();
			String newSymbol = txtSymbol.getText().trim();
			
			if (!newSymbol.equals(oldSymbol))
			{
				HistoryItem current = chartFrame.getHistory().getCurrent();
				chartFrame.getHistory().clearForwardHistory();
				chartFrame.getHistory().addHistoryItem(current);

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

				HistoryItem item = new HistoryItem(
					newStock,
					chartFrame.getChartData().getInterval().hashCode());

				chartFrame.stockChanged(new StockEvent(item));
			}
			
			buttonsStatus(true);
		}
		
	}

	public class BackAction extends SymbolChangerAction
	{

		public BackAction()
		{
			super("Go Back", "Go Back", "back");
		}

		public void actionPerformed(ActionEvent e)
		{
			buttonsStatus(false);
			HistoryItem item = chartFrame.getHistory().go(-1);
			if (item != null)
				chartFrame.stockChanged(new StockEvent(item));
			buttonsStatus(true);
		}

	}

	public class ForwardAction extends SymbolChangerAction
	{

		public ForwardAction()
		{
			super("Go Forward", "Go Forward", "forward");
		}

		public void actionPerformed(ActionEvent e)
		{
			buttonsStatus(false);
			HistoryItem item = chartFrame.getHistory().go(1);
			if (item != null)
				chartFrame.stockChanged(new StockEvent(item));
			buttonsStatus(true);
			updateToolbar();
		}

	}

	public class BackListAction extends SymbolChangerAction
	{

		public BackListAction()
		{
			super("Go Back", "Go Back", "backHistory");
		}

		public void actionPerformed(ActionEvent e)
		{
			HistoryItem[] items = chartFrame.getHistory().getBackHistoryList();
			if (items.length > 0)
			{
				JButton btn = (JButton) e.getSource();
				JPopupMenu popup = new JPopupMenu();
				JMenuItem item;

				for (int i = items.length - 1; i >= 0; i--)
				{
					final int index = i - items.length;
					if (items[i] != null)
					{
						String name =
							items[i].getStock().getKey()
							+ " : "
							+ DataProvider.getInterval(items[i].getIntervalHash());

						popup.add(item = new JMenuItem(new GoToItemAction(name, index)));
						item.setMargin(new Insets(0,0,0,0));
					}
				}

				popup.addSeparator();

				popup.add(item = new JMenuItem(new ClearHistoryAction(true)));
				item.setMargin(new Insets(0,0,0,0));

				popup.show(btn, 0, btn.getHeight());
			}
		}

	}

	public class ForwardListAction extends SymbolChangerAction
	{

		public ForwardListAction()
		{
			super("Go Forward", "Go Forward", "forwardHistory");
		}

		public void actionPerformed(ActionEvent e)
		{
			HistoryItem[] items = chartFrame.getHistory().getFwdHistoryList();
			if (items.length > 0)
			{
				JButton btn = (JButton) e.getSource();
				JPopupMenu popup = new JPopupMenu();
				JMenuItem item;

				for (int i = items.length - 1; i >= 0; i--)
				{
					final int index = Math.abs(i - items.length);
					if (items[i] != null)
					{
						String name =
							items[i].getStock().getKey()
							+ " : "
							+ DataProvider.getInterval(items[i].getIntervalHash());

						popup.add(item = new JMenuItem(new GoToItemAction(name, index)));
						item.setMargin(new Insets(0,0,0,0));
					}
				}

				popup.addSeparator();

				popup.add(item = new JMenuItem(new ClearHistoryAction(false)));
				item.setMargin(new Insets(0,0,0,0));

				popup.show(btn, 0, btn.getHeight());
			}
		}

	}

	public class GoToItemAction extends SymbolChangerAction
	{

		private int step = 0;

		public GoToItemAction(String name, int step)
		{
			super(name, name, null);
			this.step = step;
		}

		public void actionPerformed(ActionEvent e)
		{
			buttonsStatus(false);
			HistoryItem item = chartFrame.getHistory().go(step);
			if (item != null)
				chartFrame.stockChanged(new StockEvent(item));
			buttonsStatus(true);
		}

	}

	public class ClearHistoryAction extends SymbolChangerAction
	{

		private boolean back;

		public ClearHistoryAction(boolean back)
		{
			super("Clear", "Clear", null);
			this.back = back;
		}

		public void actionPerformed(ActionEvent e)
		{
			buttonsStatus(false);
			if (back)
				chartFrame.getHistory().clearBackHistory();
			else
				chartFrame.getHistory().clearForwardHistory();

			buttonsStatus(true);
			updateToolbar();
		}

	}

}
