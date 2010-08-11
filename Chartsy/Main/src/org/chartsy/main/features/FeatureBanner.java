package org.chartsy.main.features;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 *
 * @author Viorel
 */
public class FeatureBanner extends JButton
{

	public FeatureBanner(Action action)
	{
		super(action);
		setText("");
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setHorizontalAlignment(SwingConstants.CENTER);
		setFocusPainted(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setMargin(new Insets(0, 0, 0, 0));
		ImageIcon icon = (ImageIcon) action.getValue(Action.SMALL_ICON);
		setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
	}

}
