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
import org.apache.commons.httpclient.NameValuePair;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.stockscanpro.ui.QueryPanel;
import org.chartsy.stockscanpro.ui.ScanLoaderPanel;
import org.chartsy.stockscanpro.ui.StockScanToolbar;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
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
			Container container = btn.getParent();
			if (container instanceof StockScanToolbar)
				queryPanel = ((StockScanToolbar) container).getQueryPanel();
			else
				queryPanel = null;
		}

		if (queryPanel != null)
		{
			ScanLoaderPanel panel = new ScanLoaderPanel(queryPanel);
			DialogDescriptor descriptor 
				= new DialogDescriptor(panel, "Load Scan", true, null);
			descriptor.setMessageType(DialogDescriptor.PLAIN_MESSAGE);
			descriptor.setOptions(new Object[]
			{
				DialogDescriptor.OK_OPTION,
				DialogDescriptor.CANCEL_OPTION
			});
			panel.addNotify();
			Object ret = DialogDisplayer.getDefault().notify(descriptor);
			if (ret.equals(DialogDescriptor.OK_OPTION))
			{
				panel.loadScan();
				panel.removeNotify();
			}
			else
			{
				panel.removeNotify();
			}
		}
	}

	public static Node getRootNode()
	{
		boolean copied = false;
		Preferences preferences = NbPreferences.root().node("/org/chartsy/register");

		try
		{
			FileObject dest = FileUtil.createData(FileUtils.stockScanFile("loadScans.xml"));
			NameValuePair[] query = new NameValuePair[]
			{
				new NameValuePair("option", "com_chartsy"),
				new NameValuePair("view", "loadscans"),
				new NameValuePair("format", "raw"),
				new NameValuePair("username", preferences.get("username", "")),
				new NameValuePair("passwd", preferences.get("password", ""))
			};

			InputStream inputStream = ProxyManager.getDefault().inputStreamPOST(
				NbBundle.getMessage(LoadScansAction.class, "StockScanPRO_URL"),
				query);
			OutputStream outputStream = dest.getOutputStream();
			FileUtil.copy(inputStream, outputStream);
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
				URL fileURL = FileUtils.stockScanFile("loadScans.xml").toURL();

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
