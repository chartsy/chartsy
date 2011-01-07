package org.chartsy.chatsy;

import org.openide.modules.ModuleInstall;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall
{

	@Override public void restored()
	{
		WindowManager.getDefault().invokeWhenUIReady(new Runnable()
		{
			@Override public void run()
			{
				NbPreferences.root().node("/org/chartsy/chat").putBoolean("loggedin", false);
			}
		});
	}
}
