package org.chartsy.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorelgheba
 */
public final class Register implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        Preferences p = NbPreferences.root().node("/org/chartsy/register");
        boolean registred = Boolean.parseBoolean(p.get("registred", "false"));
        if (!registred) {
            RegisterDialog register = new RegisterDialog(new javax.swing.JFrame(), true);
            register.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            register.setVisible(true);
        } else {
            NotifyDescriptor d = new NotifyDescriptor.Message("The application is already registred.", NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
    }

}
