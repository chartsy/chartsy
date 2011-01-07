package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.packet.Presence;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

public class RosterNode extends DefaultMutableTreeNode
{
	
	private String name;
    private boolean isGroup;

    private Icon openIcon;
    private Icon closedIcon;

    private Presence presence;
    private String fullJID;

    public RosterNode()
	{
        super("root");
    }

    public RosterNode(String name, boolean isGroup)
	{
        super(name, true);
        this.name = name;
        this.isGroup = isGroup;
        if (isGroup)
		{
            openIcon = null;
            closedIcon = null;
        }
    }

    public Object getUserObject()
	{
        return name + " " + getChildCount();
    }

    public RosterNode(String name, String fullJID)
	{
        super(name, false);
        this.name = name;
        this.fullJID = fullJID;
    }

    public Icon getIcon()
	{
        return closedIcon;
    }

    public Icon getOpenIcon()
	{
        return openIcon;
    }

    public Icon getClosedIcon()
	{
        return closedIcon;
    }

    public void setOpenIcon(Icon icon)
	{
        openIcon = icon;
    }

    public void setClosedIcon(Icon icon)
	{
        closedIcon = icon;
    }

    public boolean isContact()
	{
        return !isGroup;
    }

    public boolean isGroup()
	{
        return isGroup;
    }

    public Presence getPresence()
	{
        return presence;
    }

    public void setPresence(Presence presence)
	{
        this.presence = presence;
    }

    public String getFullJID()
	{
        return fullJID;
    }

    public void setFullJID(String fullJID)
	{
        this.fullJID = fullJID;
    }

    public String getName()
	{
        return name;
    }
	
}
