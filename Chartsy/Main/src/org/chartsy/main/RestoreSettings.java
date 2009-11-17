package org.chartsy.main;

import java.util.LinkedHashMap;
import javax.swing.JFrame;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.dialogs.LoaderDialog;
import org.chartsy.main.managers.ChartFrameManager;
import org.chartsy.main.managers.DatasetManager;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.updater.AbstractUpdater;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.main.utils.Stock;
import org.chartsy.main.utils.XMLUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.WindowManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author viorel.gheba
 */
public class RestoreSettings {
    
    public static RestoreSettings newInstance() { return new RestoreSettings(); }

    protected RestoreSettings() {}

    public void restore() {
        String updater = XMLUtils.getActiveDataProvider();
        if (updater != null) {
            UpdaterManager.getDefault().setActiveUpdater(updater);
            AbstractUpdater active = UpdaterManager.getDefault().getActiveUpdater();
            // restore settings
            String path = FileUtils.SaveFile(updater);
            Document document = XMLUtils.loadXMLDocument(path);

            if (document != null) {
                Element root = XMLUtils.getRoot(document);
                if (root != null) {
                    NodeList nodeList = root.getElementsByTagName("chartframe");
                    LinkedHashMap<Object, Object> ht = new LinkedHashMap<Object, Object>();
                    Stock[] stocks = new Stock[nodeList.getLength()];
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Element parent = (Element) nodeList.item(i);
                        Stock stock = active.getStock(XMLUtils.getStringParam(parent, "symbol"), XMLUtils.getStringParam(parent, "exchange"));
                        String time = XMLUtils.getStringParam(parent, "time");
                        stocks[i] = stock;
                        ht.put(stock, time);
                    }
                    LoaderDialog loader = new LoaderDialog(new JFrame(), true);
                    loader.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
                    loader.update(ht);
                    loader.setVisible(true);

                    if (!loader.isVisible()) {
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Element parent = (Element) nodeList.item(i);
                            Stock stock = active.getStock(XMLUtils.getStringParam(parent, "symbol"), XMLUtils.getStringParam(parent, "exchange"));
                            String chart = XMLUtils.getStringParam(parent, "chart");
                            newChartFrame(stock, chart, parent);
                        }
                    }
                }
            }
        } else {
            SelectDataProvider sdp = new SelectDataProvider(new javax.swing.JFrame(), true);
            sdp.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            sdp.setVisible(true);
        }
    }

    protected void newChartFrame(Stock stock, String chart, Element parent) {
        if (DatasetManager.getDefault().getDataset(DatasetManager.getName(stock, DatasetManager.DAILY)) != null) {
            ChartFrame chartFrame = new ChartFrame(stock, chart);
            Element element = (Element) parent.getElementsByTagName("frame").item(0);
            int tab = XMLUtils.getIntegerParam(element, "tabPosition");
            chartFrame.setID(ChartFrameManager.getDefault().getID());
            ChartFrameManager.getDefault().addChartFrame(chartFrame.preferredID(), chartFrame);
            chartFrame.openAtTabPosition(tab);
            if (chartFrame.isOpened()) chartFrame.readXMLDocument(parent);
        } else {
            NotifyDescriptor nd = new NotifyDescriptor.Message("There is no data for " + stock.getKey() + " symbol.", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

}
