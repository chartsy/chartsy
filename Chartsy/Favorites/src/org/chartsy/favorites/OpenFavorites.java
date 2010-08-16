package org.chartsy.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public final class OpenFavorites implements ActionListener
{

	public void actionPerformed(ActionEvent e)
	{
		TopComponent component = WindowManager.getDefault().findTopComponent("FavoritesComponent");
		if (component instanceof FavoritesComponent)
		{
			FavoritesComponent favoritesComponent
				= (FavoritesComponent) component;
			if (!favoritesComponent.isOpened())
			{
				Mode mode = WindowManager.getDefault().findMode("explorer");
				if (mode != null)
				{
					mode.dockInto(favoritesComponent);
					favoritesComponent.open();
				}
			}
			else
			{
				favoritesComponent.requestActive();
			}
		}
	}
}
