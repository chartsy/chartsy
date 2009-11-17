package org.chartsy.main;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.dialogs.LoaderDialog;
import org.chartsy.main.managers.ChartFrameManager;
import org.chartsy.main.managers.DatasetManager;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.updater.AbstractUpdater;
import org.chartsy.main.utils.Stock;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class NewChart implements ActionListener {

    private String symbol = "";
    private String exchange = "";
    private String chart = "";

    public NewChart() {}

    public void setSymbol(String symbol) { this.symbol = symbol.toUpperCase(); }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public void setChart(String chart) { this.chart = chart; }

    public void actionPerformed(ActionEvent e) {
        AbstractUpdater updater = UpdaterManager.getDefault().getActiveUpdater();
        if (updater != null) {
            NewChartDialog dialog = new NewChartDialog(new JFrame(), true);
            dialog.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            dialog.setListener(this);
            dialog.setVisible(true);

            if (!dialog.isVisible()) {
                if (!symbol.equals("") && !chart.equals("")) {
                    Stock stock = updater.getStock(symbol, exchange);
                    boolean exists = DatasetManager.getDefault().dataExists(stock);
                    if (!exists) {
                        LoaderDialog loader = new LoaderDialog(new JFrame(), true);
                        loader.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
                        loader.update(stock);
                        loader.setVisible(true);

                        if (!loader.isVisible()) {
                            newChartFrame(stock, chart);
                        }
                    } else newChartFrame(stock, chart);
                }
            }
        } else {
            NotifyDescriptor nd = new NotifyDescriptor.Message("Connect to a data provider first.", NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

    protected void newChartFrame(Stock stock, String chart) {
        if (DatasetManager.getDefault().getDataset(DatasetManager.getName(stock, DatasetManager.DAILY)) != null) {
            ChartFrame chartFrame = new ChartFrame(stock, chart);
            chartFrame.setID(ChartFrameManager.getDefault().getID());
            ChartFrameManager.getDefault().addChartFrame(chartFrame.preferredID(), chartFrame);
            chartFrame.open();
            chartFrame.toFront();
            chartFrame.requestActive();
        } else {
            NotifyDescriptor nd = new NotifyDescriptor.Message("There is no data for " + stock.getKey() + " symbol.", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

}
