package org.chartsy.main.favorites;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Viorel
 */
public class FolderNode extends AbstractNode
{

    public FolderNode(Folder folder)
    {
        super(new LeafChildren(folder), Lookups.singleton(folder));
        setDisplayName(folder.getFolderName());
        // set icon
    }

    public PasteType getDropType(Transferable t, final int action, int index)
    {
        final Node dropNode = NodeTransfer.node(t, DnDConstants.ACTION_COPY_OR_MOVE + NodeTransfer.CLIPBOARD_CUT);
        if (null != dropNode)
        {
            final Leaf leaf = (Leaf) dropNode.getLookup().lookup(Leaf.class);
            if (null != leaf && !this.equals(dropNode.getParentNode()))
            {
                return new PasteType()
                {
                    public Transferable paste() throws IOException
                    {
                        getChildren().add(new Node[] {});
                        if ((action & DnDConstants.ACTION_MOVE) != 0)
                        {
                            dropNode.getParentNode().getChildren().remove(new Node[] {dropNode});
                        }
                        return null;
                    }
                };
            }
        }
        return null;
    }
    
    public Cookie getCookie(Class clazz)
    {
        Children ch = getChildren();
        if (clazz.isInstance(ch))
            return (Cookie) ch;
        return super.getCookie(clazz);
    }

    protected void createPasteTypes(Transferable t, List s)
    {
        super.createPasteTypes(t, s);
        PasteType paste = getDropType(t, DnDConstants.ACTION_COPY, -1);
        if (null != paste)
            s.add(paste);
    }

    /*public Action[] getActions(boolean context) {
        return new Action[] {
            //SystemAction.get(NewAction.class),
            //SystemAction.get(PasteAction.class)
        };
    }*/

    public boolean canDestroy() 
    { return true; }

}
