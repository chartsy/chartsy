package org.chartsy.main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.chartsy.main.intro.WelcomeTopComponent;
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
            WelcomeTopComponent.findInstance().requestActive();
            init();
        }
    };

    private static void init() {
        InitializeApplication.initialize();
        Preferences p = NbPreferences.root().node("/org/chartsy/register");
        boolean registred = Boolean.parseBoolean(p.get("registred", "false"));
        if (!registred) {
            RegisterForm register = new RegisterForm(new JFrame(), true);
            register.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            register.setVisible(true);
        }
    }

    public void restored() {
        super.restored();
        addKeystore();
        addPrintProperties();
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
                    if (getSrcFile().exists()) FileUtils.copyFile(getSrcFile(), getDestFile());
                } catch (IOException ex) {
                    LoggerManager.getDefault().log(ex);
                }
            }
        });
        t.start();
    }

    private void addPrintProperties() {
        if (!(new File(new File(new File(new File(new File(new File(System.getProperty("netbeans.user"), "config"), "Preferences"), "org"), "netbeans"), "modules"), "autoupdate.properties").exists())) {
            Preferences p = NbPreferences.root().node("/org/netbeans/modules/print");
            p.put("print.area.height", "697.8897637795276");
            p.put("print.area.width", "451.2755905511811");
            p.put("print.area.x", "72.0");
            p.put("print.area.y", "72.0");
            p.put("print.border", "false");
            p.put("print.border.color", "0,0,0");
            p.put("print.footer", "false");
            p.put("print.footer.center", "");
            p.put("print.footer.color", "0,0,0");
            p.put("print.footer.font", "Serif,0,10");
            p.put("print.footer.left", "%ROW%.%COLUMN% of %COUNT%");
            p.put("print.footer.right", "%MODIFIED_DATE%  %MODIFIED_TIME%");
            p.put("print.header", "false");
            p.put("print.header.center", "");
            p.put("print.header.color", "0,0,0");
            p.put("print.header.font", "Serif,0,10");
            p.put("print.header.left", "%NAME%");
            p.put("print.header.right", "");
            p.put("print.page.orientation", "0.0");
            p.put("print.paper.height", "841.8897637795276");
            p.put("print.paper.width", "595.275590551181");
            p.put("print.text.as.editor", "false");
            p.put("print.text.background.color", "255,250,255");
            p.put("print.text.color", "0,0,0");
            p.put("print.text.font", "Monospaced,0,10");
            p.put("print.text.line.numbers", "false");
            p.put("print.text.line.spacing", "1.0");
            p.put("print.text.selection", "false");
            p.put("print.text.use.color", "true");
            p.put("print.text.use.font", "true");
            p.put("print.text.wrap.lines", "false");
            p.put("print.zoom", "0.0");
        }
    }

}
