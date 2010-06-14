package org.chartsy.favorites.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.SystemColor;
import java.util.Hashtable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Viorel
 */
public class ZebraJTree extends JTree
{

    public Color rowColors[] = new Color[2];
    private boolean drawStripes = true;

    public ZebraJTree()
    {}

    public ZebraJTree(Hashtable<?, ?> value)
    {
        super(value);
    }

    public ZebraJTree(Object[] value)
    {
        super(value);
    }

    public ZebraJTree(TreeModel newModel)
    {
        super(newModel);
    }

    public ZebraJTree(TreeNode root)
    {
        super(root);
    }

    public ZebraJTree(TreeNode root, boolean asksAllowsChildren)
    {
        super(root, asksAllowsChildren);
    }

    public void paintComponent(Graphics g)
    {
        if (!(drawStripes = isOpaque()))
        {
            super.paintComponent(g);
            return;
        }

        updateZebraColors();
        final Insets insets = getInsets();
        final int w = getWidth() - insets.left - insets.right;
        final int h = getHeight() - insets.top - insets.bottom;
        final int x = insets.left;
        int y = insets.top;

        int nRows = 0;
        int startRow = 0;
        int rowHeight = getRowHeight();
        if (rowHeight > 0)
            nRows = h / rowHeight;
        else
        {
            final int nItems = getRowCount();
            rowHeight = 17;
            for (int i = 0; i < nItems; i++)
            {
                rowHeight = getRowBounds(i).height;
                g.setColor(rowColors[i&1]);
                g.fillRect(x, y, w, rowHeight);
            }
            nRows = nItems + (insets.top + h - y) / rowHeight;
            startRow = nItems;
        }
        for (int i = startRow; i < nRows; i++)
        {
            g.setColor(rowColors[i&1]);
            g.fillRect(x, y, w, rowHeight);
        }
        final int reminder = insets.top - h - y;
        if (reminder > 0)
        {
            g.setColor(rowColors[nRows&1]);
            g.fillRect(x, y, w, reminder);
        }

        setOpaque(false);
        super.paintComponent(g);
        setOpaque(true);
    }

    public class RendererEditorWrapper implements TreeCellRenderer, TreeCellEditor
    {

        TreeCellRenderer ren = null;
        TreeCellEditor ed = null;

        public java.awt.Component getTreeCellRendererComponent(
            javax.swing.JTree tree, Object value,
            boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus)
        {
            final Component component =
                    ren.getTreeCellRendererComponent(
                    tree, value, selected, expanded, leaf, row, hasFocus);

            if (selected || !drawStripes)
                return component;

            if (!(component instanceof javax.swing.tree.DefaultTreeCellRenderer))
                component.setBackground(rowColors[row&1]);
            else
                ((javax.swing.tree.DefaultTreeCellRenderer)component).
                        setBackgroundNonSelectionColor(rowColors[row&1]);

            return component;
        }

        public java.awt.Component getTreeCellEditorComponent(
            javax.swing.JTree tree, Object value,
            boolean selected, boolean expanded,
            boolean leaf, int row )
        {
            final java.awt.Component component =
                ed.getTreeCellEditorComponent(
                tree, value, selected, expanded, leaf, row);

            if (!selected && drawStripes)
                component.setBackground(rowColors[row&1]);

            return component;
        }

        public void addCellEditorListener(javax.swing.event.CellEditorListener listener)
        {
            ed.addCellEditorListener(listener);
        }

        public void cancelCellEditing()
        {
            ed.cancelCellEditing();
        }

        public Object getCellEditorValue()
        {
            return ed.getCellEditorValue();
        }

        public boolean isCellEditable(java.util.EventObject anEvent)
        {
            return ed.isCellEditable(anEvent);
        }

        public void removeCellEditorListener(javax.swing.event.CellEditorListener listener)
        {
            ed.removeCellEditorListener(listener);
        }

        public boolean shouldSelectCell(java.util.EventObject anEvent)
        {
            return ed.shouldSelectCell(anEvent);
        }

        public boolean stopCellEditing()
        {
            return ed.stopCellEditing();
        }

    }

    private RendererEditorWrapper wrapper = null;

    public javax.swing.tree.TreeCellRenderer getCellRenderer()
    {
        final javax.swing.tree.TreeCellRenderer ren = super.getCellRenderer();
        if (ren == null)
            return null;
        if (wrapper == null)
            wrapper = new RendererEditorWrapper();
        wrapper.ren = ren;
        return wrapper;
    }

    public javax.swing.tree.TreeCellEditor getCellEditor()
    {
        final javax.swing.tree.TreeCellEditor ed = super.getCellEditor();
        if (ed == null)
            return null;
        if (wrapper == null)
            wrapper = new RendererEditorWrapper();
        wrapper.ed = ed;
        return wrapper;
    }

    private void updateZebraColors()
    {
        if ((rowColors[0] = getBackground()) == null)
        {
            rowColors[0] = rowColors[1] = Color.white;
            return;
        }
        Color sel = UIManager.getColor("Tree.selectionBackground");
        if (sel == null)
            sel = SystemColor.textHighlight;
        if (sel == null)
        {
            rowColors[1] = rowColors[0];
            return;
        }

        final float[] bgHSB = Color.RGBtoHSB(
                rowColors[0].getRed( ), rowColors[0].getGreen( ),
                rowColors[0].getBlue( ), null );
        final float[] selHSB  = Color.RGBtoHSB(
                sel.getRed(), sel.getGreen(), sel.getBlue(), null);
        rowColors[1] = Color.getHSBColor(
                (selHSB[1] == 0.0 || selHSB[2] == 0.0) ? bgHSB[0] : selHSB[0],
                0.1f * selHSB[1] + 0.9f * bgHSB[1],
                bgHSB[2] + ((bgHSB[2]<0.5f) ? 0.05f : -0.05f));
    }

}
