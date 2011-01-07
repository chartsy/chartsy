package org.chartsy.chatsy.chat;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.packet.VCard;
import org.chartsy.chatsy.chat.component.JContactItemField;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.ContactGroup;
import org.chartsy.chatsy.chat.ui.ContactItem;
import org.chartsy.chatsy.chat.ui.ContactList;
import org.chartsy.chatsy.chat.ui.rooms.GroupChatRoom;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingTimerTask;
import org.chartsy.chatsy.chat.util.TaskEngine;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chatimpl.profile.VCardManager;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class UserManager
{

    private Map<JFrame,Component> parents = new HashMap<JFrame,Component>();

    public UserManager()
	{
    }

    public String getNickname()
	{
        final VCardManager vCardManager = ChatsyManager.getVCardManager();
        VCard vcard = vCardManager.getVCard();
        if (vcard == null)
            return ChatsyManager.getSessionManager().getUsername();
        else
		{
            String nickname = vcard.getNickName();
            if (ModelUtil.hasLength(nickname))
                return nickname;
            else
			{
                String firstName = vcard.getFirstName();
                if (ModelUtil.hasLength(firstName))
                    return firstName;
            }
        }

        String username = ChatsyManager.getSessionManager().getUsername();
        username = StringUtils.unescapeNode(username);

        return username;
    }

    public Collection getUserJidsInRoom(String room, boolean fullJID)
	{
        return new ArrayList();
    }

    public boolean isOwner(GroupChatRoom groupChatRoom, String nickname)
	{
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null)
		{
            String affiliation = occupant.getAffiliation();
            if ("owner".equals(affiliation))
                return true;
        }
        return false;
    }

    public boolean isOwner(Occupant occupant)
	{
        if (occupant != null)
		{
            String affiliation = occupant.getAffiliation();
            if ("owner".equals(affiliation))
                return true;
        }
        return false;
    }

    public boolean isModerator(GroupChatRoom groupChatRoom, String nickname)
	{
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null)
		{
            String role = occupant.getRole();
            if ("moderator".equals(role))
                return true;
        }
        return false;
    }

    public boolean isModerator(Occupant occupant)
	{
        if (occupant != null)
		{
            String role = occupant.getRole();
            if ("moderator".equals(role)) 
                return true;
        }
        return false;
    }

    public boolean isOwnerOrAdmin(GroupChatRoom groupChatRoom, String nickname)
	{
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null)
		{
            String affiliation = occupant.getAffiliation();
            if ("owner".equals(affiliation) || "admin".equals(affiliation))
                return true;
        }
        return false;
    }

    public boolean isOwnerOrAdmin(Occupant occupant)
	{
        if (occupant != null)
		{
            String affiliation = occupant.getAffiliation();
            if ("owner".equals(affiliation) || "admin".equals(affiliation))
                return true;
        }
        return false;
    }

    public Occupant getOccupant(GroupChatRoom groupChatRoom, String nickname)
	{
        String userJID = groupChatRoom.getRoomname() + "/" + nickname;
        Occupant occ = null;
        try
		{
            occ = groupChatRoom.getMultiUserChat().getOccupant(userJID);
        }
        catch (Exception e)
		{
            Log.error(e);
        }
        return occ;
    }

    public boolean isAdmin(GroupChatRoom groupChatRoom, String nickname)
	{
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null)
		{
            String affiliation = occupant.getAffiliation();
            if ("admin".equals(affiliation))
                return true;
        }
        return false;
    }

    public boolean hasVoice(GroupChatRoom groupChatRoom, String nickname)
	{
        Occupant occupant = getOccupant(groupChatRoom, nickname);
        if (occupant != null)
		{
            String role = occupant.getRole();
            if ("visitor".equals(role))
                return false;
        }
        return true;
    }

    public Collection getAllParticipantsInRoom(ChatRoom chatRoom)
	{
        return new ArrayList();
    }


    public String getUserNicknameFromJID(String jid)
	{
        ContactList contactList = ChatsyManager.getWorkspace().getContactList();
        ContactItem item = contactList.getContactItemByJID(jid);
        if (item != null)
            return item.getDisplayName();

        return unescapeJID(jid);
    }

    public static String escapeJID(String jid)
	{
        if (jid == null)
            return null;

        final StringBuilder builder = new StringBuilder();
        String node = StringUtils.parseName(jid);
        String restOfJID = jid.substring(node.length());
        builder.append(StringUtils.escapeNode(node));
        builder.append(restOfJID);
        return builder.toString();
    }

    public static String unescapeJID(String jid)
	{
        if (jid == null)
            return null;

        final StringBuilder builder = new StringBuilder();
        String node = StringUtils.parseName(jid);
        String restOfJID = jid.substring(node.length());
        builder.append(StringUtils.unescapeNode(node));
        builder.append(restOfJID);
        return builder.toString();
    }

    public String getJIDFromDisplayName(String displayName)
	{
        ContactList contactList = ChatsyManager.getWorkspace().getContactList();
        ContactItem item = contactList.getContactItemByDisplayName(displayName);
        if (item != null)
            return getFullJID(item.getJID());

        return null;
    }

    public String getFullJID(String jid)
	{
        Presence presence = PresenceManager.getPresence(jid);
        return presence.getFrom();
    }

    public void searchContacts(String contact, final JFrame parent)
	{
        if (parents.get(parent) == null)
            parents.put(parent, parent.getGlassPane());

        final Component glassPane = parents.get(parent);
        parent.setGlassPane(glassPane);

        final Map<String, ContactItem> contactMap = new HashMap<String, ContactItem>();
        final List<ContactItem> contacts = new ArrayList<ContactItem>();

        final ContactList contactList = ChatsyManager.getWorkspace().getContactList();

        for (ContactGroup contactGroup : contactList.getContactGroups())
		{
            for (ContactItem contactItem : contactGroup.getContactItems())
			{
                if (!contactMap.containsKey(contactItem.getJID()))
				{
                    contacts.add(contactItem);
                    contactMap.put(contactItem.getJID(), contactItem);
                }
            }
        }

        Collections.sort(contacts, itemComparator);
        final JContactItemField contactField = new JContactItemField(new ArrayList<ContactItem>(contacts));

        JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new GridBagLayout());
        JLabel enterLabel = new JLabel("Find");
        enterLabel.setFont(new Font("Dialog", Font.BOLD, 10));
        layoutPanel.add(enterLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 0, 5), 0, 0));
        layoutPanel.add(contactField, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 50, 0));
        layoutPanel.setBorder(BorderFactory.createBevelBorder(0));

        contactField.addKeyListener(new KeyAdapter()
		{
            public void keyReleased(KeyEvent keyEvent)
			{
                if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER)
				{
                    if (ModelUtil.hasLength(contactField.getText()))
					{
                        ContactItem item = contactMap.get(contactField.getText());
                        if (item == null)
                            item = contactField.getSelectedContactItem();
                        if (item != null)
						{
                            parent.setGlassPane(glassPane);
                            parent.getGlassPane().setVisible(false);
                            contactField.dispose();
                            ChatsyManager.getChatManager().activateChat(item.getJID(), item.getDisplayName());
                        }
                    }

                }
                else if (keyEvent.getKeyChar() == KeyEvent.VK_ESCAPE)
				{
                    parent.setGlassPane(glassPane);
                    parent.getGlassPane().setVisible(false);
                    contactField.dispose();
                }
            }
        });

        contactField.getList().addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent e)
			{
                if (e.getClickCount() == 2)
				{
                    if (ModelUtil.hasLength(contactField.getText()))
					{
                        ContactItem item = contactMap.get(contactField.getText());
                        if (item == null)
                            item = contactField.getSelectedContactItem();
                        if (item != null)
						{
                            parent.setGlassPane(glassPane);
                            parent.getGlassPane().setVisible(false);
                            contactField.dispose();
                            ChatsyManager.getChatManager().activateChat(item.getJID(), item.getDisplayName());
                        }
                    }
                }
            }
        });

        final JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(layoutPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 200, 0));
        mainPanel.setOpaque(false);

        contactField.setText(contact);
        parent.setGlassPane(mainPanel);
        parent.getGlassPane().setVisible(true);
        contactField.focus();

        mainPanel.addMouseListener(new MouseAdapter()
		{
            public void mouseClicked(MouseEvent mouseEvent)
			{
                parent.setGlassPane(glassPane);
                parent.getGlassPane().setVisible(false);
                contactField.dispose();
            }
        });

        parent.addWindowListener(new WindowAdapter()
		{
            public void windowClosing(WindowEvent windowEvent)
			{
                parent.setGlassPane(glassPane);
                parent.getGlassPane().setVisible(false);
                contactField.dispose();
                parent.removeWindowListener(this);
            }

            public void windowDeactivated(final WindowEvent windowEvent)
			{
                TimerTask task = new SwingTimerTask()
				{
                    public void doRun()
					{
                        if (contactField.canClose())
                            windowClosing(windowEvent);
                    }
                };
                TaskEngine.getInstance().schedule(task, 250);
            }
        });
    }

    public static String getValidJID(String jid)
	{
        Roster roster = ChatsyManager.getConnection().getRoster();
        Iterator<Presence> presences = roster.getPresences(jid);
        int count = 0;
        Presence p = null;
        if (presences.hasNext())
		{
            p = presences.next();
            count++;
        }

        if (count == 1 && p != null)
            return p.getFrom();
        else
            return jid;
    }

    final Comparator<ContactItem> itemComparator = new Comparator<ContactItem>()
	{
        public int compare(ContactItem item1, ContactItem item2)
		{
            return item1.getDisplayName().toLowerCase().compareTo(item2.getDisplayName().toLowerCase());
        }
    };

}



