package org.chartsy.main.favorites;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.favorites.nodes.StockAPI;
import org.chartsy.main.favorites.nodes.StockAPINode;
import org.chartsy.main.intervals.DailyInterval;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.TemplateManager;
import org.chartsy.main.templates.Template;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.NodeTreeModel;
import org.openide.explorer.view.TreeView;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;

/**
 *
 * @author Viorel
 */
public class FavoritesTreeView extends TreeView
{

	public static Color rowColors[] = new Color[]
	{
		Color.decode("0xffffff"),
		Color.decode("0xeeeeee")
	};
	private boolean drawStripes = true;

	private static FavoritesTreeView instance;

	public static FavoritesTreeView getDefault()
	{
		if (instance == null)
			instance = new FavoritesTreeView();
		return instance;
	}

	private FavoritesTreeView()
	{
		setBorder(BorderFactory.createEmptyBorder());
		setRootVisible(false);
		tree.setCellRenderer(new FavoritesNodeRenderer());

		setOpaque(true);
		getViewport().setOpaque(false);
		tree.setOpaque(false);
		tree.addMouseListener(new MouseAdapter()
		{
			public @Override void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1
					&& e.getClickCount() == 2)
				{
					TreePath treePath
						= tree.getPathForLocation(e.getX(), e.getY());
					if (treePath != null)
					{
						Node node = Visualizer.findNode(treePath.getLastPathComponent());
						if (node instanceof StockAPINode)
						{
							StockAPINode stockNode = (StockAPINode) node;
							StockAPI stock = stockNode.getStock();

							if (stock != null)
							{
								String defaultTemplate = TemplateManager.getDefault().getDefaultTemplate();
								Template template = TemplateManager.getDefault().getTemplate(defaultTemplate);
								
								ChartData chartData = new ChartData();
								chartData.setStock(stock.getStock());
								chartData.setDataProviderName(stock.getDataProviderName());
								chartData.setInterval(new DailyInterval());
								chartData.setChart(ChartManager.getDefault().getChart("Candle Stick"));

								ChartFrame chartFrame = ChartFrame.getInstance();
								chartFrame.setChartData(chartData);
								chartFrame.setTemplate(template);
								chartFrame.open();
								chartFrame.requestActive();
							}
						}
					}
				}
			}
		});
	}

	protected NodeTreeModel createModel()
	{
		return new NodeTreeModel();
	}

	protected void selectionChanged(Node[] nodes, ExplorerManager em) 
		throws PropertyVetoException
	{
		if (nodes.length > 0)
		{
			Node context = nodes[0].getParentNode();
			for (int i = 1; i < nodes.length; i++)
			{
				if (context != nodes[i].getParentNode())
				{
					em.setSelectedNodes(nodes);
					return;
				}
			}

			if (em.getRootContext().getParentNode() == context)
				em.setExploredContextAndSelection(null, nodes);
			else
				em.setExploredContextAndSelection(context, nodes);
		}
		else
			em.setSelectedNodes(nodes);
	}

	protected boolean selectionAccept(Node[] nodes)
	{
		return true;
	}

	protected void showPath(TreePath path)
	{
		tree.expandPath(path);
		showPathWithoutExpansion(path);
	}

	private void showPathWithoutExpansion(TreePath path)
	{
		Rectangle rect = tree.getPathBounds(path);
		if (rect != null)
			tree.scrollRectToVisible(rect);
	}

	protected void showSelection(TreePath[] treePaths)
	{
		tree.getSelectionModel().setSelectionPaths(treePaths);
		if (treePaths.length == 1)
			showPathWithoutExpansion(treePaths[0]);
	}

	public @Override void setEnabled(boolean enabled)
	{
		this.tree.setEnabled(enabled);
	}

	public @Override boolean isEnabled()
	{
		if (this.tree == null)
			return true;
		return this.tree.isEnabled();
	}

	public JTree getTree()
	{
		return tree;
	}

	public @Override void paintComponent(Graphics g)
	{
		if (!(drawStripes = isOpaque()))
        {
            super.paintComponent(g);
            return;
        }

		final java.awt.Insets insets = tree.getInsets();
        final int w   = tree.getWidth() - insets.left - insets.right;
        final int h   = tree.getHeight() - insets.top  - insets.bottom;
        final int x   = insets.left;
        int y         = insets.top;
        int nRows     = 0;
        int startRow  = 0;
        int rowHeight = tree.getRowHeight();

        if (rowHeight > 0)
		{
			nRows = h / rowHeight;
		}
		else
        {
            final int nItems = tree.getRowCount();
            rowHeight = 17;
            for (int i = 0; i < nItems; i++, y += rowHeight)
            {
                rowHeight = tree.getRowBounds(i).height;
                g.setColor(rowColors[i&1]);
                g.fillRect(x, y, w, rowHeight);
            }

            nRows    = nItems + (insets.top + h - y) / rowHeight;
            startRow = nItems;
        }
		
        for (int i = startRow; i < nRows; i++, y += rowHeight)
        {
            g.setColor(rowColors[i&1]);
            g.fillRect(x, y, w, rowHeight);
        }
		
        final int remainder = insets.top + h - y;
        if (remainder > 0)
        {
            g.setColor(rowColors[nRows&1]);
            g.fillRect(x, y, w, remainder);
        }

        setOpaque(false);
        super.paintComponent(g);
        setOpaque(true);
	}

}
