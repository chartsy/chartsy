package org.chartsy.main;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.text.AbstractDocument;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Stock;
import org.chartsy.main.events.StockEvent;
import org.chartsy.main.history.HistoryItem;
import org.chartsy.main.resources.ResourcesUtils;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.UppercaseDocumentFilter;
import org.chartsy.main.utils.autocomplete.StockAutoCompleter;

/**
 *
 * @author viorel.gheba
 */
public class SymbolChanger extends JToolBar implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private ChartFrame chartFrame;
    private JTextField txtSymbol;
    private JButton btnSubmit;
    private JButton btnBack;
    private JButton btnForward;
    private JButton btnBackHistory;
    private JButton btnForwardHistory;
	
    private DataProvider dataProvider;

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
    }

	public Action submit = new AbstractAction()
	{
		public void actionPerformed(ActionEvent e)
		{
			btnSubmit.doClick();
		}
	};

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
