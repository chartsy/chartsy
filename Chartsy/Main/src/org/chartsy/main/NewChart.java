package org.chartsy.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Stock;
import org.chartsy.main.exceptions.InvalidStockException;
import org.chartsy.main.exceptions.RegistrationException;
import org.chartsy.main.exceptions.StockNotFoundException;
import org.chartsy.main.intervals.DailyInterval;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.DataProviderManager;
import org.chartsy.main.templates.Template;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
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

    private static final RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
	private final Interval defaultInterval = new DailyInterval();
    private NewChartDialog dialog;
    private boolean newChart = true;
    private boolean canOpen = true;
	private RequestProcessor.Task stockTask;

    @Override public void actionPerformed(final ActionEvent e)
    {
        if (newChart)
        {
            dialog = new NewChartDialog(new JFrame(), true);
        }
        dialog.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
        dialog.setVisible(true);

        if (!dialog.isVisible())
        {
            final String symbol = dialog.getStock();
            final String dataProvider = dialog.getDataProvider();
			final DataProvider provider = DataProviderManager.getDefault().getDataProvider(dataProvider);
            final Template template = dialog.getTemplate();

            if (dataProvider != null
                    && symbol != null
                    && template != null)
            {
				final ProgressHandle handle = ProgressHandleFactory.createHandle("Aquiring stock info", new Cancellable()
				{
					@Override
					public boolean cancel()
					{
						if (stockTask == null)
							return false;
						return stockTask.cancel();
					}
				});
				
				final Runnable runnable = new Runnable()
				{
					@Override
					public void run()
					{
						handle.start();
						handle.switchToIndeterminate();
						
						canOpen = true;
						try
						{
							if (!provider.stockExists(symbol))
								provider.fetchStock(symbol);
						} catch (InvalidStockException ex)
                        {
							handle.finish();
                            notifyError(e, ex);
                        } catch (StockNotFoundException ex)
                        {
							handle.finish();
                            notifyError(e, ex);
                        } catch (RegistrationException ex)
                        {
							handle.finish();
                            notifyError(e, ex);
                        } catch (IOException ex)
                        {
							handle.finish();
                            notifyError(e, new InvalidStockException());
                        }
					}
				};
				
                stockTask = RP.create(runnable);
                stockTask.addTaskListener(new TaskListener()
                {
                    @Override
					public void taskFinished(Task task)
                    {
                        handle.finish();
						final Stock stock = provider.fetchStockFromCache(symbol);
                        if (canOpen && stock != null)
                        {
                            SwingUtilities.invokeLater(new Runnable()
							{
                                @Override public void run()
								{
									openNewChart(stock, dataProvider, template);
                                }
                            });
                        }
                    }
                });
				stockTask.schedule(0);
            }
        }
    }

    private void notifyError(final ActionEvent e, Throwable t)
    {
        canOpen = false;
        NotifyDescriptor descriptor = new NotifyDescriptor.Message(t.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
        Object ret = DialogDisplayer.getDefault().notify(descriptor);
        if (ret.equals(NotifyDescriptor.OK_OPTION))
        {
            newChart = false;
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override public void run()
                {
                    actionPerformed(e);
                }
            });
        }
    }

    private void openNewChart(Stock stock, String dataProvider, Template template)
    {
        newChart = true;
        ChartData chartData = new ChartData();
        chartData.setStock(stock);
        chartData.setChart(template.getChart());
        chartData.setDataProviderName(dataProvider);

        Interval minInterval = null;
        Interval maxInterval = null;
        for ( Interval i : chartData.getDataProvider().getSupportedIntervals() ) {
            if ( minInterval == null || i.getLengthInSeconds() < minInterval.getLengthInSeconds() )
                minInterval = i;
            if ( maxInterval == null || i.getLengthInSeconds() > maxInterval.getLengthInSeconds() )
                maxInterval = i;
        }

        chartData.setInterval( defaultInterval.getLengthInSeconds() >= minInterval.getLengthInSeconds()
                             ? defaultInterval.getLengthInSeconds() <= maxInterval.getLengthInSeconds()
                             ? defaultInterval : maxInterval : minInterval );

        ChartFrame chartFrame = ChartFrame.getInstance();
        chartFrame.setChartData(chartData);
        chartFrame.setTemplate(template);

        chartFrame.open();
        chartFrame.requestActive();
    }
	
}
