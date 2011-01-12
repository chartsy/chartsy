package org.chartsy.main.templates;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import org.chartsy.main.managers.TemplateManager;
import org.chartsy.main.resources.ResourcesUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor.Message;

/**
 *
 * @author Viorel
 */
public class TemplateManagerPanel extends JPanel
{

	private ZebraJList templateList;
	private JToolBar toolBar;
	private JButton defaultBtn;
	private JButton deleteBtn;

	public TemplateManagerPanel()
	{
		super(new BorderLayout());
		setOpaque(false);
		initComponents();
	}

	private void initComponents()
	{
		templateList = new ZebraJList(TemplateManager.getDefault().getTemplateNames());
		templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		templateList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		templateList.setPreferredSize(new Dimension(400, 500));
		add(templateList, BorderLayout.CENTER);

		toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolBar.setPreferredSize(new Dimension(150, 500));
		toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(toolBar, BorderLayout.EAST);

		defaultBtn = new JButton(new SetDefault());
		defaultBtn.setIcon(ResourcesUtils.getIcon("default"));
		defaultBtn.setOpaque(true);
		defaultBtn.setBorderPainted(true);
		defaultBtn.setRolloverEnabled(true);
		toolBar.add(defaultBtn);

		deleteBtn = new JButton(new Delete());
		deleteBtn.setIcon(ResourcesUtils.getIcon("delete"));
		deleteBtn.setOpaque(true);
		deleteBtn.setBorderPainted(true);
		deleteBtn.setRolloverEnabled(true);
		toolBar.add(deleteBtn);
	}

	public class SetDefault extends AbstractAction
	{

		public SetDefault()
		{
			putValue(NAME, "Set As Default");
			putValue(SHORT_DESCRIPTION, "Set the selected template as default");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int index = templateList.getSelectedIndex();
			if (index != -1)
			{
				String name = (String) templateList.getSelectedValue();
				TemplateManager.getDefault().setDefaultTemplate(name);
			}
		}

	}

	public class Delete extends AbstractAction
	{

		public Delete()
		{
			putValue(NAME, "Delete");
			putValue(SHORT_DESCRIPTION, "Delete the selected template");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (templateList.getModel().getSize() > 1)
			{
				int index = templateList.getSelectedIndex();
				if (index != -1)
				{
					String name = (String) templateList.getSelectedValue();
					if (!name.equals(TemplateManager.getDefault().getDefaultTemplate()))
					{
						TemplateManager.getDefault().removeTemplate(name);
						templateList.clearSelection();
						templateList.setListData(TemplateManager.getDefault().getTemplateNames());
					}
					else
					{
						Message message = new Message("You can't delete the default template.", DialogDescriptor.WARNING_MESSAGE);
						DialogDisplayer.getDefault().notify(message);
					}
				}
			}
			else
			{
				Message message = new Message("You can't delete the default template.", DialogDescriptor.WARNING_MESSAGE);
				DialogDisplayer.getDefault().notify(message);
			}
		}

	}

	public class ZebraJList
		extends javax.swing.JList
	{
		private java.awt.Color rowColors[] = new java.awt.Color[2];
		private boolean drawStripes = false;

		public ZebraJList() {}
		public ZebraJList(javax.swing.ListModel dataModel)
		{
			super(dataModel);
		}
		public ZebraJList(Object[] listData)
		{
			super(listData);
		}

		@Override
		public void paintComponent(java.awt.Graphics g)
		{
			drawStripes = (getLayoutOrientation()==VERTICAL) && isOpaque();
			if (!drawStripes)
			{
				super.paintComponent(g);
				return;
			}

			updateZebraColors();
			final java.awt.Insets insets = getInsets();
			final int w   = getWidth()  - insets.left - insets.right;
			final int h   = getHeight() - insets.top  - insets.bottom;
			final int x   = insets.left;
			int y         = insets.top;
			int nRows     = 0;
			int startRow  = 0;
			int rowHeight = getFixedCellHeight();
			if (rowHeight > 0)
				nRows = h / rowHeight;
			else
			{
				final int nItems = getModel().getSize();
				rowHeight = 17;
				for (int i = 0; i < nItems; i++, y+=rowHeight)
				{
					rowHeight = getCellBounds(i, i).height;
					g.setColor(rowColors[i&1]);
					g.fillRect(x, y, w, rowHeight);
				}
				nRows    = nItems + (insets.top + h - y) / rowHeight;
				startRow = nItems;
			}
			for (int i = startRow; i < nRows; i++, y+=rowHeight)
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

		private class RendererWrapper
			implements javax.swing.ListCellRenderer
		{
			public javax.swing.ListCellRenderer ren = null;

			@Override
			public java.awt.Component getListCellRendererComponent(
				javax.swing.JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus)
			{
				String temp = (String) value;
				if (TemplateManager.getDefault().getDefaultTemplate().equals(temp))
					temp += " (default)";
				final java.awt.Component c = ren.getListCellRendererComponent(
					list, temp, index, isSelected, cellHasFocus);
				if (!isSelected && drawStripes)
					c.setBackground(rowColors[index&1]);
				return c;
			}
		}
		private RendererWrapper wrapper = null;

		@Override
		public javax.swing.ListCellRenderer getCellRenderer()
		{
			final javax.swing.ListCellRenderer ren = super.getCellRenderer();
			if (ren == null)
				return null;
			if (wrapper == null)
				wrapper = new RendererWrapper();
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

}
