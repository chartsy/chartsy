package org.chartsy.main.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@OptionsPanelController.TopLevelRegistration(categoryName = "#OptionsCategory_Name_Twitter",
iconBase = "org/chartsy/main/options/twitter.png",
keywords = "#OptionsCategory_Keywords_Twitter",
keywordsCategory = "Twitter")
public final class TwitterOptionsPanelController extends OptionsPanelController {

    private TwitterPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    public void update() {
        getPanel().load();
        changed = false;
    }

    public void applyChanges() {
        getPanel().store();
        if (changed) {
            Confirmation descriptor = new DialogDescriptor.Confirmation(NbBundle.getMessage(getClass(), "restart.msg"), NbBundle.getMessage(getClass(), "restart.title"));
            descriptor.setMessageType(DialogDescriptor.QUESTION_MESSAGE);
            descriptor.setOptions(new Object[]
            {
                DialogDescriptor.YES_OPTION,
                DialogDescriptor.NO_OPTION
            });

            Object ret = DialogDisplayer.getDefault().notify(descriptor);
            if (ret.equals(DialogDescriptor.YES_OPTION))
            {
                LifecycleManager.getDefault().markForRestart();
                LifecycleManager.getDefault().exit();
            }
        }
        changed = false;
    }

    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    public boolean isValid() {
        return getPanel().valid();
    }

    public boolean isChanged() {
        return changed;
    }

    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private TwitterPanel getPanel() {
        if (panel == null) {
            panel = new TwitterPanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
