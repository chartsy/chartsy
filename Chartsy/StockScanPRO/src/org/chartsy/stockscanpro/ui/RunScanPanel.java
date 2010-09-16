package org.chartsy.stockscanpro.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.commons.httpclient.NameValuePair;
import org.chartsy.main.managers.ProxyManager;
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

		private final RequestProcessor RP
			= new RequestProcessor("interruptible tasks", 1, true);
		private String responce;
		private InputStream stream;

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

			final RequestProcessor.Task task = RP.create(new Runnable()
			{
				public void run()
				{
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
					responce = ProxyManager.getDefault().inputStringPOST(
						NbBundle.getMessage(SaveScansAction.class, "StockScanPRO_URL"),
						query, request);
				}
			});

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
			task.addTaskListener(new TaskListener()
			{
				public void taskFinished(Task task)
				{
					handle.finish();
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							ResultsPanel resultsPanel
								= queryPanel.getContentPanel().getResultsPanel();
							resultsPanel.addTab(
								exchange,
								scanTtl,
								getQuery(queryPanel.getScan()),
								responce);
						}
					});
				}
			});

			handle.start();
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

	}

}
