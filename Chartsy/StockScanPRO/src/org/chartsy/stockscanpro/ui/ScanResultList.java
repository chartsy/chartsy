package org.chartsy.stockscanpro.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Stock;
import org.chartsy.main.intervals.DailyInterval;
import org.chartsy.main.intervals.Interval;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.DataProviderManager;
import org.chartsy.main.managers.TemplateManager;
import org.chartsy.main.templates.Template;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class ScanResultList extends JList
{

	public Color rowColors[] = new Color[2];
	private boolean drawStripes = true;

	public ScanResultList()
	{
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		addMouseListener(new MouseListener() 
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2)
				{
					String item = (String) ScanResultList.this.getSelectedValue();
					String stockInfo = item.substring(0, item.indexOf(","));

					item = item.substring(item.indexOf("\"") + 1, item.length());
					String companyName = item.substring(0, item.indexOf("\""));

					String symbol = "";
					String exchange = "";

					if (stockInfo.contains("."))
					{
						symbol = stockInfo.substring(0, stockInfo.indexOf("."));
						exchange = stockInfo.substring(stockInfo.indexOf("."), stockInfo.length());
					} else
						symbol = stockInfo;

					final Stock stock = new Stock(symbol, exchange);
					stock.setCompanyName(companyName);
					final DataProvider dataProvider = DataProviderManager.getDefault().getDataProvider("MrSwing");
					final Chart chart = ChartManager.getDefault().getChart("Candle Stick");
					final Interval interval = new DailyInterval();

					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							String defaultTemplate = TemplateManager.getDefault().getDefaultTemplate();
							Template template = TemplateManager.getDefault().getTemplate(defaultTemplate);

							ChartData chartData = new ChartData();
							chartData.setStock(stock);
							chartData.setChart(chart);
							chartData.setDataProviderName(dataProvider.getName());
							chartData.setInterval(interval);

							ChartFrame chartFrame = ChartFrame.getInstance();
							chartFrame.setChartData(chartData);
							chartFrame.setTemplate(template);
							chartFrame.open();
						}
					});
				}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
	}

	protected @Override void paintComponent(Graphics g)
	{
		if (!drawStripes)
		{
			super.paintComponent(g);
			return;
		}

		updateZebraColors();
		final Insets insets = getInsets();
		final int width = getWidth() - insets.left - insets.right;
		final int height = getHeight() - insets.top - insets.bottom;
		final int x = insets.left;
		int y = insets.top;
		int nRows = 0;
		int startRow = 0;
		int rowHeight = getFixedCellHeight();
		if (rowHeight > 0)
			nRows = height / rowHeight;
		else
		{
			final int nItems = getModel().getSize();
			rowHeight = 17;
			for (int i = 0; i < nItems; i++)
			{
				rowHeight = getCellBounds(i, i).height;
				g.setColor(rowColors[i&1]);
				g.fillRect(x, y, width, rowHeight);
			}

			nRows = nItems + (insets.top + height - y) / rowHeight;
			startRow = nItems;
		}

		for (int i = startRow; i < nRows; i++, y += rowHeight)
		{
			g.setColor(rowColors[i&1]);
			g.fillRect(x, y, width, rowHeight);
		}

		final int remainder = insets.top + height - y;
		if (remainder > 0)
		{
			g.setColor(rowColors[nRows&1]);
			g.fillRect(x, y, width, remainder);
		}

		setOpaque(false);
		super.paintComponent(g);
		setOpaque(true);
	}

	public class ScanResultRenderer implements ListCellRenderer
	{

		public ListCellRenderer ren = null;

		public ScanResultRenderer()
		{}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			final JLabel label = new JLabel();

			String text = (String) value;

			String symbol = "";
			String exchange = "";
			String companyName = "";
			String date = "";

			int i;

			i = text.indexOf(",");
			symbol = text.substring(0, i);
			text = text.substring(i + 1, text.length());

			i = text.indexOf(",");
			exchange = text.substring(0, i);
			text = text.substring(i + 2, text.length());

			i = text.indexOf("\"");
			companyName = text.substring(0, i);
			text = text.substring(i + 2, text.length());

			date = text;

			label.setText(NbBundle.getMessage(ScanResultList.class, "ResultList_HTML", new String[]
			{
				symbol,
				exchange,
				companyName,
				date
			}));
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setOpaque(true);

			if (!isSelected && drawStripes)
				label.setBackground(rowColors[index&1]);
			
			if (isSelected)
			{
				label.setBackground(Color.decode("0x6BBA70"));
				label.setForeground(Color.decode("0xFFFFFF"));
			}
			
			return label;
		}

	}

	private ScanResultRenderer wrapper = null;

	public @Override ListCellRenderer getCellRenderer()
	{
		final ListCellRenderer ren = super.getCellRenderer();
		if (ren == null)
			return null;
		if (wrapper == null)
			wrapper = new ScanResultRenderer();
		wrapper.ren = ren;
		return wrapper;
	}

	private void updateZebraColors()
	{
		if ((rowColors[0] = getBackground()) == null)
		{
			rowColors[0] = rowColors[1] = java.awt.Color.white;
			return;
		}

		final java.awt.Color sel = getSelectionBackground();
		if (sel == null)
		{
			rowColors[1] = rowColors[0];
			return;
		}

		final float[] bgHSB = java.awt.Color.RGBtoHSB(
			rowColors[0].getRed(), rowColors[0].getGreen(),
			rowColors[0].getBlue(), null);

		final float[] selHSB  = java.awt.Color.RGBtoHSB(
			sel.getRed(), sel.getGreen(), sel.getBlue(), null );

		rowColors[1] = java.awt.Color.getHSBColor(
			(selHSB[1]==0.0||selHSB[2]==0.0) ? bgHSB[0] : selHSB[0],
			0.1f * selHSB[1] + 0.9f * bgHSB[1],
			bgHSB[2] + ((bgHSB[2]<0.5f) ? 0.05f : -0.05f));
	}

}
