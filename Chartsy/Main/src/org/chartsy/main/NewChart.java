package org.chartsy.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.exceptions.InvalidStockException;
import org.chartsy.main.exceptions.RegistrationException;
import org.chartsy.main.exceptions.StockNotFoundException;
import org.chartsy.main.intervals.DailyInterval;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class NewChart implements ActionListener
{

    private final static RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
    private NewChartDialog dialog;
    private boolean newChart = true;
	private boolean canOpen = true;

    public void actionPerformed(final ActionEvent e)
    {
		if (newChart)
			dialog = new NewChartDialog(new JFrame(), true);
		dialog.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
		dialog.setVisible(true);

		if (!dialog.isVisible())
		{
			final Stock stock = dialog.getStock();
			final DataProvider dataProvider = dialog.getDataProvider();
			final Chart chart = dialog.getChart();

			if (dataProvider != null && stock != null && chart != null)
			{
				final RequestProcessor.Task stockTask = RP.create(new Runnable()
				{
					public void run()
					{
						canOpen = true;
						try
						{
							dataProvider.setStockCompanyName(stock);
						} 
						catch (InvalidStockException ex)
						{
							notifyError(e, ex);
						} 
						catch (StockNotFoundException ex)
						{
							notifyError(e, ex);
						} 
						catch (RegistrationException ex)
						{
							notifyError(e, ex);
						} 
						catch (IOException ex)
						{
							notifyError(e, new InvalidStockException());
						}
					}
				});

				final ProgressHandle handle = ProgressHandleFactory.createHandle("Aquiring stock info", stockTask);
				stockTask.addTaskListener(new TaskListener()
				{
					public void taskFinished(Task task)
					{
						handle.finish();
						if (canOpen)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									dataProvider.addStock(stock);
									openNewChart(stock, dataProvider, chart);
								}
							});
						}
					}
				});

				handle.start();
				stockTask.schedule(0);
			}
		}
    }

	private void notifyError(final ActionEvent e, Throwable t)
	{
		canOpen = false;
		NotifyDescriptor descriptor
			= new NotifyDescriptor.Exception(t);
		Object ret = DialogDisplayer.getDefault().notify(descriptor);
		if (ret.equals(NotifyDescriptor.OK_OPTION))
		{
			newChart = false;
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					actionPerformed(e);
				}
			});
		}
	}

    private void openNewChart(Stock stock, DataProvider dataProvider, Chart chart)
    {
        newChart = true;
        ChartData cd = new ChartData();
        cd.setStock(stock);
        cd.setChart(chart);
        cd.setDataProvider(dataProvider);
        cd.setInterval(new DailyInterval());
		cd.setDataset(new Dataset());

        ChartFrame chartFrame = new ChartFrame(cd);
        chartFrame.open();
        chartFrame.requestActive();
    }

}
