package org.chartsy.welcome.content;

import java.awt.Color;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class WebLink extends LinkButton {

	private String url;

	public static WebLink createWebLink(String key)										{ return new WebLink(key, false); }
	public static WebLink createWebLink(String key, boolean showBullet)					{ return new WebLink(key, showBullet); }
	public static WebLink createWebLink(String label, String url)						{ return new WebLink(label, url, false); }
	public static WebLink createWebLink(String label, String url, boolean showBullet)	{ return new WebLink(label, url, showBullet); }

	public WebLink(String key, boolean showBullet)
	{
		super(NbBundle.getMessage(WebLink.class, "LBL_"+key),showBullet);
		this.url = NbBundle.getMessage(WebLink.class, "URL_"+key);
	}

	public WebLink(String label, String url, boolean showBullet)
	{
		super(label, showBullet);
		this.url = url;
	}

	public WebLink(String key, boolean showBullet, Color foreground)
	{
		super(NbBundle.getMessage(WebLink.class, "LBL_"+key), 
			showBullet,
			foreground);
		this.url = NbBundle.getMessage(WebLink.class, "URL_"+key);
	}

	public WebLink(String label, String url, boolean showBullet, Color foreground)
	{
		super(label, showBullet, foreground);
		this.url = url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	@Override protected void onActionPerformed()
	{
		try { DesktopUtil.browse(url); }
		catch (Exception ex) { Exceptions.printStackTrace(ex); }
	}

	@Override protected void onMouseEntered()
	{
		setForeground(LINK_HOVER_COLOR);
		StatusDisplayer.getDefault().setStatusText(url);
	}

	@Override protected void onMouseExited()
	{
		setForeground(LINK_COLOR);
		StatusDisplayer.getDefault().setStatusText("");
	}

}
