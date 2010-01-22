package org.chartsy.main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import org.chartsy.main.intro.WelcomePage;
import org.chartsy.main.managers.LoggerManager;
import org.chartsy.main.utils.AlphaPropertyEditor;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.main.utils.PricePropertyEditor;
import org.chartsy.main.utils.StrokePropertyEditor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    private static WindowAdapter windowListener = new WindowAdapter() {
        public void windowOpened(WindowEvent e) {
            WindowManager.getDefault().getMainWindow().removeWindowListener(this);
            WelcomePage welcome = WelcomePage.getDefault();
            welcome.open();
            welcome.requestActive();
            if (welcome.isOpened()) {
                InitializeApplication.initialize();
            }
        }
    };

    public void restored() {
        super.restored();
        addKeystore();
        PropertyEditorManager.registerEditor(int.class, StrokePropertyEditor.class);
        PropertyEditorManager.registerEditor(String.class, PricePropertyEditor.class);
        PropertyEditorManager.registerEditor(int.class, AlphaPropertyEditor.class);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                WindowManager.getDefault().getMainWindow().addWindowListener(windowListener);
            }
        });
    }

    public boolean closing() {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation("Do you really want to exit the application?", "Exit", NotifyDescriptor.YES_NO_OPTION);
        Object retval = DialogDisplayer.getDefault().notify(d);
        if (retval.equals(NotifyDescriptor.YES_OPTION)) {
            return true;
        } else {
            return false;
        }
    }

    private Preferences getPreferences() {
        return NbPreferences.root().node("/org/netbeans/modules/autoupdate");
    }

    private File getChacheDirectory() {
        File cacheDir = null;
        String userDir = System.getProperty("netbeans.user");
        if (userDir != null) {
            cacheDir = new File(new File(new File(userDir, "var"), "cache"), "catalogcache");
        } else {
            File dir = FileUtil.toFile(Repository.getDefault().getDefaultFileSystem().getRoot());
            cacheDir = new File(dir, "cachecatalog");
        }
        cacheDir.mkdirs();
        return cacheDir;
    }

    private File getSrcFile() {
        return new File(new File(new File(new File(System.getProperty("netbeans.home")).getParentFile(), "chartsy"), "core"), "user.ks");
    }

    private File getDestFile() { return new File(getChacheDirectory(), "user.ks"); }

    private File getPropsFile() {
        return new File(new File(new File(new File(new File(new File(System.getProperty("netbeans.user"), "config"), "Preferences"), "org"), "netbeans"), "modules"), "autoupdate.properties");
    }

    private void addKeystore() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (!getPropsFile().exists()) {}
                try {
                    getPreferences().put("userKS", "user.ks");
                    getPreferences().put("period", "1");
                    System.out.println(System.getProperty("netbeans.home"));
                    FileUtils.copyFile(getSrcFile(), getDestFile());
                } catch (IOException ex) {
                    LoggerManager.getDefault().log(ex);
                }
            }
        });
        t.start();
    }

}
