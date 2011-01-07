package org.chartsy.chatsy.chat.component;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class JiveTreeNode extends DefaultMutableTreeNode implements Transferable
{

    private Icon closedImage = null;
    private Icon openImage = null;
    public static final DataFlavor[] DATA_FLAVORS = {new DataFlavor(JiveTreeNode.class, "JiveTreeNodeFlavor")};
    private Object associatedObject;

    public JiveTreeNode(TreeFolder folder)
	{
        super(folder.getDisplayName(), true);
        closedImage = null;
        openImage = null;
        associatedObject = folder;
    }

    public JiveTreeNode(String name, boolean allowsChildren)
	{
        super(name, allowsChildren);
        if (allowsChildren)
		{
            closedImage = null;
            openImage = null;
        }
    }

    public JiveTreeNode(Object o, boolean allowsChildren)
	{
        super(o, allowsChildren);
    }

    public JiveTreeNode(TreeItem item)
	{
        super(item.getDisplayName(), false);
        associatedObject = item;
    }

    public JiveTreeNode(TreeFolder folder, Icon img)
	{
        this(folder);
        closedImage = img;
    }

    public JiveTreeNode(TreeItem item, Icon img)
	{
        this(item);
        closedImage = img;
    }

    public JiveTreeNode(String userobject)
	{
        super(userobject);
    }

    public JiveTreeNode(String userObject, boolean allowChildren, Icon icon)
	{
        super(userObject, allowChildren);
        closedImage = icon;
        openImage = icon;
    }

    public Icon getIcon()
	{
        return closedImage;
    }

    public Icon getOpenIcon()
	{
        return openImage;
    }

    public Icon getClosedIcon()
	{
        return closedImage;
    }

    public void setIcon(Icon icon)
	{
        closedImage = icon;
    }

    public Object getAssociatedObject()
	{
        return associatedObject;
    }

    public void setAssociatedObject(Object o)
	{
        this.associatedObject = o;
    }

    public final boolean hasParent(String parentName)
	{
        JiveTreeNode parent = (JiveTreeNode)getParent();
        while (true)
		{
            if (parent.getAssociatedObject() == null)
                break;
            final TreeFolder folder = (TreeFolder)parent.getAssociatedObject();
            if (folder.getDisplayName().equals(parentName))
                return true;
            parent = (JiveTreeNode)parent.getParent();
        }
        return false;
    }

    public DataFlavor[] getTransferDataFlavors()
	{
        return DATA_FLAVORS;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor)
	{
        return flavor == DATA_FLAVORS[0];
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException
	{
        if (this.isDataFlavorSupported(flavor))
            return this;
        throw new UnsupportedFlavorException(flavor);
    }

}



