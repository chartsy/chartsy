package org.chartsy.chatsy.chat.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.util.StringUtils;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.PresenceManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.Workspace;
import org.chartsy.chatsy.chat.component.tabbedPane.AbstractTab;
import org.chartsy.chatsy.chat.plugin.Plugin;
import org.chartsy.chatsy.chat.util.TaskEngine;
import org.chartsy.chatsy.chatimpl.profile.VCardManager;

public final class ContactList extends AbstractTab
	implements ContactGroupListener, Plugin, RosterListener, ConnectionListener
{

	private static final Logger LOG = Logger.getLogger(ContactList.class.getName());

	private JPanel mainPanel = new JPanel();
    private JScrollPane contactListScrollPane;
	
    private final List<String> groupList = new ArrayList<String>();
	private final List<ContactItem> selectedContacts = new ArrayList<ContactItem>();

    private List<Presence> initialPresences = new ArrayList<Presence>();
    private final Timer presenceTimer = new Timer();
    private final List<ContactListListener> contactListListeners = new ArrayList<ContactListListener>();

    private Workspace workspace;
    public static KeyEvent activeKeyEvent;

    public ContactList()
	{
		setName("Contacts");
		if (!initialized)
		{
			buildContent();
			initialized = true;
		}
    }

	@Override protected void buildContent()
	{
        mainPanel.setLayout(new LayoutManager()
		{
			public void addLayoutComponent(String name, Component comp)
			{
			}
			public void removeLayoutComponent(Component comp)
			{
			}
			public Dimension preferredLayoutSize(Container parent)
			{
				return new Dimension(0, 0);
			}
			public Dimension minimumLayoutSize(Container parent)
			{
				return new Dimension(0, 0);
			}
			public void layoutContainer(Container parent)
			{
				final Component[] components = parent.getComponents();
				if (components.length == 0)
					return;

				final Insets insets = parent.getInsets();
				final int width = parent.getWidth() - insets.left - insets.right;
				final int x = insets.left;
				int y = insets.top;

				for (int i = 0; i < components.length; i++)
				{
					int height = ((ContactGroup)components[i]).getPanelHeight();
					components[i].setBounds(x, y, width, height);
					y += height;
				}
			}
		});
        mainPanel.setOpaque(false);

        contactListScrollPane = new JScrollPane(mainPanel);
		contactListScrollPane.getViewport().setOpaque(true);
		contactListScrollPane.getViewport().setBackground(Color.WHITE);
        contactListScrollPane.setAutoscrolls(true);
        contactListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        contactListScrollPane.getVerticalScrollBar().setBlockIncrement(50);
        contactListScrollPane.getVerticalScrollBar().setUnitIncrement(20);
		add(contactListScrollPane, BorderLayout.CENTER);

        workspace = ChatsyManager.getWorkspace();
        ChatsyManager.getConnection().addConnectionListener(this);

		addKeyListener(new KeyAdapter()
		{
			@Override public void keyPressed(KeyEvent e)
			{
				activeKeyEvent = e;
			}
			@Override public void keyReleased(KeyEvent e)
			{
				activeKeyEvent = null;
			}
		});
	}

	private synchronized void updateUserPresence(Presence presence)
		throws Exception
	{
        if (presence.getError() != null)
            return;

        final Roster roster = ChatsyManager.getConnection().getRoster();
        final String bareJID = StringUtils.parseBareAddress(presence.getFrom());
        RosterEntry entry = roster.getEntry(bareJID);
        boolean isPending = entry != null 
			&& (entry.getType() == RosterPacket.ItemType.none
			|| entry.getType() == RosterPacket.ItemType.from)
            && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == entry.getStatus();

		if (presence.getType() == Presence.Type.available
			|| (presence.getFrom().indexOf("workgroup.") != -1))
			changeOfflineToOnline(entry, presence);
		else if (presence.getType() == Presence.Type.available)
            updateContactItemsPresence(presence, entry, bareJID);
		else if (presence.getType() == Presence.Type.unavailable
			&& !isPending)
		{
			Presence rosterPresence = PresenceManager.getPresence(bareJID);
			if (!rosterPresence.isAvailable())
                moveToOfflineGroup(presence, bareJID);
            else
                updateContactItemsPresence(rosterPresence, entry, bareJID);
		}
    }

    private void updateContactItemsPresence(Presence presence, RosterEntry entry, String bareJID)
	{
        for (ContactGroup group : getContactGroups())
		{
            ContactItem item = group.getContactItemByJID(bareJID);
            if (item != null)
			{
                item.setPresence(presence);
                group.fireContactGroupUpdated();
            }
        }
    }

    private void moveToOfflineGroup(final Presence presence, final String bareJID)
	{
        for (ContactGroup grpItem : getContactGroups())
		{
            final ContactGroup group = grpItem;
            final ContactItem item = group.getContactItemByJID(bareJID);
            if (item != null)
			{
                int numberOfMillisecondsInTheFuture = 3000;
                Date timeToRun = new Date(System.currentTimeMillis() + numberOfMillisecondsInTheFuture);

                if (item.getPresence().isAvailable())
				{
                    item.showUserGoingOfflineOnline();
                    item.setIcon(PresenceManager.getIconFromPresence(presence));
                    group.fireContactGroupUpdated();

                    final Timer offlineTimer = new Timer();
                    offlineTimer.schedule(new TimerTask()
					{
                        public void run()
						{
                            Presence userPresence = PresenceManager.getPresence(bareJID);
                            if (userPresence.isAvailable()) 
                                return;
                            item.setPresence(presence);
							group.fireContactGroupUpdated();
                            checkGroup(group);
                        }
                    }, timeToRun);
                }
            }
        }
    }

    private void changeOfflineToOnline(final RosterEntry entry, Presence presence)
	{
		if (entry != null)
        for (RosterGroup rosterGroup : entry.getGroups())
		{
            ContactGroup contactGroup = getContactGroup(rosterGroup.getName());
            if (contactGroup == null)
                contactGroup = addContactGroup(rosterGroup.getName());

            if (contactGroup != null) 
			{
				ContactItem contactItem = null;
                if (contactGroup.getContactItemByJID(entry.getUser()) == null)
				{
                    contactItem = new ContactItem(entry.getName(), null, entry.getUser());
                    contactGroup.addContactItem(contactItem);
                }
                else if (contactGroup.getContactItemByJID(entry.getUser()) != null)
				{
                    contactItem = contactGroup.getContactItemByJID(entry.getUser());
                }

                try
				{
                    contactItem.setPresence(presence);
                }
                catch (NullPointerException e)
				{
                }
				
                contactItem.setOffline(false);
                toggleGroupVisibility(contactGroup.getGroupName(), true);

                contactItem.updateAvatarInSideIcon();
                contactItem.showUserComingOnline();
                contactGroup.fireContactGroupUpdated();

                int numberOfMillisecondsInTheFuture = 5000;
                Date timeToRun = new Date(System.currentTimeMillis() + numberOfMillisecondsInTheFuture);
                Timer timer = new Timer();

                final ContactItem staticItem = contactItem;
                final ContactGroup staticGroup = contactGroup;
                timer.schedule(new TimerTask()
				{
                    public void run()
					{
                        staticItem.updatePresenceIcon(staticItem.getPresence());
                        staticGroup.fireContactGroupUpdated();
                    }
                }, timeToRun);
            }
        }
    }

    private void buildContactList()
	{
        XMPPConnection con = ChatsyManager.getConnection();
        final Roster roster = con.getRoster();
        roster.addRosterListener(this);

        for (RosterGroup group : roster.getGroups()) 
            addContactGroup(group.getName());

        for (RosterGroup group : roster.getGroups())
		{
			if (group.getName() != null || !group.getName().isEmpty())
			{
	            ContactGroup contactGroup = getContactGroup(group.getName());
				if (contactGroup != null)
				{
					for (RosterEntry entry : group.getEntries())
					{
						ContactItem contactItem = new ContactItem(entry.getName(), null, entry.getUser());
						contactGroup.addContactItem(contactItem);
						contactGroup.setVisible(true);
						updateContactItemsPresence(PresenceManager.getPresence(contactItem.getJID()), entry, contactItem.getJID());
					}
				}
            }
        }

        for (RosterEntry entry : roster.getUnfiledEntries())
		{
            ContactItem contactItem = new ContactItem(entry.getName(), null, entry.getUser());
            moveToOffline(contactItem);
        }
    }

    public void entriesAdded(final Collection<String> addresses)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            public void run()
			{
                Roster roster = ChatsyManager.getConnection().getRoster();
                for (String jid : addresses)
				{
                    RosterEntry entry = roster.getEntry(jid);
                    addUser(entry);
                }
            }
        });
    }

    private void addUser(RosterEntry entry)
	{
        ContactItem newContactItem = new ContactItem(entry.getName(), null, entry.getUser());
        if (entry.getType() == RosterPacket.ItemType.none 
			|| entry.getType() == RosterPacket.ItemType.from)
		{
            for (RosterGroup group : entry.getGroups())
			{
                ContactGroup contactGroup = getContactGroup(group.getName());
                if (contactGroup == null) 
                    contactGroup = addContactGroup(group.getName());

                boolean isPending = entry.getType() == RosterPacket.ItemType.none
					|| entry.getType() == RosterPacket.ItemType.from
                    && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == entry.getStatus();
                if (isPending) 
                    contactGroup.setVisible(true);
                contactGroup.addContactItem(newContactItem);
            }
            return;
        }
        else
		{
            moveToOffline(newContactItem);
        }

        Presence presence = ChatsyManager.getConnection().getRoster().getPresence(entry.getUser());
        try
		{
            updateUserPresence(presence);
        }
        catch (Exception e)
		{
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public void entriesUpdated(final Collection<String> addresses)
	{
        handleEntriesUpdated(addresses);
    }

    public void entriesDeleted(final Collection<String> addresses)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            public void run()
			{
                for (String jid : addresses) 
                    removeContactItem(jid);
            }
        });

    }

    private synchronized void handleEntriesUpdated(final Collection<String> addresses)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            public void run()
			{
                Roster roster = ChatsyManager.getConnection().getRoster();

                Iterator<String> jids = addresses.iterator();
                while (jids.hasNext())
				{
                    String jid = jids.next();
                    RosterEntry rosterEntry = roster.getEntry(jid);
                    if (rosterEntry != null)
					{
                        for (RosterGroup group : rosterEntry.getGroups())
						{
                            if (getContactGroup(group.getName()) == null)
							{
                                ContactGroup contactGroup = addContactGroup(group.getName());
                                contactGroup.setVisible(false);
                                contactGroup = getContactGroup(group.getName());
                                ContactItem contactItem = new ContactItem(rosterEntry.getName(), null, rosterEntry.getUser());
                                contactGroup.addContactItem(contactItem);
                                Presence presence = PresenceManager.getPresence(jid);
                                contactItem.setPresence(presence);
                                if (presence.isAvailable()) 
                                    contactGroup.setVisible(true);
                            }
                            else
							{
                                ContactGroup contactGroup = getContactGroup(group.getName());
                                ContactItem item = contactGroup.getContactItemByJID(jid);
								
                                if (item == null)
								{
                                    item = new ContactItem(rosterEntry.getName(), null, rosterEntry.getUser());
                                    Presence presence = PresenceManager.getPresence(jid);
                                    item.setPresence(presence);
                                    if (presence.isAvailable())
									{
                                        contactGroup.addContactItem(item);
                                        contactGroup.fireContactGroupUpdated();
                                    }
                                    else
									{
                                        moveToOffline(item);
                                        contactGroup.fireContactGroupUpdated();
                                    }
                                }
                                else
								{
                                    RosterEntry entry = roster.getEntry(jid);
                                    Presence presence = PresenceManager.getPresence(jid);
                                    item.setPresence(presence);
                                    try
									{
                                        updateUserPresence(presence);
                                    }
                                    catch (Exception e)
									{
                                        LOG.log(Level.SEVERE, "", e);
                                    }

                                    if (entry != null 
										&& (entry.getType() == RosterPacket.ItemType.none
										|| entry.getType() == RosterPacket.ItemType.from)
                                        && RosterPacket.ItemStatus.SUBSCRIPTION_PENDING == entry.getStatus()) 
                                        contactGroup.setVisible(true);
                                    contactGroup.fireContactGroupUpdated();
                                }
                            }
                        }

                        final Set<String> userGroupSet = new HashSet<String>();
                        jids = addresses.iterator();
                        while (jids.hasNext())
						{
                            jid = (String)jids.next();
                            rosterEntry = roster.getEntry(jid);

                            boolean unfiled = true;
                            for (RosterGroup g : rosterEntry.getGroups())
							{
                                userGroupSet.add(g.getName());
                                unfiled = false;
                            }

                            for (ContactGroup group : new ArrayList<ContactGroup>(getContactGroups()))
							{
                                ContactItem itemFound = group.getContactItemByJID(jid);
                                if (itemFound != null && !unfiled) 
                                    if (!userGroupSet.contains(group.getGroupName())) 
                                        removeContactGroup(group);
                            }
                        }
                    }
                }
            }
        });
    }

    public void presenceChanged(Presence presence)
	{
    }

    public ContactItem getContactItemByJID(String jid)
	{
        for (ContactGroup group : getContactGroups())
		{
			String JID = StringUtils.parseBareAddress(jid);
            ContactItem item = group.getContactItemByJID(JID);
            if (item != null) 
                return item;
        }
        return null;
    }

    public Collection<ContactItem> getContactItemsByJID(String jid)
	{
        final List<ContactItem> list = new ArrayList<ContactItem>();
        for (ContactGroup group : getContactGroups())
		{
            ContactItem item = group.getContactItemByJID(StringUtils.parseBareAddress(jid));
            if (item != null) 
                list.add(item);
        }

        for (ContactGroup group : getContactGroups())
		{
            for (ContactItem offlineItem : group.getOfflineContacts()) 
                if (offlineItem != null
					&& offlineItem.getJID().equalsIgnoreCase(StringUtils.parseBareAddress(jid)))
                    if (!list.contains(offlineItem)) 
                        list.add(offlineItem);
        }
		
        return list;
    }

    public void setIconFor(String jid, Icon icon)
	{
        for (ContactGroup group : getContactGroups())
		{
            ContactItem item = group.getContactItemByJID(StringUtils.parseBareAddress(jid));
            if (item != null)
			{
                item.setIcon(icon);
                group.fireContactGroupUpdated();
            }
        }
    }

    public void useDefaults(String jid)
	{
        for (ContactGroup group : getContactGroups())
		{
            ContactItem item = group.getContactItemByJID(StringUtils.parseBareAddress(jid));
            if (item != null)
			{
                item.updatePresenceIcon(item.getPresence());
                group.fireContactGroupUpdated();
            }
        }
    }

    public ContactItem getContactItemByDisplayName(String displayName)
	{
        for (ContactGroup contactGroup : getContactGroups())
		{
            ContactItem item = contactGroup.getContactItemByDisplayName(displayName);
            if (item != null) 
                return item;
        }
        return null;
    }

    private void addContactGroup(ContactGroup group)
	{
		Roster roster = ChatsyManager.getConnection().getRoster();
		RosterGroup rosterGroup = roster.getGroup(group.getGroupName());
		if (rosterGroup == null)
			rosterGroup = roster.createGroup(group.getGroupName());
		
		groupList.add(group.getName());
		mainPanel.add(group);
		
        group.addContactGroupListener(this);
        fireContactGroupAdded(group);
		group.setExpanded(false);
    }

    private ContactGroup addContactGroup(String groupName)
	{
        StringTokenizer tkn = new StringTokenizer(groupName, "::");
		StringBuilder buf = new StringBuilder();
		ContactGroup newContactGroup = null;
		
		while (tkn.hasMoreTokens())
		{
			String group = tkn.nextToken();
			buf.append(group);
			if (tkn.hasMoreTokens())
                buf.append("::");

			String name = buf.toString();
			if (name.endsWith("::"))
                name = name.substring(0, name.length() - 2);

			newContactGroup = getContactGroup(name);
			if (newContactGroup == null)
			{
				newContactGroup = new ContactGroup(group);
				String realGroupName = buf.toString();
				if (realGroupName.endsWith("::"))
                    realGroupName = realGroupName.substring(0, realGroupName.length() - 2);
				newContactGroup.setGroupName(realGroupName);
				newContactGroup.addContactGroupListener(this);
				fireContactGroupAdded(newContactGroup);
				newContactGroup.setExpanded(false);
			}
		}

		if (newContactGroup != null)
		{
			final List<String> tempList = new ArrayList<String>();
			final Component[] comps = mainPanel.getComponents();
			for (Component c : comps)
			{
				if (c instanceof ContactGroup)
					tempList.add(c.getName());
			}
			tempList.add(newContactGroup.getName());
			groupList.add(newContactGroup.getName());
			Collections.sort(tempList);
			int loc = tempList.indexOf(newContactGroup.getName());
			try
			{
				mainPanel.add(newContactGroup, loc);
			}
			catch (Exception e)
			{
				LOG.log(Level.SEVERE, "", e);
			}
		}
		
        return getContactGroup(groupName);
    }

    private void removeContactGroup(ContactGroup contactGroup)
	{
        contactGroup.removeContactGroupListener(this);
        groupList.remove(contactGroup.getName());
        mainPanel.remove(contactGroup);

        contactListScrollPane.validate();
        mainPanel.invalidate();
        mainPanel.repaint();
        fireContactGroupRemoved(contactGroup);
    }

    public ContactGroup getContactGroup(String groupName)
	{
        ContactGroup cGroup = null;
        for (String group : groupList)
		{
            if (group.equals(groupName))
			{
                cGroup = (ContactGroup) mainPanel.getComponent(groupList.indexOf(groupName));
                break;
            }
        }
        return cGroup;
    }

    public void toggleGroupVisibility(String groupName, boolean visible)
	{
        StringTokenizer tkn = new StringTokenizer(groupName, "::");
        while (tkn.hasMoreTokens())
		{
            String group = tkn.nextToken();
            ContactGroup contactGroup = getContactGroup(group);
            if (contactGroup != null) 
                contactGroup.setVisible(visible);
        }

        ContactGroup group = getContactGroup(groupName);
        if (group != null) 
            group.setVisible(true);
    }

    private void removeContactFromGroup(ContactItem item)
	{
        String groupName = item.getGroupName();
        ContactGroup contactGroup = getContactGroup(groupName);
        Roster roster = ChatsyManager.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(item.getJID());
        if (entry != null && contactGroup != null)
		{
            try
			{
                RosterGroup rosterGroup = roster.getGroup(groupName);
                if (rosterGroup != null)
				{
                    RosterEntry rosterEntry = rosterGroup.getEntry(entry.getUser());
                    if (rosterEntry != null) 
                        rosterGroup.removeEntry(rosterEntry);
                }
                contactGroup.removeContactItem(contactGroup.getContactItemByJID(item.getJID()));
                checkGroup(contactGroup);
            }
            catch (Exception e)
			{
				LOG.log(Level.SEVERE, "Error removing user from contact list.", e);
            }
        }
    }

    private void removeContactFromRoster(ContactItem item)
	{
        Roster roster = ChatsyManager.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(item.getJID());
        if (entry != null)
		{
            try
			{
                roster.removeEntry(entry);
            }
            catch (XMPPException e) 
			{
				LOG.log(Level.SEVERE, "Unable to remove roster entry.", e);
            }
        }
    }

    private void removeContactItem(String jid)
	{
        for (ContactGroup group : new ArrayList<ContactGroup>(getContactGroups()))
		{
            ContactItem item = group.getContactItemByJID(jid);
            group.removeOfflineContactItem(jid);
            if (item != null)
			{
                group.removeContactItem(item);
                checkGroup(group);
            }
        }
    }

    public void contactItemClicked(ContactItem item)
	{
		if (activeKeyEvent == null || ((activeKeyEvent.getModifiers() & KeyEvent.CTRL_MASK) == 0))
		{
			clearSelectionList(item);
		}
		else
		{
			if (!selectedContacts.contains(item))
			{
				selectedContacts.add(item);
				item.setSelected(true);
			}
			else
			{
				selectedContacts.remove(item);
				item.setSelected(false);
			}
		}
		
        fireContactItemClicked(item);
    }

    public void contactItemDoubleClicked(ContactItem item)
	{
        ChatManager chatManager = ChatsyManager.getChatManager();
        boolean handled = chatManager.fireContactItemDoubleClicked(item);
        if (!handled) 
            chatManager.activateChat(item.getJID(), item.getDisplayName());
        fireContactItemDoubleClicked(item);
    }

    public void contactGroupPopup(MouseEvent e, final ContactGroup group)
	{
        final JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem;
		popup.add(menuItem = new JMenuItem(new AbstractAction("Add a Contact")
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				ChatsyManager.getWorkspace().getCommandPanel().addButtonClick();
			}
		}));

		popup.add(menuItem = new JMenuItem(new AbstractAction("Add a Contact Group") 
		{
			@Override public void actionPerformed(ActionEvent e) 
			{
				String groupName = JOptionPane.showInputDialog(getGUI(), "New Group Name:", "Add New Group", JOptionPane.QUESTION_MESSAGE);
				if (groupName == null && groupName.isEmpty())
					return;
				ContactGroup contactGroup = getContactGroup(groupName);
				if (contactGroup == null)
				{
					contactGroup = addContactGroup(groupName);
					contactGroup.setVisible(true);
					validateTree();
					repaint();
				}
			}
		}));

		popup.add(menuItem = new JMenuItem(new AbstractAction("Invite Group to Conference")
		{
			@Override public void actionPerformed(ActionEvent e) 
			{
				clearSelectionList(null);
				for (ContactItem item : group.getContactItems())
					if (!item.isOffline())
						selectedContacts.add(item);
				ChatsyManager.getWorkspace().getCommandPanel().chatButtonClick();
			}
		}));

		popup.addSeparator();

		popup.add(menuItem = new JMenuItem(new AbstractAction("Delete")
		{
			@Override public void actionPerformed(ActionEvent e) 
			{
				removeContactGroup(group);
			}
		}));

		popup.add(menuItem = new JMenuItem(new AbstractAction("Rename")
		{
			@Override public void actionPerformed(ActionEvent e) 
			{
				String newName = JOptionPane.showInputDialog(group, "New Group Name:", "Rename Group", JOptionPane.QUESTION_MESSAGE);
				if (newName == null && newName.isEmpty())
					return;
				String groupName = group.getGroupName();
				Roster roster = ChatsyManager.getConnection().getRoster();
				RosterGroup rosterGroup = roster.getGroup(groupName);
				if (rosterGroup != null)
				{
					rosterGroup.setName(newName);
					group.setGroupName(newName);
				}
			}
		}));
		
		popup.show(group, e.getX(), e.getY());
    }

    public void showPopup(MouseEvent e, final ContactItem item)
	{
		final JPopupMenu popup = new JPopupMenu();
		JMenu menu;
		JMenuItem menuItem;
		
		popup.add(menuItem = new JMenuItem(new AbstractAction("Start Chat")
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				ChatsyManager.getWorkspace().getCommandPanel().chatButtonClick();
			}
		}));

		popup.add(menuItem = new JMenuItem(new AbstractAction("View Profile")
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				VCardManager vCardManager = ChatsyManager.getVCardManager();
				String jid = item.getJID();
				vCardManager.viewProfile(jid, ChatsyManager.getWorkspace());
			}
		}));

		if (getContactGroups().size() > 1)
		{
			popup.add(menu = new JMenu("Move to"));
			ContactGroup group = getContactGroup(item.getGroupName());
			for (ContactGroup contactGroup : getContactGroups())
			{
				if (!contactGroup.getGroupName().equals(group.getGroupName()))
				menu.add(menuItem = new JMenuItem(new AbstractAction(contactGroup.getGroupName())
				{
					@Override public void actionPerformed(ActionEvent e)
					{
						String groupName = item.getGroupName();
						String newGroupName = (String) getValue(NAME);
						ContactGroup group = getContactGroup(groupName);
						ContactGroup newGroup = getContactGroup(newGroupName);

						if (group == null || newGroup == null)
							return;

						group.removeContactItem(item);
						newGroup.addContactItem(item);

						revalidate();
						repaint();
					}
				}));
			}
		}

		popup.addSeparator();

		popup.add(menuItem = new JMenuItem(new AbstractAction("Delete")
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				removeContactFromRoster(item);
				revalidate();
				repaint();
			}
		}));

		popup.show(item, e.getX(), e.getY());
    }

    public void showPopup(MouseEvent e, final Collection<ContactItem> items)
	{
		ContactGroup group = null;
		for (ContactItem item : items)
		{
            group = getContactGroup(item.getGroupName());
			if (group != null)
				break;
        }

		if (group == null)
			return;

		final JPopupMenu popup = new JPopupMenu();
		JMenu menu;
		JMenuItem menuItem;
		
		popup.add(menuItem = new JMenuItem(new AbstractAction("Start Conference")
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				ChatsyManager.getWorkspace().getCommandPanel().chatButtonClick();
			}
		}));

		if (getContactGroups().size() > 1)
		{
			popup.add(menu = new JMenu("Move to"));
			for (ContactGroup contactGroup : getContactGroups())
			{
				if (!contactGroup.getGroupName().equals(group.getGroupName()))
				menu.add(menuItem = new JMenuItem(new AbstractAction(contactGroup.getGroupName())
				{
					@Override public void actionPerformed(ActionEvent e)
					{
						for (ContactItem item : items)
						{
							String groupName = item.getGroupName();
							String newGroupName = (String) getValue(NAME);
							ContactGroup group = getContactGroup(groupName);
							ContactGroup newGroup = getContactGroup(newGroupName);

							if (group == null || newGroup == null)
								return;

							group.removeContactItem(item);
							newGroup.addContactItem(item);
						}

						revalidate();
						repaint();
					}
				}));
			}
		}

		popup.addSeparator();

		popup.add(menuItem = new JMenuItem(new AbstractAction("Delete")
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				for (ContactItem item : items)
					removeContactFromRoster(item);
				revalidate();
				repaint();
			}
		}));

		popup.show(group.getListPanel(), e.getX(), e.getY());
    }

    public void clearSelectionList(ContactItem contactItem)
	{
		if (!selectedContacts.isEmpty())
			for (ContactItem item : selectedContacts)
				item.setSelected(false);
		selectedContacts.clear();
		
		if (contactItem != null)
		{
			selectedContacts.add(contactItem);
			contactItem.setSelected(true);
		}
    }

    public void initialize()
	{
        ChatsyManager.getVCardManager();
        ChatsyManager.getMainWindow().getTopToolBar().setVisible(false);

        final Runnable sharedGroupLoader = new Runnable()
		{
            public void run()
			{
                SwingUtilities.invokeLater(new Runnable()
				{
                    public void run()
					{
                        loadContactList();
                    }
                });

            }
        };
        TaskEngine.getInstance().submit(sharedGroupLoader);
    }

    private void loadContactList()
	{
        buildContactList();
        showEmptyGroups(false);
        showOfflineUsers(false);
        addSubscriptionListener();
        ChatsyManager.getWorkspace().loadPlugins();
    }

    public void addSubscriptionListener()
	{
        PacketFilter packetFilter = new PacketTypeFilter(Presence.class);
        PacketListener subscribeListener = new PacketListener()
		{
            public void processPacket(Packet packet)
			{
                final Presence presence = (Presence)packet;
                if (presence.getType() == Presence.Type.subscribe)
				{
                    SwingUtilities.invokeLater(new Runnable()
					{
                        public void run()
						{
                            subscriptionRequest(presence.getFrom());
                        }
                    });
                }
                else if (presence.getType() == Presence.Type.unsubscribe)
				{
                    SwingUtilities.invokeLater(new Runnable()
					{
                        public void run()
						{
                            Roster roster = ChatsyManager.getConnection().getRoster();
                            RosterEntry entry = roster.getEntry(presence.getFrom());
                            if (entry != null)
							{
                                try
								{
                                    removeContactItem(presence.getFrom());
                                    roster.removeEntry(entry);
                                }
                                catch (XMPPException e)
								{
                                    Presence unsub = new Presence(Presence.Type.unsubscribed);
                                    unsub.setTo(presence.getFrom());
                                    ChatsyManager.getConnection().sendPacket(unsub);
									LOG.log(Level.SEVERE, "", e);
                                }
                            }
                        }
                    });
                }
                else if (presence.getType() == Presence.Type.subscribe)
				{
                    String jid = StringUtils.parseBareAddress(presence.getFrom());
                    ContactItem item = getContactItemByJID(jid);
                    if (item == null)
					{
                        final Roster roster = ChatsyManager.getConnection().getRoster();
                        RosterEntry entry = roster.getEntry(jid);
                        if (entry != null)
						{
                            item = new ContactItem(entry.getName(), null, jid);
                            moveToOffline(item);
                        }
                    }
                }
                else if (presence.getType() == Presence.Type.unsubscribed)
				{
                    SwingUtilities.invokeLater(new Runnable()
					{
                        public void run()
						{
                            Roster roster = ChatsyManager.getConnection().getRoster();
                            RosterEntry entry = roster.getEntry(presence.getFrom());
                            if (entry != null)
							{
                                try
								{
                                    removeContactItem(presence.getFrom());
                                    roster.removeEntry(entry);
                                }
                                catch (XMPPException e)
								{
                                    LOG.log(Level.SEVERE, "", e);
                                }
                            }
                            String jid = StringUtils.parseBareAddress(presence.getFrom());
                            removeContactItem(jid);
                        }
                    });
                }
                else
				{
                    try
					{
                        initialPresences.add(presence);
                    }
                    catch (Exception e)
					{
                        LOG.log(Level.SEVERE, "", e);
                    }
                    int numberOfMillisecondsInTheFuture = 1000;
                    presenceTimer.schedule(new TimerTask()
					{
                        public void run()
						{
                            SwingUtilities.invokeLater(new Runnable()
							{
                                public void run()
								{
                                    for (Presence userToUpdate : new ArrayList<Presence>(initialPresences))
									{
                                        initialPresences.remove(userToUpdate);
                                        try
										{
                                            updateUserPresence(userToUpdate);
                                        }
                                        catch (Exception e)
										{
                                            LOG.log(Level.SEVERE, "", e);
                                        }
                                    }
                                }
                            });
                        }
                    }, numberOfMillisecondsInTheFuture);
                }
            }
        };
        ChatsyManager.getConnection().addPacketListener(subscribeListener, packetFilter);
    }

    public void shutdown()
	{
    }

    public boolean canShutDown()
	{
        return true;
    }

    private void showEmptyGroups(boolean show)
	{
        for (ContactGroup group : getContactGroups())
			if (group.isEmpty())
				group.setVisible(show);
    }
    
    private void showOfflineUsers(boolean show)
	{
		for (ContactGroup group : getContactGroups())
		{
			for (ContactItem item : group.getContactItems())
				if (item.isOffline())
					item.setVisible(show);
		}
	}

    public static final Comparator<ContactGroup> GROUP_COMPARATOR = new Comparator<ContactGroup>()
	{
        public int compare(ContactGroup group1, ContactGroup group2)
		{
            if (group2.isOfflineGroup()) 
                return -1;
            return group1.getGroupName().trim().toLowerCase().compareTo(group2.getGroupName().trim().toLowerCase());
        }
    };

    public JPanel getMainPanel()
	{
        return mainPanel;
    }

    public List<ContactGroup> getContactGroups()
	{
        final List<ContactGroup> gList = new ArrayList<ContactGroup>();
		final Component[] components = mainPanel.getComponents();
		for (int i = 0; i < components.length; i++)
			if (components[i] instanceof ContactGroup)
				gList.add((ContactGroup)components[i]);
        return gList;
    }

    private void subscriptionRequest(final String jid)
	{
        final SubscriptionDialog subscriptionDialog = new SubscriptionDialog();
        subscriptionDialog.invoke(jid);
    }

    public JComponent getGUI()
	{
        return this;
    }

    public Collection<ContactItem> getSelectedUsers()
	{
        return selectedContacts;
    }

    private void checkGroup(ContactGroup group)
	{
        if (!group.hasAvailableContacts())
            group.setVisible(false);
    }

    public void contactItemAdded(ContactItem item)
	{
        fireContactItemAdded(item);
    }

    public void contactItemRemoved(ContactItem item)
	{
        fireContactItemRemoved(item);
    }

    public void addContactListListener(ContactListListener listener)
	{
        contactListListeners.add(listener);
    }

    public void removeContactListListener(ContactListListener listener)
	{
        contactListListeners.remove(listener);
    }

    public void fireContactItemAdded(ContactItem item)
	{
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) 
            contactListListener.contactItemAdded(item);
    }

    public void fireContactItemRemoved(ContactItem item)
	{
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) 
            contactListListener.contactItemRemoved(item);
    }

    public void fireContactGroupAdded(ContactGroup group)
	{
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) 
            contactListListener.contactGroupAdded(group);
    }

    public void fireContactGroupRemoved(ContactGroup group)
	{
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) 
            contactListListener.contactGroupRemoved(group);
    }

    public void fireContactItemClicked(ContactItem contactItem)
	{
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) 
            contactListListener.contactItemClicked(contactItem);
    }

    public void fireContactItemDoubleClicked(ContactItem contactItem)
	{
        for (ContactListListener contactListListener : new ArrayList<ContactListListener>(contactListListeners)) 
            contactListListener.contactItemDoubleClicked(contactItem);
    }


    public void uninstall()
	{
    }

    public void connectionClosed()
	{
        ChatsyManager.getMainWindow().setVisible(true);
        ChatsyManager.getNativeManager().flashWindowStopOnFocus(null);
        removeAllUsers();
    }

    private void reconnect()
	{
        ChatsyManager.getMainWindow().setVisible(true);
        removeAllUsers();
    }

    public void clientReconnected()
	{
        workspace.changeCardLayout(Workspace.WORKSPACE_PANE);
        buildContactList();

        final TimerTask updatePresence = new TimerTask()
		{
            public void run()
			{
                final Presence myPresence = ChatsyManager.getWorkspace().getStatusBar().getPresence();
                ChatsyManager.getSessionManager().changePresence(myPresence);

                XMPPConnection con = ChatsyManager.getConnection();
                final Roster roster = con.getRoster();
                for (RosterEntry entry : roster.getEntries())
				{
                    String user = entry.getUser();
                    Presence presence = roster.getPresence(user);
                    try
					{
                        updateUserPresence(presence);
                    }
                    catch (Exception e)
					{
                        LOG.log(Level.SEVERE, "", e);
                    }
                }
            }
        };
        TaskEngine.getInstance().schedule(updatePresence, 5000);
    }

    public void connectionClosedOnError(final Exception ex)
	{
    }

    private void removeAllUsers()
	{
        for (ContactGroup contactGroup : new ArrayList<ContactGroup>(getContactGroups())) 
            contactGroup.removeAllContacts();
    }

    public void reconnectingIn(int i)
	{
    }

    public void reconnectionSuccessful()
	{
        clientReconnected();
    }

    public void reconnectionFailed(Exception exception)
	{
    }

    private void moveToOffline(ContactItem contactItem)
	{       	  
        String jid = contactItem.getJID();

        final Roster roster = ChatsyManager.getConnection().getRoster();
        for (RosterGroup group : roster.getEntry(jid).getGroups())
		{
            ContactGroup contactGroup = getContactGroup(group.getName());
            if (contactGroup == null
				&& !group.getName().isEmpty())
            	contactGroup = addContactGroup(group.getName());

            if (contactGroup != null)
                contactGroup.addOfflineContactItem(contactItem.getAlias(), contactItem.getNickname(), contactItem.getJID(), contactItem.getStatus());
        }
        
		for (ContactGroup group : getContactGroups())
			group.toggleOfflineVisibility(false);
    }

    public final static Comparator<ContactItem> ContactItemComparator = new Comparator<ContactItem>()
	{
        public int compare(ContactItem item1, ContactItem item2)
		{
            return item1.getDisplayName().toLowerCase().compareTo(item2.getDisplayName().toLowerCase());
        }
    };

}