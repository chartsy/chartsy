package org.chartsy.stockscanpro.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

/**
 *
 * @author Viorel
 */
public class ExchangePopup extends JPopupMenu
{

    private JCheckList list;

    public ExchangePopup()
    {
        setBackground(Color.WHITE);
        loadUIElements();
        addKeyListener(new ExchangePopupKeyListener());
    }

    private void loadUIElements()
    {
        list = new JCheckList();
        JScrollPane scrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane);
    }

    public @Override void show(Component invoker, int x, int y)
    {
        if (list.getModel().getSize() < 1)
            return;
        super.show(invoker, x, y);
        requestFocusInWindow();
    }

	public Object[] getSelectedItems()
	{
		return list.getSelectedValues();
	}

    public int getSelectedExchanges()
    {
        return list.getSelectedItems();
    }

    public void setExchangeListener(JLabel label)
    {
        list.setExchangeListener(label);
    }

    public void select()
    {
        if (list.getModel().getSize() < 1)
            return;
        int index = list.getSelectedIndex();
        JCheckList.CheckableItem item = (JCheckList.CheckableItem) list.getSelectedValue();
        item.setSelected(!item.isSelected());
        list.repaint(list.getCellBounds(index, index));
    }

    public void moveDown()
    {
        if (list.getModel().getSize() < 1)
            return;
        int oldIndex = list.getSelectedIndex();
        int newIndex = Math.min(list.getModel().getSize() - 1, oldIndex + 1);
        list.setSelectionInterval(newIndex, newIndex);
        list.scrollRectToVisible(list.getCellBounds(newIndex, newIndex));
    }

    public void moveUp()
    {
        if (list.getModel().getSize() < 1)
            return;
        int oldIndex = list.getSelectedIndex();
        int newIndex = Math.max(0, oldIndex - 1);
        list.setSelectionInterval(newIndex, newIndex);
        list.scrollRectToVisible(list.getCellBounds(newIndex, newIndex));
    }

    public void moveStart()
    {
        if (list.getModel().getSize() < 1)
            return;
        list.setSelectionInterval(0, 0);
        list.scrollRectToVisible(list.getCellBounds(0, 0));
    }

    public void moveEnd()
    {
        if (list.getModel().getSize() < 1)
            return;
        int endIndex = list.getModel().getSize() - 1;
        list.setSelectionInterval(endIndex, endIndex);
        list.scrollRectToVisible(list.getCellBounds(endIndex, endIndex));
    }

    public void movePageUp()
    {
        if (list.getModel().getSize() < 1)
            return;
        int oldIndex = list.getSelectedIndex();
        int newIndex = Math.max(0, oldIndex - Math.max(0, list.getVisibleRowCount() - 1));
        list.setSelectionInterval(newIndex, newIndex);
        list.scrollRectToVisible(list.getCellBounds(newIndex, newIndex));
    }

    public void movePageDown()
    {
        if (list.getModel().getSize() < 1)
            return;
        int oldIndex = list.getSelectedIndex();
        int newIndex = Math.min(list.getModel().getSize() - 1, oldIndex + Math.max(0, list.getVisibleRowCount() - 1));
        list.setSelectionInterval(newIndex, newIndex);
        list.scrollRectToVisible(list.getCellBounds(newIndex, newIndex));
    }

    private class ExchangePopupKeyListener extends KeyAdapter
    {
        public @Override void keyPressed(KeyEvent e)
        {
            if (e.isConsumed())
                return;

            if (ExchangePopup.this.isVisible())
            {
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_UP:
                        moveUp();
                        e.consume();
                        break;
                    case KeyEvent.VK_DOWN:
                        moveDown();
                        e.consume();
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        movePageUp();
                        e.consume();
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        movePageDown();
                        e.consume();
                        break;
                    case KeyEvent.VK_END:
                        moveEnd();
                        e.consume();
                        break;
                    case KeyEvent.VK_HOME:
                        moveStart();
                        e.consume();
                        break;
                    case KeyEvent.VK_ENTER:
                        select();
                        e.consume();
                        break;
                }
            }
        }
    }

}
