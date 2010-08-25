package org.chartsy.stockscanpro.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import org.chartsy.main.utils.FileUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author Viorel
 */
public class JCheckList extends JList
{

	public Color rowColors[] = new Color[2];
	private boolean drawStripes = true;

    private JLabel listener;
    private int selectedItems = 0;
	private String selectedExcg = "";

    public JCheckList()
    {
		setListData(createData());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        addMouseListener(new MouseListener()
        {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e)
            {
                int index = JCheckList.this.locationToIndex(e.getPoint());
                CheckableItem item = (CheckableItem) JCheckList.this.getModel().getElementAt(index);
                boolean selected = !item.isSelected();
                item.setSelected(selected);
                Rectangle rectangle = JCheckList.this.getCellBounds(index, index);
                JCheckList.this.repaint(rectangle);

                if (selected)
                {
                    selectedItems++;
                    if (selectedItems > JCheckList.this.getModel().getSize())
                        selectedItems = JCheckList.this.getModel().getSize();
                }
                else
                {
                    selectedItems--;
                    if (selectedItems < 0)
                        selectedItems = 0;
                }

                fireExchangeChange();
            }
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
        if (getModel().getSize() > 0)
            setSelectedIndex(0);
    }

    public void setExchangeListener(JLabel label)
    {
        this.listener = label;
    }

    public void fireExchangeChange()
    {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < getModel().getSize(); i++) {
			CheckableItem item = (CheckableItem) getModel().getElementAt(i);
			if (item.isSelected()) {
				builder.append(item.getExchange()).append(":");
			}
		}
		
		if (builder.length() > 1)
		{
			builder.deleteCharAt(builder.length() - 1);
			writeSelectedXcg(builder.toString());
		}

        if (listener != null)
        {
            listener.setText(selectedItems + " exchange(s) are selected.");
            listener.repaint(listener.getBounds());
        }
    }

	public @Override Object[] getSelectedValues()
	{
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < getModel().getSize(); i++)
		{
			Object obj = getModel().getElementAt(i);
			if (obj instanceof CheckableItem)
				if (((CheckableItem) obj).isSelected())
					list.add(obj);
		}
		return list.toArray(new Object[list.size()]);
	}


    public int getSelectedItems()
    {
        return selectedItems;
    }

    public @Override int getVisibleRowCount()
    {
        return Math.min(getModel().getSize(), 10);
    }

    public @Override boolean isOpaque()
    {
        return true;
    }

	private String readSelectedXcg()
	{
		File file = FileUtils.stockScanFile("selxcg.txt");
		if (!file.exists())
		{
			FileUtils.createFile(file.getAbsolutePath());
			return "";
		}

		try
		{
			StringBuilder contents = new StringBuilder();
			BufferedReader input =  new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = input.readLine()) != null) {
				contents.append(line);
			}
			input.close();
			return contents.toString();
		}
		catch (IOException ex)
		{ return ""; }
	}

	private void writeSelectedXcg(String contents)
	{
		File file = FileUtils.stockScanFile("selxcg.txt");
		if (!file.exists())
			FileUtils.createFile(file.getAbsolutePath());
		
		try
		{
			Writer output = new BufferedWriter(new FileWriter(file));
			output.write(contents);
			output.close();
		}
		catch (IOException ex)
		{}
	}

    private CheckableItem[] createData()
    {
		String defaultExchanges = NbBundle.getMessage(JCheckList.class, "DFLT_Exchange");
		String selectedExchanges = readSelectedXcg();

		if (selectedExchanges.isEmpty())
		{
			selectedItems = defaultExchanges.split(":").length;
			selectedExcg = defaultExchanges;
		}
		else
		{
			selectedItems = selectedExchanges.split(":").length;
			selectedExcg = selectedExchanges;
		}
		

        int length = NbBundle.getMessage(JCheckList.class, "LIST_Exchange").split(":").length;
        CheckableItem[] list = new CheckableItem[length];

        for (int i = 0; i < length; i++)
        {
            CheckableItem item = new CheckableItem(NbBundle.getMessage(JCheckList.class, "LIST_Exchange").split(":")[i]);
            if (isDefault(item.getExchange()))
                item.setSelected(true);
            list[i] = item;
        }
        
        return list;
    }

    private boolean isDefault(String exchange)
    {
		return selectedExcg.contains(exchange);
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

    class CheckableItem
    {

        private String exchange;
        private boolean isSelected;

        public CheckableItem(String string)
        {
            this.exchange = string;
            this.isSelected = false;
        }

        public String getExchange()
        {
            return exchange;
        }

        public String getTooltip()
        {
            return NbBundle.getMessage(JCheckList.class, exchange + "_Desc");
        }

        public void setSelected(boolean b)
        {
            isSelected = b;
        }

        public boolean isSelected()
        {
            return isSelected;
        }

        public @Override String toString()
        {
            return exchange + " (" + NbBundle.getMessage(JCheckList.class, exchange + "_Desc") + ")";
        }

    }

    class JCheckListRenderer implements ListCellRenderer
    {

		public ListCellRenderer ren = null;

        public JCheckListRenderer()
        {}

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
			final JCheckBox box = new JCheckBox();

            CheckableItem item = (CheckableItem) value;
            box.setSelected(item.isSelected());
			box.setText(item.toString());
			box.setToolTipText(item.getTooltip());
			box.setOpaque(true);

			if (!isSelected && drawStripes)
				box.setBackground(rowColors[index&1]);

            if (isSelected)
            {
                box.setBackground(Color.decode("0x6BBA70"));
				box.setForeground(Color.decode("0xFFFFFF"));
            }
            
            return box;
        }

    }

	private JCheckListRenderer wrapper = null;

	public @Override ListCellRenderer getCellRenderer()
	{
		final ListCellRenderer ren = super.getCellRenderer();
		if (ren == null)
			return null;
		if (wrapper == null)
			wrapper = new JCheckListRenderer();
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
