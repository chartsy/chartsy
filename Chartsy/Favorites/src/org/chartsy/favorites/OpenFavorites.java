package org.chartsy.favorites;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

public final class OpenFavorites implements ActionListener
{

	public void actionPerformed(ActionEvent e)
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
}
