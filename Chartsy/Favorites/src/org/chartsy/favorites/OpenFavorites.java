package org.chartsy.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class OpenFavorites implements ActionListener
{

	public void actionPerformed(ActionEvent e)
	{
		FavoritesComponent component = FavoritesComponent.findInstance();
		component.open();
		component.requestActive();
	}
}
