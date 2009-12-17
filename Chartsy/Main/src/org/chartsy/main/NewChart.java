package org.chartsy.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.chart.AbstractChart;
import org.chartsy.main.managers.ChartManager;
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
    private String updater = "";
    private String exchange = "";
    private String chart = "";

    public NewChart() {}

    public void setSymbol(String symbol) { this.symbol = symbol.toUpperCase(); }
    public void setUpdater(String updater) { this.updater = updater; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public void setChart(String chart) { this.chart = chart; }

    public void actionPerformed(ActionEvent e) {
        NewChartDialog dialog = new NewChartDialog(new JFrame(), true);
        dialog.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
        dialog.setListener(this);
        dialog.setVisible(true);

        if (!dialog.isVisible()) {
            AbstractUpdater abstractUpdater = UpdaterManager.getDefault().getUpdater(updater);
            if (abstractUpdater != null) {
                if (!symbol.equals("") && !chart.equals("")) {
                    AbstractChart abstractChart = ChartManager.getDefault().getChart(chart);
                    Stock stock = abstractUpdater.getStock(symbol, exchange);
                    if (abstractChart != null && stock != null) {
                        newChartFrame(stock, abstractChart, abstractUpdater);
                    } else {
                        NotifyDescriptor d = new NotifyDescriptor.Message("Can't find data for " + symbol + " symbol.", NotifyDescriptor.INFORMATION_MESSAGE);
                        DialogDisplayer.getDefault().notify(d);
                    }
                }
            }
        }
    }

    protected void newChartFrame(Stock stock, AbstractChart abstractChart, AbstractUpdater abstractUpdater) {
        ChartFrame chartFrame = new ChartFrame();
        chartFrame.setStock(stock);
        chartFrame.setUpdater(abstractUpdater);
        chartFrame.setChart(abstractChart);
        chartFrame.setTime(AbstractUpdater.DAILY);
        chartFrame.open();
        chartFrame.requestActive();
    }

}
