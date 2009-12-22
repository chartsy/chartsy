package org.chartsy.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.chartsy.main.utils.XMLUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorelgheba
 */
public final class Register implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        if (!XMLUtils.isRegistred()) {
            RegisterDialog register = new RegisterDialog(new javax.swing.JFrame(), true);
            register.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            register.setVisible(true);
        } else {
            NotifyDescriptor d = new NotifyDescriptor.Message("The application is already registred.", NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
    }

}
