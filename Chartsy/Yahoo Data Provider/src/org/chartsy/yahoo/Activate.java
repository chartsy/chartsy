package org.chartsy.yahoo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import org.chartsy.main.chartsy.chart.ButtonGroupHelper;
import org.chartsy.main.managers.UpdaterManager;
import org.chartsy.main.updater.AbstractUpdater;
import org.openide.util.actions.Presenter;

/**
 *
 * @author viorel.gheba
 */
public class Activate implements ActionListener, Presenter.Menu {

    public void actionPerformed(ActionEvent e) {
        /*AbstractUpdater updater = UpdaterManager.getDefault().getActiveUpdater();
        if (updater != null) {
            if (!updater.getName().equals(UpdaterKeys.UPDATER_NAME)) {
                UpdaterManager.getDefault().setActiveUpdater(UpdaterKeys.UPDATER_NAME);
            } else {
                NotifyDescriptor nd = new NotifyDescriptor.Message("This data provider is already active.", NotifyDescriptor.INFORMATION_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
            }
        } else {
            UpdaterManager.getDefault().setActiveUpdater(UpdaterKeys.UPDATER_NAME);
        }*/
    }

    public JMenuItem getMenuPresenter() {
        final AbstractUpdater updater = UpdaterManager.getDefault().getActiveUpdater();
        JCheckBoxMenuItem abc = new JCheckBoxMenuItem("Yahoo", null);
        ButtonGroup local = ButtonGroupHelper.returnGroup();
        local.add(abc);
        if (updater != null) {
            if (!updater.getName().equals(UpdaterKeys.UPDATER_NAME)) abc.setSelected(false);
            else abc.setSelected(true);
        }
        abc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //StatusDisplayer.getDefault().setStatusText("Marge chosen");
                AbstractUpdater updater = UpdaterManager.getDefault().getActiveUpdater();
                if (updater != null) {
                    if (!updater.getName().equals(UpdaterKeys.UPDATER_NAME)) {
                        UpdaterManager.getDefault().setActiveUpdater(UpdaterKeys.UPDATER_NAME);
                    }
                } else {
                    UpdaterManager.getDefault().setActiveUpdater(UpdaterKeys.UPDATER_NAME);
                }
            }
        });
        return abc;
    }

}
