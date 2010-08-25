package org.chartsy.main.features;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JToolBar;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.DesktopUtil;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;

/**
 *
 * @author Viorel
 */
public class FeaturesPanel extends JToolBar
{

	private static final Logger LOG
		= Logger.getLogger(FeaturesPanel.class.getPackage().getName());
	private static FeaturesPanel instance;

	private FeatureBanner chartsyBanner;
	private JToolBar.Separator sep1;
	private FeatureBanner mrSwingBanner;
	private JToolBar.Separator sep2;
	private FeatureBanner stockScanPROBanner;
	private List<String> usedCookies = new ArrayList<String>();

	public static FeaturesPanel getDefault()
	{
		if (instance == null)
			instance = new FeaturesPanel();
		return instance;
	}

	private FeaturesPanel()
	{
		super("Features Toolbar", JToolBar.HORIZONTAL);
		initComponents();
	}

	private void initComponents()
	{
		Preferences chartsy = NbPreferences.root().node("/org/chartsy/register");
		Preferences stockScan = NbPreferences.root().node("/org/chartsy/stockscanpro");
		
		usedCookies.clear();
        usedCookies.add("PHPSESSID");
        usedCookies.add("amember_nr");

		chartsyBanner = new FeatureBanner(new ChartsyAction());
		mrSwingBanner = new FeatureBanner(new MrSwingAction());
		stockScanPROBanner = new FeatureBanner(new StockScanPROAction());
		
		sep1 = new JToolBar.Separator();
		sep2 = new JToolBar.Separator();

		if (!isChartsyUser())
		{
			add(chartsyBanner);
		}
		else
		{
			chartsy.putBoolean("mrswingregistred", checkMrSwingRegistration());
			stockScan.putBoolean("stockscanproregistred", checkStockScanPRORegistration() != 0);
		}

		if (!isStockScanPROUser())
		{
			if (!isMrSwingUser())
			{
				add(sep1);
				add(mrSwingBanner);
			}
			
			add(sep2);
			add(stockScanPROBanner);
		} else {
			removeAll();
		}
	}

	public void refresh()
	{
		removeAll();
		initComponents();
		validate();
		repaint();
	}

	private boolean isChartsyUser()
	{
		Preferences preferences = NbPreferences.root().node("/org/chartsy/register");
		return preferences.getBoolean("registred", false);
	}

	private boolean isMrSwingUser()
	{
		Preferences preferences = NbPreferences.root().node("/org/chartsy/register");
		return preferences.getBoolean("mrswingregistred", false);
	}

	private boolean isStockScanPROUser()
	{
		Preferences preferences = NbPreferences.root().node("/org/chartsy/stockscanpro");
		return preferences.getBoolean("stockscanproregistred", false);
	}

	public void hideBanners()
	{
		Preferences chartsy = NbPreferences.root().node("/org/chartsy/register");
		Preferences stockScan = NbPreferences.root().node("/org/chartsy/stockscanpro");

		boolean chartsyRegistred = chartsy.getBoolean("registred", false);
		boolean mrSwingRegistred = chartsy.getBoolean("mrswingregistred", false);
		boolean stockScanPRORegistred = stockScan.getBoolean("stockscanproregistred", false);

		if (chartsyRegistred)
		{
			hideChartsyBanner();
			sep1.setVisible(false);
		}

		if (mrSwingRegistred)
		{
			hideMrSwingBanner();
			sep2.setVisible(false);
		}

		if (stockScanPRORegistred)
		{
			hideStockScanPROBanner();
			sep2.setVisible(false);
		}
	}

	public void hideChartsyBanner()
	{
		chartsyBanner.setVisible(false);
	}

	public void hideMrSwingBanner()
	{
		mrSwingBanner.setVisible(false);
	}

	public void hideStockScanPROBanner()
	{
		stockScanPROBanner.setVisible(false);
	}

	private boolean checkMrSwingRegistration()
	{
		String url = "http://www.mrswing.com/chartsy/companyname.php?symbol=VODE.DE";
		BufferedReader in = null;
		HttpClient client = ProxyManager.getDefault().getHttpClient();
		GetMethod method = new GetMethod(url);

		method.setFollowRedirects(true);
		List<Cookie> list = getMrSwingCookies(url);
		for (Cookie cookie : list)
			client.getState().addCookie(cookie);

		try
		{
			client.executeMethod(method);
			in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));

