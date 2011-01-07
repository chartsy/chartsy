package org.chartsy.main.favorites;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.chartsy.main.favorites.nodes.StockAPI;
import org.chartsy.main.favorites.nodes.StockAPINode;
import org.chartsy.main.resources.ResourcesUtils;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;

/**
 *
 * @author Viorel
 */
public class FavoritesNodeRenderer extends DefaultTreeCellRenderer
{

	public FavoritesNodeRenderer()
	{
		super();
		setOpenIcon(ResourcesUtils.getIcon("folder"));
		setClosedIcon(getOpenIcon());
		setLeafIcon(null);
	}

	public @Override Component getTreeCellRendererComponent
		(JTree tree, Object value, boolean selected, boolean expanded,
		boolean leaf, int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		Node node = Visualizer.findNode(value);

		if (node instanceof StockAPINode)
		{
			StockAPINode stockNode = (StockAPINode) node;
			StockAPI stockObject = stockNode.getStock();

			StockTable stockTable = new StockTable(stockObject.getData());
			stockTable.setFont(getRendererFont());
			stockTable.setBackground(FavoritesTreeView.rowColors[row&1]);
			stockTable.setRowHeight(tree.getRowHeight());
			if (selected)
				stockTable.selectAll();

			return stockTable;
		}

		setEnabled(tree.isEnabled());
		setFont(getRendererFont());
		setOpaque(false);

		return this;
	}

	private Font getRendererFont()
	{
		int fs = 11;
		Object cfs = UIManager.get("customFontSize");
		if (cfs instanceof Integer)
			fs = ((Integer) cfs).intValue();
		Font font = new Font("Monospaced", Font.PLAIN, fs);
		return font;
	}

	public @Override void validate() {}
	public @Override void repaint(long tm, int x, int y, int w, int h) {}
	public @Override void repaint() {}
	public @Override void invalidate() {}
	public @Override void revalidate() {}

	public static class StockTable extends JTable
	{

		public static String[] columns =
		{
			"Stock", "Last Value", "Change", "Percent"
		};

		public StockTable(Object[][] rowData)
		{
			super(rowData, columns);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setColumnSelectionAllowed(false);
			setRowSelectionAllowed(true);
			setIntercellSpacing(new Dimension(0, 0));
			setOpaque(true);
			setBorder(null);
			setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			int columnCount = getColumnCount();
			for (int i = 0; i < columnCount; i++)
			{
				columnModel.getColumn(i).setCellRenderer(new StockCellRenderer());
				columnModel.getColumn(i).setPreferredWidth(50);
			}
		}

	}

	public static class StockCellRenderer extends JLabel implements TableCellRenderer
	{

		public StockCellRenderer()
		{
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent
			(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)
		{
			if (isSelected)
			{
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			}
			else
			{
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}

			DecimalFormat df = new DecimalFormat("#,##0.00");
			String stringValue = "";

			if (column > 1)
			{
				double d = Double.parseDouble(value.toString());
				stringValue = df.format(d);
				
				if (d > 0)
				{
					stringValue = " " + stringValue;
					setForeground(Color.decode("0x73d216"));
				}
				else if (d < 0)
					setForeground(Color.decode("0xef2929"));
				else
					stringValue = " " + stringValue;

				if (column == 3)
					stringValue += "%";
			}
			else if (column == 1)
			{
				double d = Double.parseDouble(value.toString());
				stringValue = df.format(d);
			}
			else
			{
				stringValue = value.toString();
			}
			
			setBorder(BorderFactory.createEmptyBorder());
			setFont(table.getFont());

			setText(stringValue);
			return this;
		}

		public @Override void validate() {}
		public @Override void repaint(long tm, int x, int y, int w, int h) {}
		public @Override void repaint() {}
		public @Override void invalidate() {}
		public @Override void revalidate() {}

	}

}
