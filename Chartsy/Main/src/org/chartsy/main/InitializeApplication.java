package org.chartsy.main;

import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.managers.ChartFrameManager;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.DatasetManager;
import org.chartsy.main.managers.IndicatorManager;
import org.chartsy.main.managers.LoggerManager;
import org.chartsy.main.managers.OverlayManager;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.main.utils.XMLUtils;


/**
 *
 * @author viorel.gheba
 */
public class InitializeApplication {

    private InitializeApplication() {}

    public static void initialize() {
        initializeLocalFolder();

        //ChartFrameManager.getDefault().initialize();
        ChartManager.getDefault().initialize();
        DatasetManager.getDefault().initialize();
        IndicatorManager.getDefault().initialize();
        OverlayManager.getDefault().initialize();
        AnnotationManager.getDefault().initialize();
        UpdaterManager.getDefault().initialize();
        LoggerManager.getDefault();

        initializeUserFile();

        //restore();
    }

    protected static void initializeLocalFolder() {
        FileUtils.LocalFolder();
        XMLUtils.createXMLDocument(FileUtils.DataProvider());
        FileUtils.LogFolder(); 
        FileUtils.LogFile();
        FileUtils.ErrorFile();
        FileUtils.SettingsFolder();
        FileUtils.SaveFolder();
        XMLUtils.createXMLDocument(FileUtils.UserFile());
        XMLUtils.createXMLDocument(FileUtils.MainFrame());
    }

    protected static void initializeUserFile() {
        for (Object obj : UpdaterManager.getDefault().getUpdaters()) {
            XMLUtils.createXMLDocument(FileUtils.SaveFile((String) obj));
        }
    }

    //public static void restore() { RestoreSettings.newInstance().restore(); }
    //public static void save() { ChartFrameManager.getDefault().saveAll(); }

}
