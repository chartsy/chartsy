package org.chartsy.main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;
import org.chartsy.main.intro.WelcomePage;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.ModuleInstall;
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
            //InitializeApplication.save();
            return true;
        } else {
            return false;
        }
    }

}
