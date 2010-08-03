package org.chartsy.stockscanpro.actions;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.stockscanpro.ui.QueryPanel;
import org.chartsy.stockscanpro.ui.ScanLoader;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.XMLFileSystem;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author Viorel
 */
public class LoadScansAction implements ActionListener
{

	private static final Logger LOG = Logger.getLogger(LoadScansAction.class.getPackage().getName());

	public LoadScansAction()
	{}

	public void actionPerformed(ActionEvent e)
	{
		QueryPanel queryPanel = null;
		Object obj = e.getSource();
		if (obj instanceof JButton)
		{
			JButton btn = (JButton) obj;
			Container container = btn.getParent().getParent();
			if (container instanceof QueryPanel)
				queryPanel = (QueryPanel) container;
			else
				queryPanel = null;
		}

		if (queryPanel != null)
		{
			ScanLoader explorer = ScanLoader.findInstance();
			explorer.setQueryPanel(queryPanel);
			explorer.open();
			explorer.requestActive();
		}
	}

	public static Node getRootNode()
	{
		boolean copied = false;
		Preferences preferences = NbPreferences.root().node("/org/chartsy/register");

		try
		{
			FileObject dest = FileUtil.createData(FileUtils.stockScanFile("loadScans.xml"));

			HttpClient client = ProxyManager.getDefault().getHttpClient();
			HttpMethod method = new GetMethod(NbBundle.getMessage(LoadScansAction.class, "StockScanPRO_URL"));

			method.setQueryString(new NameValuePair[]
			{
				new NameValuePair("option", "com_chartsy"),
				new NameValuePair("view", "loadscans"),
				new NameValuePair("format", "raw"),
				new NameValuePair("username", preferences.get("username", "")),
				new NameValuePair("passwd", preferences.get("password", ""))
			});

			client.executeMethod(method);

			InputStream inputStream = method.getResponseBodyAsStream();
			OutputStream outputStream = dest.getOutputStream();
			FileUtil.copy(inputStream, outputStream);
			method.releaseConnection();
			inputStream.close();
			outputStream.close();
			copied = true;
		}
		catch (IOException ex)
		{
			copied = false;
			LOG.log(Level.SEVERE, "", ex);
		}

		if (copied)
		{
			try
			{
				URL fileURL = new URL("file:/" + FileUtils.stockScanFile("loadScans.xml").getAbsolutePath());

				XMLFileSystem fileSystem = new XMLFileSystem();
				fileSystem.setXmlUrl(fileURL);
				FileObject fileObject = fileSystem.findResource("StockScanFolders");
				DataFolder dataFolder = DataFolder.findFolder(fileObject);
				return dataFolder.getNodeDelegate();
			}
			catch (Exception ex)
			{
				LOG.log(Level.SEVERE, "", ex);
			}
		}

		return null;
	}

}
