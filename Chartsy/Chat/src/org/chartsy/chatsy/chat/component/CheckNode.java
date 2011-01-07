package org.chartsy.chatsy.chat.component;

import java.util.Enumeration;
import javax.swing.Icon;

public class CheckNode extends JiveTreeNode
{

	public static final int SINGLE_SELECTION = 0;
    public static final int DIG_IN_SELECTION = 4;

    private int selectionMode;
    private boolean isSelected;
    private String fullName;
    private Object associatedObject;

    public CheckNode()
	{
        this(null);
    }

    public CheckNode(Object userObject)
	{
        this(userObject, true, false);
    }

    public CheckNode(Object userObject, boolean allowsChildren, boolean isSelected)
	{
        super(userObject, allowsChildren);
        this.isSelected = isSelected;
        setSelectionMode(DIG_IN_SELECTION);
    }

    public CheckNode(String userObject, boolean allowsChildren, Icon icon)
	{
        super(userObject, allowsChildren, icon);
        setSelectionMode(DIG_IN_SELECTION);
    }

    public CheckNode(Object userObject, boolean allowsChildren, boolean isSelected, String name)
	{
        super(userObject, allowsChildren);
        this.isSelected = isSelected;
        setSelectionMode(DIG_IN_SELECTION);
        fullName = name;
    }

    public String getFullName()
	{
        return fullName;
    }

    public void setSelectionMode(int mode)
	{
        selectionMode = mode;
    }

    public int getSelectionMode()
	{
        return selectionMode;
    }

    public void setSelected(boolean isSelected)
	{
        this.isSelected = isSelected;

        if (selectionMode == DIG_IN_SELECTION
                && children != null)
		{
            Enumeration nodeEnum = children.elements();
            while (nodeEnum.hasMoreElements())
			{
                CheckNode node = (CheckNode)nodeEnum.nextElement();
                node.setSelected(isSelected);
            }
        }
    }

    public boolean isSelected()
	{
        return isSelected;
    }

    public Object getAssociatedObject()
	{
        return associatedObject;
    }

    public void setAssociatedObject(Object associatedObject)
	{
        this.associatedObject = associatedObject;
    }
	
}


