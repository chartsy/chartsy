package org.chartsy.main.utils.autocomplete;

import javax.swing.text.JTextComponent;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.StockNode;

/**
 *
 * @author Viorel
 */
public class StockAutoCompleter extends AutoCompleter
{

	private DataProvider dataProvider;

	public StockAutoCompleter(JTextComponent comp)
	{
		super(comp);
	}

	public void setDataProvider(DataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}

	protected boolean updateListData()
	{
		String value = component.getText();
		if (!value.isEmpty())
		{
			list.setListData(dataProvider.getAutocomplete(value).stocks());
			return true;
		}
		else
		{
			list.setListData(new String[0]);
			return true;
		}
	}

	protected void acceptedListItem(Object selected)
	{
		if (selected == null)
			return;
		
		if (selected != null
			&& selected instanceof StockNode)
		{
			component.setText(((StockNode) selected).getSymbol());
			popupMenu.setVisible(false);
		}
	}

}
