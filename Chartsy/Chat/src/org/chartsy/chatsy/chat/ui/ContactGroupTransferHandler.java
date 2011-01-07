package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.chartsy.chatsy.chat.ChatsyManager;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ContactGroupTransferHandler extends TransferHandler
{
	
	private static final DataFlavor flavors[] = {DataFlavor.imageFlavor, DataFlavor.javaFileListFlavor};

    public int getSourceActions(JComponent c)
	{
        return TransferHandler.MOVE;
    }

    public boolean canImport(JComponent comp, DataFlavor flavor[])
	{
        return false;
    }

    protected void exportDone(JComponent c, Transferable data, int action)
	{
    }

    public Transferable createTransferable(JComponent comp)
	{
        if (comp instanceof JList)
		{
            JList list = (JList)comp;
            ContactItem source = (ContactItem)list.getSelectedValue();
            return new ContactItemTransferable(source);
        }
        return null;
    }

    public boolean importData(JComponent comp, Transferable t)
	{
        return false;
    }

    public class ContactItemTransferable implements Transferable
	{

        private ContactItem item;

        public ContactItemTransferable(ContactItem item)
		{
            this.item = item;
        }

        public DataFlavor[] getTransferDataFlavors()
		{
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor)
		{
            return DataFlavor.imageFlavor.equals(flavor);
        }

        public Object getTransferData(DataFlavor flavor) 
			throws UnsupportedFlavorException, IOException
		{
            if (!DataFlavor.imageFlavor.equals(flavor))
                throw new UnsupportedFlavorException(flavor);
            return item;
        }
    }

    private ContactGroup getContactGroup(String groupName)
	{
        ContactList contactList = ChatsyManager.getWorkspace().getContactList();
        return contactList.getContactGroup(groupName);
    }

    public boolean removeContactItem(ContactGroup contactGroup, ContactItem item)
	{
        if (contactGroup.isUnfiledGroup())
		{
            contactGroup.removeContactItem(item);
            contactGroup.fireContactGroupUpdated();
            return true;
        }

        Roster roster = ChatsyManager.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(item.getJID());
        RosterGroup rosterGroup = null;

        for (RosterGroup group : roster.getGroups())
		{
            if (group.getName().equals(contactGroup.getGroupName()))
			{
                try
				{
                    rosterGroup = group;
                    group.removeEntry(entry);
                }
                catch (XMPPException e)
				{
                    return false;
                }
            }
        }

        if (rosterGroup == null)
            return false;

        if (!rosterGroup.contains(entry))
		{
            contactGroup.removeContactItem(item);
            contactGroup.fireContactGroupUpdated();
            return true;
        }

        return false;
    }
}


