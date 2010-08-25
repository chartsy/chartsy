package org.chartsy.stockscanpro.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import org.chartsy.main.utils.DesktopUtil;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class HelpPanel extends JPanel
{

	private HelpList list;

	public HelpPanel()
	{
		super(new BorderLayout());
		setOpaque(false);
		initComponents();
	}

	private void initComponents()
	{
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btnPanel.setOpaque(false);
		btnPanel.add(new HelpLinkButton(
			"Syntax Help",
			"http://www.stockscanpro.com/help"));
		btnPanel.add(new HelpLinkButton(
			"Video Tutorial",
			"http://www.chartsy.org/support/tutorial/3"));
		btnPanel.add(new HelpLinkButton(
			"Support",
			"http://www.stockscanpro.com/support"));
		add(btnPanel, BorderLayout.NORTH);

		JPanel listPanel = new JPanel(SpringUtilities.getLayout());
		listPanel.setOpaque(false);

		JLabel label = new JLabel("Available Indicators:");
		label.setOpaque(false);
		listPanel.add(label);
		
		list = new HelpList();
		JScrollPane scrollPane = new JScrollPane(
			list,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		label.setLabelFor(scrollPane);
		listPanel.add(scrollPane);

		SpringUtilities.makeCompactGrid(listPanel,
			listPanel.getComponentCount(), 1,
			5, 5,
			5, 5);
		add(listPanel, BorderLayout.CENTER);
	}

	class HelpList extends JList
	{

		public Color rowColors[] = new Color[2];
		private boolean drawStripes = true;

		public HelpList()
		{
			super(new HelpListModel());
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			addMouseListener(new MouseAdapter()
			{
				public @Override void mousePressed(MouseEvent e)
				{
					try
					{
						int index = locationToIndex(e.getPoint());
						HelpListItem item = (HelpListItem) getModel().getElementAt(index);
						DesktopUtil.browse(item.getUrl());
					}
					catch (Exception ex)
					{
						Exceptions.printStackTrace(ex);
					}
				}
			});
		}

		public @Override boolean isOpaque()
		{
			return true;
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

		class HelpListRenderer extends JLabel implements ListCellRenderer
		{

			public ListCellRenderer ren = null;

			public HelpListRenderer()
			{}

			public Component getListCellRendererComponent
				(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				HelpListItem item = (HelpListItem) value;
				setText(item.getDisplayName());
				setToolTipText("URL: " + item.getUrl());
				setOpaque(true);
				setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

				setForeground(Color.decode("0x000000"));

				if (!isSelected && drawStripes)
					setBackground(rowColors[index&1]);

				if (isSelected)
				{
					setBackground(Color.decode("0x6BBA70"));
					setForeground(Color.decode("0xFFFFFF"));
				}

				return this;
			}

		}

		private HelpListRenderer wrapper = null;

		public @Override ListCellRenderer getCellRenderer()
		{
			final ListCellRenderer ren = super.getCellRenderer();
			if (ren == null)
				return null;
			if (wrapper == null)
				wrapper = new HelpListRenderer();
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

	class HelpListModel extends AbstractListModel
	{

		private ArrayList<HelpListItem> list;

		public HelpListModel()
		{
			list = new ArrayList<HelpListItem>();
			String[] indicators = NbBundle.getMessage(
				HelpList.class, "LST_Indicators").split(":");
			for (String indicator : indicators)
			{
				String usage = NbBundle.getMessage(
					HelpList.class, "FCT_" + indicator);
				String url = NbBundle.getMessage(
					HelpList.class, "URL_" + indicator);

				HelpListItem item = new HelpListItem(usage, url);
				list.add(item);
			}
		}

		public int getSize()
		{
			return list.size();
		}

		public Object getElementAt(int index)
		{
			if (index < 0 || index >= list.size())
				return null;
			return list.get(index);
		}

	}

	class HelpListItem extends Object
	{

		private String displayName;
		private String url;

		public HelpListItem(String displayName, String url)
		{
			this.displayName = displayName;
			this.url = url;
		}

		public String getDisplayName()
		{
			return displayName;
		}

		public void setDisplayName(String displayName)
		{
			this.displayName = displayName;
		}

		public String getUrl()
		{
			return url;
		}

		public void setUrl(String url)
		{
			this.url = url;
		}

		public @Override String toString()
		{
			return displayName;
		}

	}

	class HelpLinkButton extends JButton
	{

		private String displayName;
		private String url;

		public HelpLinkButton(final String displayName, final String url)
		{
			super(new HelpLinkButtonAction(displayName, url));
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
			setMargin(new Insets(0, 0, 0, 0));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setBorderPainted(false);
			setFocusPainted(false);
			setContentAreaFilled(false);
			setRolloverEnabled(true);
			setFocusable(true);
			setForeground(new Color(0x203673));
			addMouseListener(new MouseAdapter()
			{
				public @Override void mouseEntered(MouseEvent e)
				{
					StatusDisplayer.getDefault().setStatusText(url);
					setForeground(new Color(0x284693));
					setFont(getUnderlineFont(getFont()));
				}
				public @Override void mouseExited(MouseEvent e)
				{
					StatusDisplayer.getDefault().setStatusText("");
					setForeground(new Color(0x203673));
					setFont(getNonUnderlineFont(getFont()));
				}
			});
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

		public String getDisplayName()
		{
			return displayName;
		}

		public void setDisplayName(String displayName)
		{
			this.displayName = displayName;
		}

		public String getUrl()
		{
			return url;
		}

		public void setUrl(String url)
		{
			this.url = url;
		}

	}

	class HelpLinkButtonAction extends AbstractAction
	{

		private String url;

		public HelpLinkButtonAction(String displayName, String url)
		{
			putValue(NAME, displayName);
			putValue(SHORT_DESCRIPTION, displayName);
			this.url = url;
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				DesktopUtil.browse(url);
			}
			catch (Exception ex)
			{
				Exceptions.printStackTrace(ex);
			}
		}

	}

}
