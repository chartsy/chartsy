package org.chartsy.main.utils;

import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Stock;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Viorel
 */
public final class StockFetcher
{

	private static StockFetcher instance;

	private final static RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
	private Stock aux;

	public static StockFetcher getInstance()
	{
		if (instance == null)
			instance = new StockFetcher();
		return instance;
	}

	private StockFetcher()
	{}

	public Stock fetch(final Stock stock, final DataProvider dataProvider)
	{
		aux = null;
		final RequestProcessor.Task task = RP.create(new Runnable()
		{
			public void run()
			{
				aux = dataProvider.getStock(stock.getSymbol(), stock.getExchange());
			}
		});

		final ProgressHandle handle = ProgressHandleFactory.createHandle("Aquiring stock info", task);
		task.addTaskListener(new TaskListener()
		{
			public void taskFinished(Task task)
			{
				handle.finish();
				if (aux != null
					&& !aux.getKey().isEmpty())
					dataProvider.addStock(aux);
				else
				{
					NotifyDescriptor descriptor = new NotifyDescriptor.Exception
						(new IllegalArgumentException(
						"The symbol is invalid. Please enter a valid symbol."));
					Object ret = DialogDisplayer.getDefault().notify(descriptor);
					if (ret.equals(NotifyDescriptor.OK_OPTION))
					{
						aux = null;
					}
				}
			}
		});

		if (dataProvider.hasStock(stock))
			return dataProvider.getStock(stock);
		else
		{
			handle.start();
			task.schedule(0);
		}

		return aux;
	}

}
