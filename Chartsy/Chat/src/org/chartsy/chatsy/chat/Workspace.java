package org.chartsy.chatsy.chat;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.TimerTask;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.packet.VCard;
import org.chartsy.chatsy.chat.component.tabbedPane.TabbedPane;
import org.chartsy.chatsy.chat.search.SearchManager;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.ChatRoomNotFoundException;
import org.chartsy.chatsy.chat.ui.CommandPanel;
import org.chartsy.chatsy.chat.ui.ConferenceList;
import org.chartsy.chatsy.chat.ui.ContactItem;
import org.chartsy.chatsy.chat.ui.ContactList;
import org.chartsy.chatsy.chat.ui.status.StatusBar;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.TaskEngine;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chatimpl.plugin.manager.Enterprise;


public class Workspace extends JPanel implements PacketListener
{

	private TabbedPane workspacePane;
    private StatusBar statusBox;
    private CommandPanel commandPanel;
    private ContactList contactList;
	private ConferenceList conferenceList;
    private static Workspace instance;
	private static final Object LOCK = new Object();
    private JPanel cardPanel;
    private CardLayout cardLayout;
    public static final String WORKSPACE_PANE = "WORKSPACE_PANE";

    public static Workspace getInstance()
	{
		synchronized (LOCK)
		{
			if (instance == null)
			{
				Workspace controller = new Workspace();
				instance = controller;
			}
		}
		return instance;
    }

    private Workspace()
	{
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.add(WORKSPACE_PANE, this);
    }

    public void buildLayout()
	{
		statusBox = new StatusBar();
        commandPanel = new CommandPanel();

		Enterprise.getInstance();
		contactList = new ContactList();
		conferenceList = new ConferenceList();
        contactList.initialize();
        statusBox.loadVCard();
		workspacePane = new TabbedPane(conferenceList, contactList);

		setLayout(new BorderLayout());
		setOpaque(false);

		add(statusBox, BorderLayout.NORTH);
		add(workspacePane, BorderLayout.CENTER);
		add(commandPanel, BorderLayout.SOUTH);
    }

    public void loadPlugins()
	{
        PacketFilter workspaceMessageFilter = new PacketTypeFilter(Message.class);
        ChatsyManager.getSessionManager().getConnection().addPacketListener(this, workspaceMessageFilter);

        PacketListener workspacePresenceListener = new PacketListener()
		{
            public void processPacket(Packet packet)
			{
                Presence presence = (Presence)packet;
                if (presence.getProperty("anonymous") != null)
				{
                    boolean isAvailable = statusBox.getPresence().getMode() == Presence.Mode.available;
                    Presence reply = new Presence(Presence.Type.available);
                    if (!isAvailable) 
                        reply.setType(Presence.Type.unavailable);
                    reply.setTo(presence.getFrom());
                    ChatsyManager.getSessionManager().getConnection().sendPacket(reply);
                }
            }
        };
        ChatsyManager.getSessionManager().getConnection().addPacketListener(workspacePresenceListener, new PacketTypeFilter(Presence.class));

        final Presence presence = ChatsyManager.getWorkspace().getStatusBar().getPresence();
        ChatsyManager.getSessionManager().changePresence(presence);

        SearchManager.getInstance();

        TaskEngine.getInstance().schedule(new TimerTask()
		{
            @Override public void run()
			{
                Roster roster = ChatsyManager.getConnection().getRoster();
                roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
            }
        }, 2000);
    }


    public StatusBar getStatusBar()
	{
        return statusBox;
    }

