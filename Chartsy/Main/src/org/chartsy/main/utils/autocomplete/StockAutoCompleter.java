package org.chartsy.main.utils.autocomplete;

import java.io.IOException;
import javax.swing.text.JTextComponent;
import org.chartsy.main.data.StockNode;
import org.chartsy.main.data.StockSet;
import org.chartsy.main.managers.DataProviderManager;

/**
 *
 * @author Viorel
 */
public class StockAutoCompleter extends AutoCompleter
{

	private String dataProvider;

	public StockAutoCompleter(JTextComponent comp)
	{
		super(comp);
	}

	public void setDataProvider(String dataProvider)
	{
		this.dataProvider = dataProvider;
	}

	@Override
	protected boolean updateListData() throws IOException
	{
		String value = component.getText();
		if (!value.isEmpty())
		{
			StockSet stockSet = DataProviderManager.getDefault().getDataProvider(dataProvider).fetchAutocomplete(value);
			list.setListData(stockSet.stocks());
			return true;
		}
		else
		{
			list.setListData(new String[0]);
			return true;
		}
	}

	@Override
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
