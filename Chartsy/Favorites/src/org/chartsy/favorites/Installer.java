package org.chartsy.favorites;

import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall
{

	@Override
	public void restored()
	{
		/*SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				FavoritesComponent favoritesComponent = FavoritesComponent.findInstance();
				if (!favoritesComponent.isOpened())
				{
					favoritesComponent.open();
					favoritesComponent.requestActive();
				}
			}
		});*/
	}
}
