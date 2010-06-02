package org.chartsy.main.favorites;

import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Viorel
 */
public class LeafNode extends AbstractNode
{

    private Leaf leaf;

    public LeafNode(Leaf leaf)
    {
        super(Children.LEAF, Lookups.fixed(new Object[] {leaf}));
        this.leaf = leaf;
        setDisplayName(leaf.getDisplayName());
    }

    public boolean canCut()
    { return true; }

    public boolean canDestroy()
    { return true; }

    public Action[] getActions(boolean bln)
    {
        return new Action[]
        {
            //SystemAction.get(CopyAction.class),
            //SystemAction.get(CutAction.class),
            //null,
            //SystemAction.get(DeleteAction.class)
        };
    }

    public String getHtmlDisplayName()
    {
        return leaf.getDisplayName() + " " + leaf.getValues().toString() + " " + leaf.getChange().toString() + " " + leaf.getPercent().toString();
    }

}
