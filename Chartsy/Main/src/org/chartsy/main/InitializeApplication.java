package org.chartsy.main;

import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.IndicatorManager;
import org.chartsy.main.managers.LoggerManager;
import org.chartsy.main.managers.OverlayManager;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.main.utils.XMLUtils;
import org.openide.windows.WindowManager;


/**
 *
 * @author viorel.gheba
 */
public class InitializeApplication {

    private InitializeApplication() {}

    public static void initialize() {
        initializeLocalFolder();

        ChartManager.getDefault().initialize();
        IndicatorManager.getDefault().initialize();
        OverlayManager.getDefault().initialize();
        AnnotationManager.getDefault().initialize();
        UpdaterManager.getDefault().initialize();
        LoggerManager.getDefault();

        if (!XMLUtils.isRegistred()) {
            RegisterDialog register = new RegisterDialog(new javax.swing.JFrame(), true);
            register.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            register.setVisible(true);
        }
    }

    protected static void initializeLocalFolder() {
        FileUtils.LocalFolder();
        FileUtils.LogFolder(); 
        FileUtils.LogFile();
        FileUtils.ErrorFile();
        FileUtils.SettingsFolder();
        XMLUtils.createXMLDocument(FileUtils.UserFile());
        XMLUtils.createXMLDocument(FileUtils.RegisterFile());
    }

}