    @Override public void processPacket(final Packet packet)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            @Override public void run()
			{
                handleIncomingPacket(packet);
            }
        });
    }

    private void handleIncomingPacket(Packet packet)
	{
        if (packet instanceof Message)
		{
            final Message message = (Message)packet;
            boolean isGroupChat = message.getType() == Message.Type.groupchat;
            if (message.getExtension("x", "jabber:x:conference") != null) 
                return;

            final String body = message.getBody();
            boolean broadcast = message.getProperty("broadcast") != null;

            DelayInformation offlineInformation = (DelayInformation)message.getExtension("x", "jabber:x:delay");
            if (offlineInformation != null
				&& (Message.Type.chat == message.getType()
				|| Message.Type.normal == message.getType()))
                handleOfflineMessage(message);

            if (body == null ||
                isGroupChat ||
                broadcast ||
                message.getType() == Message.Type.normal ||
                message.getType() == Message.Type.headline ||
                message.getType() == Message.Type.error) 
                return;

            final String from = packet.getFrom();
            final String host = ChatsyManager.getSessionManager().getServerAddress();

            final String bareJID = StringUtils.parseBareAddress(from);
            if (host.equalsIgnoreCase(from) || from == null) 
                return;

            ChatRoom room = ChatsyManager.getChatManager().getChatContainer().getChatRoom(bareJID);
            if (room == null) 
                createOneToOneRoom(bareJID, message);
        }
    }

    private void handleOfflineMessage(Message message)
	{
        if (!ModelUtil.hasLength(message.getBody()))
            return;

        String bareJID = StringUtils.parseBareAddress(message.getFrom());
        ContactItem contact = contactList.getContactItemByJID(bareJID);
        String nickname = StringUtils.parseName(bareJID);
        if (contact != null)
            nickname = contact.getDisplayName();

        ChatRoom room = ChatsyManager.getChatManager().createChatRoom(bareJID, nickname, nickname);
        room.getTranscriptWindow().insertMessage(nickname, message, ChatManager.FROM_COLOR, Color.white);
        room.addToTranscript(message, true);

        ChatsyManager.getMessageEventManager().sendDeliveredNotification(message.getFrom(), message.getPacketID());
        ChatsyManager.getMessageEventManager().sendDisplayedNotification(message.getFrom(), message.getPacketID());
    }

    private void createOneToOneRoom(String bareJID, Message message)
	{
        ContactItem contact = contactList.getContactItemByJID(bareJID);
        String nickname = StringUtils.parseName(bareJID);
        if (contact != null)
		{
            nickname = contact.getDisplayName();
        }
        else
		{
            VCard vCard = ChatsyManager.getVCardManager().getVCard(bareJID);
            if (vCard != null && vCard.getError() == null)
			{
                String firstName = vCard.getFirstName();
                String lastName = vCard.getLastName();
                String userNickname = vCard.getNickName();
				
                if (ModelUtil.hasLength(userNickname)) 
                    nickname = userNickname;
                else if (ModelUtil.hasLength(firstName)
					&& ModelUtil.hasLength(lastName))
                    nickname = firstName + " " + lastName;
                else if (ModelUtil.hasLength(firstName)) 
                    nickname = firstName;
            }
        }

        ChatsyManager.getChatManager().createChatRoom(bareJID, nickname, nickname);
        try
		{
            insertMessage(bareJID, message);
        }
        catch (ChatRoomNotFoundException e)
		{
            Log.error(e);
        }
    }

    private void insertMessage(final String bareJID, final Message message) 
		throws ChatRoomNotFoundException
	{
        ChatRoom chatRoom = ChatsyManager.getChatManager().getChatContainer().getChatRoom(bareJID);
        chatRoom.insertMessage(message);
        int chatLength = chatRoom.getTranscriptWindow().getDocument().getLength();
        chatRoom.getTranscriptWindow().setCaretPosition(chatLength);
        chatRoom.getChatInputEditor().requestFocusInWindow();
    }

    public ContactList getContactList()
	{
        return contactList;
    }

    public void changeCardLayout(String layout)
	{
        cardLayout.show(cardPanel, layout);
    }

    public JPanel getCardPanel()
	{
        return cardPanel;
    }

    public CommandPanel getCommandPanel()
	{
        return commandPanel;
    }

	public TabbedPane getTabbedPane()
	{
		return workspacePane;
	}
	
}
