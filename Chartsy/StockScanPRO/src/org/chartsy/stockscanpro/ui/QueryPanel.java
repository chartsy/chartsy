package org.chartsy.stockscanpro.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.stockscanpro.actions.LoadScansAction;
import org.chartsy.stockscanpro.actions.SaveScansAction;
import org.chartsy.stockscanpro.ui.JCheckList.CheckableItem;
import org.jdesktop.swingx.JXDatePicker;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.Utilities;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Viorel
 */
public class QueryPanel extends JPanel
{

    /* main panels */
    private JPanel buttonContainer;
    private JPanel exchangeContainer;
    private JPanel formContainer;

    /* button container elements */
    private JButton loadScan;
    private JButton saveScan;
    private JXDatePicker datePicker;

    /* exchange container elements */
    private JLabel exchangeLbl;
    private JButton exchangeBtn;
    private ExchangePopup exchangePopup;

    /* form container elements */
	private JLabel scanTitleLbl;
    private JTextField scanTitle;
	private JLabel queryLbl;
    private JEditorPane editorPane;
    private JButton runScan;

    public QueryPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());

        buttonContainer = new JPanel();
        initButtonContainerUI();

        exchangeContainer = new JPanel();
        initExchangeContainerUI();

        formContainer = new JPanel();
        initFormContainerUI();

        add(buttonContainer);
        add(exchangeContainer);
        add(formContainer);
    }

    private void initButtonContainerUI()
    {
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.setOpaque(false);

        // load scan button
        loadScan = new JButton(NbBundle.getMessage(QueryPanel.class, "LoadScan_Btn"));
		loadScan.addActionListener(new LoadScansAction());
        buttonContainer.add(loadScan);

        // save scan button
        saveScan = new JButton(NbBundle.getMessage(QueryPanel.class, "SaveScan_Btn"));
		saveScan.addActionListener(new SaveScansAction());
        buttonContainer.add(saveScan);

        // date picker
        datePicker = new JXDatePicker(Calendar.getInstance().getTimeInMillis());
        datePicker.setFormats(new SimpleDateFormat[]
        {
            new SimpleDateFormat("yyyy-MM-dd")
        });
        datePicker.getEditor().setColumns(2);
        buttonContainer.add(datePicker);
    }

    private void initExchangeContainerUI()
    {
        exchangeContainer.setLayout(new BoxLayout(exchangeContainer, BoxLayout.X_AXIS));
        exchangeContainer.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        exchangeContainer.setOpaque(false);

        // exchange popup selector
        exchangePopup = new ExchangePopup();

        // exchange label
        exchangeLbl = new JLabel(NbBundle.getMessage(QueryPanel.class, "Exchange_Lbl", exchangePopup.getSelectedExchanges()));
        exchangeLbl.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        exchangeLbl.setOpaque(false);
		exchangeLbl.setFont(getExtraBoldFont(exchangeLbl.getFont()));
        exchangePopup.setExchangeListener(exchangeLbl);
        exchangeContainer.add(exchangeLbl);

        // exchange button
        exchangeBtn = new JButton(NbBundle.getMessage(QueryPanel.class, "Exchange_Btn"));
        exchangeBtn.setOpaque(false);
        exchangeBtn.setBorder(BorderFactory.createEmptyBorder(1, 11, 1, 1));
        exchangeBtn.setMargin(new Insets(0, 0, 0, 0));
        exchangeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exchangeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        exchangeBtn.setBorderPainted(false);
        exchangeBtn.setFocusPainted(false);
        exchangeBtn.setContentAreaFilled(false);
        exchangeBtn.setRolloverEnabled(true);
        exchangeBtn.setFocusable(true);
        exchangeBtn.setFont(getExtraBoldFont(exchangeBtn.getFont()));
        exchangeBtn.setForeground(new Color(0x203673));
        exchangeBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                exchangePopup.show(exchangeBtn, 11, exchangeBtn.getHeight());
            }
        });
        exchangeBtn.addMouseListener(new MouseListener()
        {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e)
            {
                exchangeBtn.setForeground(new Color(0x284693));
				exchangeBtn.setFont(getUnderlineFont(exchangeBtn.getFont()));
            }
            public void mouseExited(MouseEvent e)
            {
                exchangeBtn.setForeground(new Color(0x203673));
                exchangeBtn.setFont(getNonUnderlineFont(exchangeBtn.getFont()));
            }
        });
        exchangeContainer.add(exchangeBtn);
    }

    private void initFormContainerUI()
    {
        // scan title label
		scanTitleLbl = new JLabel(NbBundle.getMessage(QueryPanel.class, "ScanTitle_Lbl"));
        scanTitleLbl.setHorizontalTextPosition(SwingConstants.LEFT);
        scanTitleLbl.setOpaque(false);
		formContainer.add(scanTitleLbl);

        // scan title text field
        scanTitle = new JTextField(NbBundle.getMessage(QueryPanel.class, "ScanTitle_Txt"));
		scanTitle.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				if (scanTitle.getText().equals(NbBundle.getMessage(QueryPanel.class, "ScanTitle_Txt")))
					scanTitle.setText("");
			}
			public void focusLost(FocusEvent e)
			{
				if (scanTitle.getText().equals(""))
					scanTitle.setText(NbBundle.getMessage(QueryPanel.class, "ScanTitle_Txt"));
			}
		});
        formContainer.add(scanTitle);

        // separator

		JLabel label;
        formContainer.add(label = new JLabel(" "));
        label.setHorizontalTextPosition(SwingConstants.LEFT);
        label.setOpaque(false);

        // query label
		queryLbl = new JLabel(NbBundle.getMessage(QueryPanel.class, "Query_Lbl"));
		queryLbl.setHorizontalTextPosition(SwingConstants.LEFT);
        queryLbl.setOpaque(false);
        formContainer.add(queryLbl);

        // query text area
		editorPane = new JEditorPane();
		editorPane.setContentType("text/x-scan");
		EditorUI editorUI = Utilities.getEditorUI(editorPane);
		JComponent mainComp = null;
		if (editorUI != null)
			mainComp = editorUI.getExtComponent();
		if (mainComp == null)
			mainComp = new javax.swing.JScrollPane(editorPane);
		mainComp.setPreferredSize(new Dimension(520, 200));
		formContainer.add(mainComp);

        // run scan button
        runScan = new JButton(NbBundle.getMessage(QueryPanel.class, "RunScan_Btn"));
		runScan.addActionListener(new RunScanAction());
        formContainer.add(runScan);

		formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
    }

	private Font getExtraBoldFont(Font font)
	{
		Map attr = font.getAttributes();
		attr.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD);
		return font.deriveFont(attr);
	}

	private Font getUnderlineFont(Font font)
	{
		Map attr = font.getAttributes();
		attr.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		return font.deriveFont(attr);
	}

	private Font getNonUnderlineFont(Font font)
	{
		Map attr = font.getAttributes();
		attr.put(TextAttribute.UNDERLINE, -1);
		return font.deriveFont(attr);
	}

	public void setScan(String scan)
	{
		editorPane.setText(scan);
	}

	public String getScan()
	{
		return editorPane.getText();
	}

	public void setScanTitle(String title)
	{
		scanTitle.setText(title);
	}

	public String getScanTitle()
	{
		return scanTitle.getText();
	}

	private class RunScanAction implements ActionListener
	{

		private final RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
		private String responce;

		public void actionPerformed(ActionEvent e)
		{
			Object[] selectedItems = exchangePopup.getSelectedItems();
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
			final String date = (new SimpleDateFormat("yyyy-MM-dd")).format(datePicker.getDate());
			final String scanExpresion = generateQuery(date, exchange);
			final String generatedQuery = generateQuery(date, exchange);
			final String initialQuery = editorPane.getText();
			final String scanTtl = scanTitle.getText();

			final Preferences preferences = NbPreferences.root().node("/org/chartsy/register");

			final RequestProcessor.Task task = RP.create(new Runnable()
			{
				public void run()
				{
					HttpClient client = ProxyManager.getDefault().getHttpClient();
					PostMethod method = new PostMethod(NbBundle.getMessage(SaveScansAction.class, "StockScanPRO_URL"));

					try
					{
						method.setQueryString(new NameValuePair[]
						{
							new NameValuePair("option", "com_chartsy"),
							new NameValuePair("view", "scanresults"),
							new NameValuePair("format", "raw"),
							new NameValuePair("username", preferences.get("username", "")),
							new NameValuePair("passwd", preferences.get("password", ""))
						});

						method.setRequestBody(new NameValuePair[]
						{
							new NameValuePair("date", date),
							new NameValuePair("generatedQuery", generatedQuery),
							new NameValuePair("initialQuery", initialQuery),
							new NameValuePair("resultsType", "csv_export"),
							new NameValuePair("scanExpression", scanExpresion),
							new NameValuePair("scanTitle", scanTtl)
						});

						client.executeMethod(method);
						responce = method.getResponseBodyAsString();
						method.releaseConnection();
					}
					catch (IOException ex)
					{
						Exceptions.printStackTrace(ex);
					}
				}
			});

			final ProgressHandle handle = ProgressHandleFactory.createHandle(NbBundle.getMessage(QueryPanel.class, "Scaning_Lbl"), task);
			task.addTaskListener(new TaskListener()
			{
				public void taskFinished(Task task)
				{
					handle.finish();
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							ScanResults results = new ScanResults();
							results.setTitle(exchange);
							results.setResponce(responce);
							results.open();
							results.requestActive();
						}
					});
				}
			});

			handle.start();
			task.schedule(0);
		}

		public String generateQuery(String date, String exchange)
		{
			String query = editorPane.getText();

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
