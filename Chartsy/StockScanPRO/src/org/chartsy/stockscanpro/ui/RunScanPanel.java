package org.chartsy.stockscanpro.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.chartsy.stockscanpro.actions.SaveScansAction;
import org.chartsy.stockscanpro.ui.JCheckList.CheckableItem;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Viorel
 */
public class RunScanPanel extends JPanel
{

	private QueryPanel queryPanel;
	private JButton runBtn;

	public RunScanPanel(QueryPanel panel)
	{
		super(new FlowLayout(FlowLayout.LEADING));
		setOpaque(false);

		queryPanel = panel;
		initComponents();
	}

	private void initComponents()
	{
		runBtn = new JButton(
			NbBundle.getMessage(RunScanPanel.class, "RunScan_Btn"));
		runBtn.addActionListener(new RunScanAction());
		add(runBtn);
	}

	private class RunScanAction implements ActionListener
	{

		private final RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
		private RequestProcessor.Task task;
		private String responce;

		public void actionPerformed(ActionEvent e)
		{
			Object[] selectedItems = queryPanel.getSelectedExchanges();
			for (int i = 0; i < selectedItems.length; i++)
			{
				if (selectedItems[i] instanceof CheckableItem)
				{
					CheckableItem item = (CheckableItem) selectedItems[i];
					String exchange = item.getExchange();
					getResponce(exchange);
				}
			}
		}

		public void getResponce(final String exchange)
		{
			final String date = queryPanel.getDate();
			final String scanExpresion = generateQuery(date, exchange);
			final String generatedQuery = generateQuery(date, exchange);
			final String initialQuery = queryPanel.getScan();
			final String scanTtl = queryPanel.getScanTitle();

			final Preferences preferences = NbPreferences.root().node("/org/chartsy/register");

			final ProgressHandle handle = ProgressHandleFactory.createHandle(
				NbBundle.getMessage(QueryPanel.class, "Scaning_Lbl"),
				new Cancellable()
				{
					public boolean cancel()
					{
						if (task == null)
							return true;
						return task.cancel();
					}
				});

			final Runnable runnable = new Runnable()
			{
				public void run()
				{
					handle.start();
					handle.switchToIndeterminate();

					NameValuePair[] query = new NameValuePair[]
					{
						new NameValuePair("option", "com_chartsy"),
						new NameValuePair("view", "scanresults"),
						new NameValuePair("format", "raw"),
						new NameValuePair("username", preferences.get("username", "")),
						new NameValuePair("passwd", preferences.get("password", ""))
					};
					NameValuePair[] request = new NameValuePair[]
					{
						new NameValuePair("date", date),
						new NameValuePair("generatedQuery", generatedQuery),
						new NameValuePair("initialQuery", initialQuery),
						new NameValuePair("resultsType", "csv_export"),
						new NameValuePair("scanExpression", scanExpresion),
						new NameValuePair("scanTitle", scanTtl)
					};

					HttpClient client = getHttpClient();
					PostMethod method = new PostMethod(
						NbBundle.getMessage(SaveScansAction.class, "StockScanPRO_URL"));
					method.setQueryString(query);
					method.setRequestBody(request);

					responce = "";
					try
					{
						int status = client.executeMethod(method);
						if (status == HttpStatus.SC_OK)
						{
							InputStream is = method.getResponseBodyAsStream();
							BufferedInputStream bis = new BufferedInputStream(is);

							String datastr = null;
							StringBuilder sb = new StringBuilder();
							byte[] bytes = new byte[8192];

							int count = bis.read(bytes);
							while (count != -1 && count <= 8192)
							{
								datastr = new String(bytes, 0, count);
								sb.append(datastr);
								count = bis.read(bytes);
							}

							bis.close();
							responce = sb.toString();
						}
						method.releaseConnection();
					} catch (Exception ex)
					{
						handle.finish();
						responce = null;
					}
				}
			};

			task = RP.create(runnable);
			task.addTaskListener(new TaskListener()
			{
				public void taskFinished(Task task)
				{
					handle.finish();
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							if (responce != null)
							{
								ResultsPanel resultsPanel
									= queryPanel.getContentPanel().getResultsPanel();
								resultsPanel.addTab(
									exchange,
									scanTtl,
									getQuery(queryPanel.getScan()),
									responce);
							}
						}
					});
				}
			});

			task.schedule(0);
		}

		public String getQuery(String initial)
		{
			return initial.replace("\n", "");
		}

		public String generateQuery(String date, String exchange)
		{
			String query = queryPanel.getScan();

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("filter filter1 (");
			if (date.equals((new SimpleDateFormat("yyyy-MM-dd")).format(new Date())))
				date = "today";
			else
				date = "\"" + date + "\"";
			stringBuilder.append(date);
			stringBuilder.append(",\"");
			stringBuilder.append(exchange);
			stringBuilder.append("\"");
			stringBuilder.append("): ");
			stringBuilder.append(query);

			return stringBuilder.toString();
		}

		public HttpClient getHttpClient()
		{
			MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
			HttpConnectionManagerParams params = new HttpConnectionManagerParams();
			params.setMaxTotalConnections(100);
			manager.setParams(params);
			HttpClient client = new HttpClient(manager);
			
			Preferences corePreferences = NbPreferences.root().node("/org/netbeans/core");
			if (corePreferences.getInt(PROXY_TYPE_KEY, 1) == 2)
			{
				HostConfiguration config = client.getHostConfiguration();
				config.setProxy(
						corePreferences.get(PROXY_HTTP_HOST_KEY, ""),
						corePreferences.getInt(PROXY_HTTP_PORT_KEY, 0));
				if (corePreferences.getBoolean(PROXY_USE_AUTH_KEY, false))
				{
					Credentials credentials = new UsernamePasswordCredentials(
							corePreferences.get(PROXY_USERNAME_KEY, ""),
							corePreferences.get(PROXY_PASSWORD_KEY, ""));
					client.getState().setProxyCredentials(AuthScope.ANY, credentials);
				} else
				{
					client.getState().setProxyCredentials(AuthScope.ANY, null);
				}
			}
			return client;
		}

		private static final String PROXY_TYPE_KEY = "proxyType";
		private static final String PROXY_HTTP_HOST_KEY = "proxyHttpHost";
		private static final String PROXY_HTTP_PORT_KEY = "proxyHttpPort";
		private static final String PROXY_USE_AUTH_KEY = "useProxyAuthentication";
		private static final String PROXY_USERNAME_KEY = "proxyAuthenticationUsername";
		private static final String PROXY_PASSWORD_KEY = "proxyAuthenticationPassword";

	}

}
