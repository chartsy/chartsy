package org.chartsy.stockscanpro.ui;

import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.chartsy.stockscanpro.actions.LoadScansAction;
import org.chartsy.stockscanpro.actions.SaveScansAction;
import org.jdesktop.swingx.JXDatePicker;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class StockScanToolbar extends JPanel
{

	private QueryPanel queryPanel;
	private JButton loadScanBtn;
	private JButton saveScanBtn;
	private JButton emailScanBtn;
	private JXDatePicker datePicker;

	public StockScanToolbar(QueryPanel panel)
	{
		super(new FlowLayout(FlowLayout.LEADING));
		setOpaque(false);

		queryPanel = panel;
		initComponents();
	}

	private void initComponents()
	{
		loadScanBtn = new JButton(
			NbBundle.getMessage(StockScanToolbar.class, "LoadScan_Btn"));
		loadScanBtn.addActionListener(new LoadScansAction());

		saveScanBtn = new JButton(
			NbBundle.getMessage(StockScanToolbar.class, "SaveScan_Btn"));
		saveScanBtn.addActionListener(new SaveScansAction());

		//emailScanBtn = new JButton(
			//NbBundle.getMessage(StockScanToolbar.class, "EmailScan_Btn"));
		//emailScanBtn.addActionListener(new EmailScansAction());

		datePicker = new JXDatePicker(new Date());
		datePicker.setFormats(new SimpleDateFormat[]
        {
            new SimpleDateFormat("yyyy-MM-dd")
        });
		datePicker.getEditor().setColumns(10);

		add(loadScanBtn);
		add(saveScanBtn);
		//add(emailScanBtn);
		add(datePicker);
	}

	public QueryPanel getQueryPanel()
	{
		return queryPanel;
	}

	public String getDate()
	{
		return (new SimpleDateFormat("yyyy-MM-dd")).format(datePicker.getDate());
	}

}