			if (in.readLine().equals("OK"))
			{
				in.close();
				method.releaseConnection();
				return true;
			}
			else
			{
				in.close();
				method.releaseConnection();
				return false;
			}
		}
		catch (IOException ex)
		{
			LOG.log(Level.SEVERE, null, ex);
			method.releaseConnection();
			if (in != null)
			{
				try { in.close(); }
                catch (IOException io) { LOG.log(Level.SEVERE, null, io); }
			}
			return false;
		}
	}

	private List<Cookie> getMrSwingCookies(String url)
	{
		Preferences preferences = NbPreferences.root().node("/org/chartsy/register");
		List<Cookie> list = new ArrayList<Cookie>();

		String username = preferences.get("username", "");
		String password = preferences.get("password", "");

		if (username != null && password != null)
		{
			NameValuePair[] data =
            {
                new NameValuePair("amember_login", username),
                new NameValuePair("amember_pass", password)
            };

			HttpClient client = ProxyManager.getDefault().getHttpClient();
			PostMethod method = new PostMethod(url);
			method.setRequestBody(data);

			try
			{
				int responce = client.executeMethod(method);
				if (responce != HttpStatus.SC_NOT_IMPLEMENTED)
				{
					for (Cookie cookie : client.getState().getCookies())
					{
						if (usedCookies.contains(cookie.getName()))
							list.add(cookie);
					}
				}
				method.releaseConnection();
			}
			catch (IOException ex)
			{
				LOG.log(Level.SEVERE, null, ex);
			}
		}

		return list;
	}

	private int checkStockScanPRORegistration()
	{
		Preferences preferences = NbPreferences.root().node("/org/chartsy/register");
		HttpClient client = ProxyManager.getDefault().getHttpClient();
		GetMethod method = new GetMethod("http://www.stockscanpro.com/index.php");
		int id = 0;

		try
		{
			method.setQueryString(new NameValuePair[]
			{
				new NameValuePair("option", "com_chartsy"),
				new NameValuePair("view", "checkregistration"),
				new NameValuePair("format", "raw"),
				new NameValuePair("username", preferences.get("username", "")),
				new NameValuePair("passwd", preferences.get("password", ""))
			});

			client.executeMethod(method);
			id = Integer.parseInt(method.getResponseBodyAsString());
			method.releaseConnection();
		}
		catch (IOException ex)
		{
			LOG.log(Level.SEVERE, null, ex);
		}

		return id;
	}

	class ChartsyAction extends AbstractAction
	{

		public ChartsyAction()
		{
			putValue(NAME, "Please register Chartsy, it's free!");
			putValue(SHORT_DESCRIPTION, "Please register Chartsy, it's free!");
			putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/chartsy/main/features/chartsy-banner.png", true));
			putValue(LARGE_ICON_KEY, ImageUtilities.loadImageIcon("org/chartsy/main/features/chartsy-banner.png", true));
		}

		public void actionPerformed(ActionEvent e)
		{
			try { DesktopUtil.browse("http://www.chartsy.org/partners"); }
			catch (Exception ex) { Exceptions.printStackTrace(ex); }
		}

	}

	class MrSwingAction extends AbstractAction
	{

		public MrSwingAction()
		{
			putValue(NAME, "Get the MrSwing golbal EOD datafeed for only $7.79/month!");
			putValue(SHORT_DESCRIPTION, "Get the MrSwing golbal EOD datafeed for only $7.79/month!");
			putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/chartsy/main/features/mrswing-banner.png", true));
			putValue(LARGE_ICON_KEY, ImageUtilities.loadImageIcon("org/chartsy/main/features/mrswing-banner.png", true));
		}

		public void actionPerformed(ActionEvent e)
		{
			try { DesktopUtil.browse("http://www.chartsy.org/partners"); }
			catch (Exception ex) { Exceptions.printStackTrace(ex); }
		}

	}

	class StockScanPROAction extends AbstractAction
	{

		public StockScanPROAction()
		{
			putValue(NAME, "Get StockScanPRO integration for chartsy for only $9.99/month and you get the global EOD datafeed for free!");
			putValue(SHORT_DESCRIPTION, "Get StockScanPRO integration for chartsy for only $9.99/month and you get the global EOD datafeed for free!");
			putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/chartsy/main/features/stockscanpro-banner.png", true));
			putValue(LARGE_ICON_KEY, ImageUtilities.loadImageIcon("org/chartsy/main/features/stockscanpro-banner.png", true));
		}

		public void actionPerformed(ActionEvent e)
		{
			try { DesktopUtil.browse("http://www.chartsy.org/partners"); }
			catch (Exception ex) { Exceptions.printStackTrace(ex); }
		}

	}

}
