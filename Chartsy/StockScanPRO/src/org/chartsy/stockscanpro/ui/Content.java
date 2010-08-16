package org.chartsy.stockscanpro.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JLayeredPane;

/**
 *
 * @author Viorel
 */
public class Content extends JLayeredPane
{

	private LogoBar logoBar;
	private QueryPanel queryPanel;
	private HelpPanel helpPanel;
	private ResultsPanel resultsPanel;

    public Content()
    {
		logoBar = new LogoBar();
		queryPanel = new QueryPanel(this);
		helpPanel = new HelpPanel();
		resultsPanel = new ResultsPanel();

        setOpaque(true);
        setBackground(Color.decode("0xf3f2f2"));
		setLayout(new StockScanLayout());

		add(logoBar);
		add(queryPanel);
		add(helpPanel);
		add(resultsPanel);
    }

	public QueryPanel getQueryPanel()
	{
		return queryPanel;
	}

	public ResultsPanel getResultsPanel()
	{
		return resultsPanel;
	}

	private class StockScanLayout implements LayoutManager
	{

		public void addLayoutComponent(String name, Component comp)
		{}

		public void removeLayoutComponent(Component comp)
		{}

		public Dimension preferredLayoutSize(Container parent)
		{
			return new Dimension(0, 0);
		}

		public Dimension minimumLayoutSize(Container parent)
		{
			return new Dimension(0, 0);
		}

		public void layoutContainer(Container parent)
		{
			int gap = 10;

			Insets insets = parent.getInsets();
			int width = parent.getWidth() - insets.left - insets.right;
			int height = parent.getHeight() - insets.top - insets.bottom;

			int logoWidth = logoBar.getPreferredSize().width;
			int logoHeight = logoBar.getPreferredSize().height;

			int restHeight = height - logoHeight - 2*gap;

			int centerWidth = (width - 3*gap) / 2;
			int centerHeight = (int) ((restHeight - 2*gap) * 0.6);

			if (centerHeight < 300)
			{
				logoBar.setVisible(false);
				centerHeight += logoHeight;
			}

			int bottomWidth = (width - 2*gap);
			int bottomHeight = restHeight - 2*gap - centerHeight;

			logoBar.setBounds(
				(width / 2) - (logoWidth / 2),
				gap,
				logoWidth,
				logoHeight);

			queryPanel.setBounds(
				gap,
				2*gap + (logoBar.isVisible() ? logoHeight : 0),
				centerWidth,
				centerHeight);

			helpPanel.setBounds(
				2*gap + centerWidth,
				2*gap + (logoBar.isVisible() ? logoHeight : 0),
				centerWidth,
				centerHeight);

			resultsPanel.setBounds(
				gap,
				3*gap + (logoBar.isVisible() ? logoHeight : 0) + centerHeight,
				bottomWidth,
				bottomHeight);
		}

	}

}
