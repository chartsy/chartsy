package org.chartsy.favorites.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;
import org.openide.explorer.view.TreeTableView;

/**
 *
 * @author Viorel
 */
public class FavoritesView extends TreeTableView
{

    public Color rowColors[] = new Color[2];
    private boolean drawStripes = true;
    private RendererWrapper wrapper = null;

    public FavoritesView()
    {
        setRootVisible(false);
        JTree jTree = new JTree();
        final javax.swing.tree.TreeCellRenderer ren = jTree.getCellRenderer();
        if (ren != null)
        {
            if (wrapper == null)
                wrapper = new RendererWrapper();
            wrapper.ren = ren;
            tree.setCellRenderer(wrapper);
        }
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        updateZebraColors();
        super.paintComponent(g);
    }


    public void setDefaultActionProcessor(final ActionListener action)
    {
        setDefaultActionAllowed(false);
        tree.addMouseListener(new MouseAdapter()
        {
            public @Override void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    action.actionPerformed(null);
                }
            }
        });
        treeTable.registerKeyboardAction(action,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                JComponent.WHEN_FOCUSED);
    }

    public class RendererWrapper implements TreeCellRenderer
    {

        TreeCellRenderer ren = null;

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
        {
            Component component =
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

    }

    private void updateZebraColors()
    {
        if ((rowColors[0] = tree.getBackground()) == null)
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
