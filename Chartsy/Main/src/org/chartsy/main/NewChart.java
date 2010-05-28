package org.chartsy.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Stock;
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
    private Stock stockInfo;
    private boolean newChart = true;

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
                stockInfo = null;
                final RequestProcessor.Task stockTask = RP.create(new Runnable()
                {
                    public void run()
                    {
                        stockInfo = dataProvider.getStock(stock.getSymbol(), stock.getExchange());
                    }
                });

                final ProgressHandle handle = ProgressHandleFactory.createHandle("Aquiring stock info", stockTask);
                stockTask.addTaskListener(new TaskListener()
                {
                    public void taskFinished(Task task)
                    {
                        handle.finish();
                        if (stockInfo != null)
                        {
                            if (!stockInfo.getKey().equals(""))
                            {
                                SwingUtilities.invokeLater(new Runnable()
                                {
                                    public void run()
                                    {
                                        dataProvider.addStock(stockInfo);
                                        openNewChart(stockInfo, dataProvider, chart);
                                    }
                                });
                            }
                        }
                        else
                        {
                            NotifyDescriptor d = new NotifyDescriptor.Exception(new IllegalArgumentException("The symbol is invalid. Please enter a valid symbol."));
                            Object ret = DialogDisplayer.getDefault().notify(d);
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
                    }
                });


                if (dataProvider.hasStock(stock))
                {
                    stockInfo = dataProvider.getStock(stock);
                    openNewChart(stockInfo, dataProvider, chart);
                }
                else
                {
                    handle.start();
                    stockTask.schedule(0);
                }
            }
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

        ChartFrame chartFrame = new ChartFrame(cd);
        chartFrame.open();
        chartFrame.requestActive();
    }

}
