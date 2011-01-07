package org.chartsy.chatsy.chat.component;

import org.chartsy.chatsy.chat.util.GraphicUtils;
import org.jdesktop.swingx.JXTable;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Table extends JXTable
{

    private Table.JiveTableModel tableModel;
    public static final Color SELECTION_COLOR = new Color(166, 202, 240);
    public static final Color TOOLTIP_COLOR = new Color(166, 202, 240);
    private final Map<Integer,Object> objectMap = new HashMap<Integer,Object>();

    protected Table()
	{
    }

    public String getToolTipText(MouseEvent e)
	{
        int r = rowAtPoint(e.getPoint());
        int c = columnAtPoint(e.getPoint());
        Object value;
        try
		{
            value = getValueAt(r, c);
        }
        catch (Exception ex)
		{
            return "";
        }

        String tooltipValue = null;
        if (value instanceof JLabel)
            tooltipValue = ((JLabel)value).getToolTipText();
        if (value instanceof JLabel && tooltipValue == null)
            tooltipValue = ((JLabel)value).getText();
        else if (value != null && tooltipValue == null)
            tooltipValue = value.toString();
        else if (tooltipValue == null)
            tooltipValue = "";
        return GraphicUtils.createToolTip(tooltipValue);
    }

    public TableCellRenderer getCellRenderer(int row, int column)
	{
        Object o = getValueAt(row, column);
        if (o != null)
            if (o instanceof JLabel)
                return new JLabelRenderer(false);
        return super.getCellRenderer(row, column);
    }

    protected Table(String[] headers)
	{
        tableModel = new Table.JiveTableModel(headers, 0, false);
        setModel(tableModel);
        JTableHeader header = getTableHeader();
        Dimension dim = header.getPreferredSize();
        dim.height = 20;
        header.setPreferredSize(dim);

        getTableHeader().setReorderingAllowed(false);
        setGridColor(Color.white);
        setRowHeight(20);
        getColumnModel().setColumnMargin(0);
        setSelectionBackground(SELECTION_COLOR);
        setSelectionForeground(Color.black);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.addKeyListener(new KeyListener()
		{
            public void keyPressed(KeyEvent e)
			{
                if (e.getKeyChar() == KeyEvent.VK_ENTER)
				{
                    e.consume();
                    enterPressed();
                }
            }
            public void keyReleased(KeyEvent e)
			{
            }
            public void keyTyped(KeyEvent e)
			{
            }
        });
    }

    public Component prepareRenderer(TableCellRenderer renderer, 
		int rowIndex, int vColIndex)
	{
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex))
            c.setBackground(getBackground());
        else if (isCellSelected(rowIndex, vColIndex))
            c.setBackground(SELECTION_COLOR);
        else
            c.setBackground(getBackground());
        return c;
    }

    public void add(List list)
	{
        for (Object aList : list)
		{
            Object[] newRow = (Object[]) aList;
            tableModel.addRow(newRow);
        }
    }

    public Object[] getSelectedRowObject()
	{
        return getRowObject(getSelectedRow());
    }

    public Object[] getRowObject(int selectedRow)
	{
        if (selectedRow < 0)
            return null;
        int columnCount = getColumnCount();
        Object[] obj = new Object[columnCount];
        for (int j = 0; j < columnCount; j++)
		{
            Object objs = tableModel.getValueAt(selectedRow, j);
            obj[j] = objs;
        }
        return obj;
    }

    public void clearTable()
	{
        int rowCount = getRowCount();
        for (int i = 0; i < rowCount; i++)
            getTableModel().removeRow(0);
    }

    public static class JiveTableModel extends DefaultTableModel
	{

		private boolean isEditable;

        public JiveTableModel(Object[] columnNames, int numRows, boolean isEditable)
		{
            super(columnNames, numRows);
            this.isEditable = isEditable;
        }

        public boolean isCellEditable(int row, int column)
		{
            return isEditable;
        }
		
    }

    public class JLabelRenderer extends JLabel implements TableCellRenderer 
	{

        Border unselectedBorder;
        Border selectedBorder;
        boolean isBordered = true;

        public JLabelRenderer(boolean isBordered)
		{
            setOpaque(true);
            this.isBordered = isBordered;
        }

        public Component getTableCellRendererComponent(JTable table,
			Object color, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
            final String text = ((JLabel)color).getText();
            if (text != null)
                setText(" " + text);
            final Icon icon = ((JLabel)color).getIcon();
            setIcon(icon);

            if (isSelected)
			{
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            }
            else
			{
                setForeground(Color.black);
                setBackground(Color.white);
            }

            if (isBordered)
			{
                if (isSelected)
				{
                    if (selectedBorder == null)
                        selectedBorder = BorderFactory.createMatteBorder(
							2, 5, 2, 5, table.getSelectionBackground());
                    setBorder(selectedBorder);
                }
                else
				{
                    if (unselectedBorder == null)
                        unselectedBorder = BorderFactory.createMatteBorder(
							2, 5, 2, 5, table.getBackground());
                    setBorder(unselectedBorder);
                }
            }
            return this;
        }
    }

    public class TextAreaCellRenderer extends JTextArea implements TableCellRenderer
	{

        public TextAreaCellRenderer(Font font)
		{
            setLineWrap(true);
            setWrapStyleWord(true);
            setFont(font);
        }

        public Component getTableCellRendererComponent(JTable jTable,
			Object obj, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
            setText(obj == null ? "" : obj.toString());
            setSize(jTable.getColumnModel().getColumn(column).getWidth(),
				getPreferredSize().height);
            if (jTable.getRowHeight(row) != getPreferredSize().height)
                jTable.setRowHeight(row, getPreferredSize().height);
            return this;
        }
    }

    public class JButtonRenderer extends JButton implements TableCellRenderer
	{

        Border unselectedBorder;
        Border selectedBorder;
        boolean isBordered = true;

        public JButtonRenderer()
		{
        }

        public Component getTableCellRendererComponent(JTable table,
			Object color, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
            final String text = ((JButton)color).getText();
            setText(text);

            final Icon icon = ((JButton)color).getIcon();
            setIcon(icon);

            if (isSelected)
			{
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            }
            else
			{
                setForeground(Color.black);
                setBackground(Color.white);
            }

            if (isBordered)
			{
                if (isSelected)
				{
                    if (selectedBorder == null)
                        selectedBorder = BorderFactory.createMatteBorder(
							2, 5, 2, 5, table.getSelectionBackground());
                    setBorder(selectedBorder);
                }
                else
				{
                    if (unselectedBorder == null)
                        unselectedBorder = BorderFactory.createMatteBorder(
							2, 5, 2, 5, table.getBackground());
                    setBorder(unselectedBorder);
                }
            }
            return this;
        }
    }

    public class ComboBoxRenderer extends JComboBox implements TableCellRenderer 
	{

        public ComboBoxRenderer()
		{
        }

        public ComboBoxRenderer(String[] items)
		{
            super(items);
        }

        public Component getTableCellRendererComponent(JTable table, 
			Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
            if (isSelected)
			{
                setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            }
            else
			{
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            setSelectedItem(value);
            return this;
        }
    }

    public class MyComboBoxEditor extends DefaultCellEditor
	{
        public MyComboBoxEditor(String[] items)
		{
            super(new JComboBox(items));
        }
    }

    public Table.JiveTableModel getTableModel()
	{
        return tableModel;
    }

    public void clearObjectMap()
	{
        objectMap.clear();
    }

    public void addObject(int row, Object object)
	{
        objectMap.put(row, object);
    }

    public Object getObject(int row)
	{
        return objectMap.get(row);
    }

    public void enterPressed()
	{
    }

}