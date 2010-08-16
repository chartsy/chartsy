package org.chartsy.main;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.chartsy.main.features.FeaturesPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorelgheba
 */
public final class Register extends CallableSystemAction
{

	public @Override void performAction()
	{
        if (!isRegistred())
		{
            RegisterForm register = new RegisterForm(new javax.swing.JFrame(), true);
            register.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
            register.setVisible(true);
        } 
		else
		{
            Confirmation descriptor = new NotifyDescriptor.Confirmation(
				NbBundle.getMessage(Register.class, "Unregister.confirmationMsg", getUsername()),
				"Unregister",
				NotifyDescriptor.YES_NO_OPTION);
            Object retval = DialogDisplayer.getDefault().notify(descriptor);
			if (retval.equals(NotifyDescriptor.YES_OPTION))
			{
				clearRegistration();
				FeaturesPanel.getDefault().refresh();
			}
        }
	}

	public @Override String getName()
	{
		return isRegistred() ? "Unregister" : "Register";
	}

	public @Override HelpCtx getHelpCtx()
	{
		return HelpCtx.findHelp(Register.class);
	}

	protected @Override void initialize()
	{
		super.initialize();
		putValue("noIconInMenu", Boolean.FALSE);
	}

	protected @Override boolean asynchronous()
	{
		return false;
	}

	private boolean isRegistred()
	{
		Preferences preferences
			= NbPreferences.root().node("/org/chartsy/register");
		return preferences.getBoolean("registred", false);
	}

	private String getUsername()
	{
		Preferences preferences
			= NbPreferences.root().node("/org/chartsy/register");
		return preferences.get("username", "");
	}

	private void clearRegistration()
	{
		Preferences preferences
			= NbPreferences.root().node("/org/chartsy/register");
		Preferences stockScan = NbPreferences.root().node("/org/chartsy/stockscanpro");
		try
		{
			preferences.clear();
			stockScan.clear();
		}
		catch (BackingStoreException ex)
		{
			System.err.print(ex.getMessage());
		}
	}

}
