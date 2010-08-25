package org.chartsy.stockscanpro.ui;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 *
 * @author Viorel
 */
public class QueryPanel extends JPanel
{

	private Content content;
	private StockScanToolbar stockScanToolbar;
	private ExchangesPanel exchangesPanel;
	private ScanTitlePanel scanTitlePanel;
	private ScanQueryPanel scanQueryPanel;
	private RunScanPanel runScanPanel;

    public QueryPanel(Content panel)
    {
		super(new SpringLayout());
		setOpaque(false);

		content = panel;
		initComponents();
    }

	private void initComponents()
	{
		stockScanToolbar = new StockScanToolbar(this);
		exchangesPanel = new ExchangesPanel();
		scanTitlePanel = new ScanTitlePanel();
		scanQueryPanel = new ScanQueryPanel();
		runScanPanel = new RunScanPanel(this);

		add(stockScanToolbar);
		add(exchangesPanel);
		add(scanTitlePanel);
		add(scanQueryPanel);
		add(runScanPanel);

		SpringUtilities.makeCompactGrid(this,
			5, 1,	// rows, cols
			0, 0, // initialX, initialY
			0, 0);// xPad, yPad
	}

	public Content getContentPanel()
	{
		return content;
	}

	public void setScan(String scan)
	{
		scanQueryPanel.setQuery(scan);
	}

	public String getScan()
	{
		return scanQueryPanel.getQuery();
	}

	public void setScanTitle(String title)
	{
		scanTitlePanel.setScanTitle(title);
	}

	public String getScanTitle()
	{
		return scanTitlePanel.getScanTitle();
	}

	public Object[] getSelectedExchanges()
	{
		return exchangesPanel.getSelectedExchanges();
	}

	public String getDate()
	{
		return stockScanToolbar.getDate();
	}

}
