package org.chartsy.favorites;

import java.awt.Rectangle;
import org.openide.modules.ModuleInstall;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall
{

	@Override
	public void restored()
	{
		WindowManager.getDefault().invokeWhenUIReady(new Runnable()
		{
			public void run()
			{
				FavoritesComponent component = FavoritesComponent.findInstance();
				Mode mode = WindowManager.getDefault().findMode("explorer");
				if (mode != null)
				{
					mode.setBounds(new Rectangle(168, 242, 200, 373));
					mode.dockInto(component);
					component.open();
				}
				else
				{
					System.out.println("Mode not found.");
				}
			}
		});
	}
}
