package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import org.chartsy.main.data.StockNode;

/**
 *
 * @author viorel.gheba
 */
public class AutocompletePopup extends JPopupMenu
{

    private JTextField txtSymbol;
    private JList list;
    private DefaultListModel model;
    private Point position;

    public AutocompletePopup(JTextField textField)
    {
        setBackground(Color.WHITE);
        txtSymbol = textField;
        model = new DefaultListModel();
        position = new Point(0, 0);
        loadUIElements();
        setEventManagement();
    }

    private void loadUIElements()
    {
        HtmlRendererImpl renderer = new HtmlRendererImpl();
        list = new JList(model)
        {
            public @Override boolean isOpaque()
            { return true; }
            public @Override int getVisibleRowCount()
            { return Math.min(model.getSize(), 20); }
        };
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBackground(Color.WHITE);
        list.setCellRenderer(renderer);
        JScrollPane scrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane);
    }

    private void setEventManagement()
    {
        list.addKeyListener(new WordMenuKeyListener());
        list.addMouseListener(new WordMenuMouseListener());
    }

    public boolean hasSelected()
    {
        Object obj = list.getSelectedValue();
        return obj != null;
    }

    public void onSelected()
    {
        Object obj = list.getSelectedValue();
        if (obj != null)
        {
            StockNode node = (StockNode) obj;
            String symbol = node.getSymbol();
            txtSymbol.setText(symbol);
        }
    }

    public void display(Point point)
    {
        position = point;
        Point p = txtSymbol.getLocationOnScreen();
        setLocation(new Point(p.x + point.x, p.y + point.y));
        setVisible(true);
    }

    public void move()
    {
        if (position != null)
        {
            Point p = txtSymbol.getLocationOnScreen();
            setLocation(new Point(p.x + position.x, p.y + position.y));
        }
    }

    public void setWords(StockNode[] words)
    {
        model.clear();
        if ((words == null) || (words.length == 0))
        {
            setVisible(false);
            return;
        }
        for (int i = 0; i < words.length; i++)
        {
            model.addElement(words[i]);
        }
        pack();
        pack();
    }

    public void moveDown()
    {
        if (model.getSize() < 1)
            return;
        int oldIndex = list.getSelectedIndex();
        int newIndex = Math.min(model.getSize() - 1, oldIndex + 1);
        list.setSelectionInterval(newIndex, newIndex);
        list.scrollRectToVisible(list.getCellBounds(newIndex, newIndex));
    }

    public void moveUp()
    {
        if (model.getSize() < 1)
            return;
        int oldIndex = list.getSelectedIndex();
        int newIndex = Math.max(0, oldIndex - 1);
        list.setSelectionInterval(newIndex, newIndex);
        list.scrollRectToVisible(list.getCellBounds(newIndex, newIndex));
    }

    public void moveStart()
    {
        if (model.getSize() < 1)
            return;
        list.setSelectionInterval(0, 0);
        list.scrollRectToVisible(list.getCellBounds(0, 0));
    }

    public void moveEnd()
    {
        if (model.getSize() < 1)
            return;
        int endIndex = model.getSize() - 1;
        list.setSelectionInterval(endIndex, endIndex);
        list.scrollRectToVisible(list.getCellBounds(endIndex, endIndex));
    }

    public void movePageUp()
    {
        if (model.getSize() < 1)
            return;
        int oldIndex = list.getSelectedIndex();
        int newIndex = Math.max(0, oldIndex - Math.max(0, list.getVisibleRowCount() - 1));
        list.setSelectionInterval(newIndex, newIndex);
        list.scrollRectToVisible(list.getCellBounds(newIndex, newIndex));
    }

    public void movePageDown()
    {
        if (model.getSize() < 1)
            return;
        int oldIndex = list.getSelectedIndex();
        int newIndex = Math.min(model.getSize() - 1, oldIndex + Math.max(0, list.getVisibleRowCount() - 1));
        list.setSelectionInterval(newIndex, newIndex);
        list.scrollRectToVisible(list.getCellBounds(newIndex, newIndex));
    }

    private class WordMenuKeyListener extends KeyAdapter
    {
        public @Override void keyPressed(KeyEvent e)
        {
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
            { onSelected(); }
        }
    }

    private class WordMenuMouseListener extends MouseAdapter
    {
        public @Override void mouseClicked(MouseEvent e)
        {
            if ((e.getButton() == MouseEvent.BUTTON1))
            { onSelected(); }
        }
    }

}