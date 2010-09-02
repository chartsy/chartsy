package org.chartsy.welcome;

import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall implements Runnable
{

	@Override
	public void restored()
	{
		WindowManager.getDefault().invokeWhenUIReady(this);
	}

	@Override public void run()
	{
		Feeds.start();
	}
}
