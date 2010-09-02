package org.chartsy.welcome.content;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class MenuLink extends JPanel
	implements Constants, MouseListener
{

	private String url;

	public static MenuLink createMenuLink(String key)
	{
		return new MenuLink(key);
	}

	public MenuLink(String key)
	{
		super(new BorderLayout());
		Icon image = ImageUtilities.loadImageIcon(
			NbBundle.getMessage(MenuLink.class, "IMG_"+key), true);
		JLabel label = new JLabel(image);
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		label.setOpaque(false);
		label.addMouseListener((MouseListener)this);
		setOpaque(false);
		add(label, BorderLayout.CENTER);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.url = NbBundle.getMessage(MenuLink.class, "URL_"+key);
	}

	@Override public void mouseClicked(MouseEvent e)
	{
		try { DesktopUtil.browse(url); }
		catch (Exception ex) {Exceptions.printStackTrace(ex); }
	}

	@Override public void mousePressed(MouseEvent e)
	{}

	@Override public void mouseReleased(MouseEvent e)
	{}

	@Override public void mouseEntered(MouseEvent e)
	{
		StatusDisplayer.getDefault().setStatusText(url);
	}

	@Override public void mouseExited(MouseEvent e)
	{
		StatusDisplayer.getDefault().setStatusText("");
	}

}
