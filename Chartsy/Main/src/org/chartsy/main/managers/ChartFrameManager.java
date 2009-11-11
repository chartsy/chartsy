package org.chartsy.main.managers;

import java.util.Hashtable;
import java.util.Iterator;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.main.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viorel.gheba
 */
public class ChartFrameManager {

    protected static ChartFrameManager instance;
    protected final String chartFrameID = "ChartFrame";
    protected Hashtable<Object, Object> chartFrames;
    protected int nr;

    public static ChartFrameManager getDefault() {
        if (instance == null) instance = new ChartFrameManager();
        return instance;
    }

    protected ChartFrameManager() {}

    public void initialize() {
        chartFrames = new Hashtable<Object, Object>();
        nr = 0;
    }

    public String getID() {
        return chartFrameID + nr;
    }

    public void addChartFrame(Object key, Object value) {
        nr = chartFrames.containsKey(key) ? nr : nr + 1;
        chartFrames.put(key, value);
    }

    public void removeChartFrame(Object key) {
        chartFrames.remove(key);
    }

    public void removeAll() {
        chartFrames.clear();
        nr = 0;
    }

    public void closeAll() {
        Iterator it = chartFrames.keySet().iterator();
        while (it.hasNext()) {
            Object obj = chartFrames.get(it.next());
            if (obj != null && obj instanceof ChartFrame) {
                ChartFrame cf = (ChartFrame) obj;
                cf.setForced(true);
                cf.close();
            }
        }
        removeAll();
    }

    public void openAll() {
        Iterator it = chartFrames.keySet().iterator();
        while (it.hasNext()) {
            Object obj = chartFrames.get(it.next());
            if (obj != null && obj instanceof ChartFrame) ((ChartFrame) obj).open();
        }
    }

    public void saveAll() {
        String updater = UpdaterManager.getDefault().getActiveUpdaterName();
        if (updater != null) {
            String path = FileUtils.SaveFile(updater);
            FileUtils.removeFile(path);
            XMLUtils.createXMLDocument(path);
            Document document = XMLUtils.loadXMLDocument(path);

            if (document != null) {
                Element root = XMLUtils.getRoot(document);
                Iterator it = chartFrames.keySet().iterator();
                while (it.hasNext()) {
                    Object obj = chartFrames.get(it.next());
                    if (obj != null && obj instanceof ChartFrame) {
                        Element element = document.createElement("chartframe");
                        ((ChartFrame) obj).writeXMLDocument(document, element);
                        root.appendChild(element);
                    }
                }
                XMLUtils.saveXMLDocument(document, path);
            } else {
                XMLUtils.createXMLDocument(path);
                saveAll();
            }
        }
    }

    public ChartFrame getChartFrame(Object key) {
        Object obj = chartFrames.get(key);
        if (obj != null && obj instanceof ChartFrame) return (ChartFrame) obj;
        return null;
    }

    public void print() {
        Iterator it = chartFrames.keySet().iterator();
        while (it.hasNext()) System.out.println(it.next().toString());
    }

}
