package org.chartsy.welcome.content;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public class Logo extends JPanel 
	implements Constants, MouseListener
{

	private String url;
	private JLabel label;
	private ImageIcon icon;

	public static Logo createLogoLink(String img, String url)
	{
		return new Logo(img, url);
	}

	public static Logo createChartsyLogo()
	{
		Logo logo = new Logo(CHARTSY_ICON, CHARTSY_URL);
		logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
		return logo;
	}

	public Logo(String img, String url)
	{
		super(new BorderLayout());
		icon = ImageUtilities.loadImageIcon(img, true);
		label = new JLabel(icon);
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		label.setOpaque(false);
		label.addMouseListener((MouseListener)this);
		addMouseListener((MouseListener)this);
		setOpaque(false);
		add(label, BorderLayout.CENTER);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.url = url;
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
