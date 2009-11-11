package org.chartsy.main;

import java.util.Hashtable;
import javax.swing.JFrame;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.dialogs.LoaderDialog;
import org.chartsy.main.managers.ChartFrameManager;
import org.chartsy.main.managers.DatasetManager;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.utils.FileUtils;
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
            // restore settings
            String path = FileUtils.SaveFile(updater);
            Document document = XMLUtils.loadXMLDocument(path);

            if (document != null) {
                Element root = XMLUtils.getRoot(document);
                if (root != null) {
                    NodeList nodeList = root.getElementsByTagName("chartframe");
                    Hashtable<Object, Object> ht = new Hashtable<Object, Object>();
                    String[] symbols = new String[nodeList.getLength()];
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Element parent = (Element) nodeList.item(i);
                        String symbol = XMLUtils.getStringParam(parent, "symbol");
                        String time = XMLUtils.getStringParam(parent, "time");
                        symbols[i] = symbol;
                        ht.put(symbol, time);
                    }
                    LoaderDialog loader = new LoaderDialog(new JFrame(), true);
                    loader.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
                    loader.update(ht);
                    loader.setVisible(true);

                    if (!loader.isVisible()) {
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Element parent = (Element) nodeList.item(i);
                            String symbol = XMLUtils.getStringParam(parent, "symbol");
                            String chart = XMLUtils.getStringParam(parent, "chart");
                            newChartFrame(symbol, chart, parent);
                        }
                    }
                }
            }
        }
    }

    protected void newChartFrame(String symbol, String chart, Element parent) {
        if (DatasetManager.getDefault().getDataset(DatasetManager.getName(symbol, DatasetManager.DAILY)) != null) {
            ChartFrame chartFrame = new ChartFrame(symbol, chart);
            chartFrame.setID(ChartFrameManager.getDefault().getID());
            ChartFrameManager.getDefault().addChartFrame(chartFrame.preferredID(), chartFrame);
            chartFrame.open();
            if (chartFrame.isOpened()) chartFrame.readXMLDocument(parent);
        } else {
            NotifyDescriptor nd = new NotifyDescriptor.Message("There is no data for " + symbol + " symbol.", NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

}
