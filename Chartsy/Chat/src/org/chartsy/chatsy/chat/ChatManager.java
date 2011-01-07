package org.chartsy.chatsy.chat;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.MessageEventManager;
import org.jivesoftware.smackx.MessageEventNotificationListener;
import org.jivesoftware.smackx.MessageEventRequestListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.chartsy.chatsy.chat.component.tabbedPane.ChatsyTab;
import org.chartsy.chatsy.chat.decorator.DefaultTabHandler;
import org.chartsy.chatsy.chat.ui.ChatContainer;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.ChatRoomListener;
import org.chartsy.chatsy.chat.ui.ContactItem;
import org.chartsy.chatsy.chat.ui.ContactItemHandler;
import org.chartsy.chatsy.chat.ui.ContactList;
import org.chartsy.chatsy.chat.ui.GlobalMessageListener;
import org.chartsy.chatsy.chat.ui.MessageFilter;
import org.chartsy.chatsy.chat.ui.ChatsyTabHandler;
import org.chartsy.chatsy.chat.ui.TranscriptWindowInterceptor;
import org.chartsy.chatsy.chat.ui.conferences.ConferenceUtils;
import org.chartsy.chatsy.chat.ui.conferences.RoomInvitationListener;
import org.chartsy.chatsy.chat.ui.rooms.ChatRoomImpl;
import org.chartsy.chatsy.chat.ui.rooms.GroupChatRoom;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingWorker;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import org.chartsy.chatsy.chat.util.log.Log;
import org.openide.util.NbPreferences;

public final class ChatManager implements MessageEventNotificationListener
{

    private static ChatManager singleton;
    private static final Object LOCK = new Object();

    public static Color TO_COLOR = Color.gray;
    public static Color FROM_COLOR = Color.black;
    public static Color NOTIFICATION_COLOR = Color.blue;
    public static Color ERROR_COLOR = Color.red;

    public static Color[] COLORS = {Color.red, Color.blue, Color.gray, Color.magenta, new Color(238, 153, 247), new Color(128, 128, 0), new Color(173, 205, 50),
        new Color(181, 0, 0), new Color(0, 100, 0), new Color(237, 150, 122), new Color(0, 139, 139), new Color(218, 14, 0), new Color(147, 112, 219),
        new Color(205, 133, 63), new Color(163, 142, 35), new Color(72, 160, 237), new Color(255, 140, 0), new Color(106, 90, 205), new Color(224, 165, 32),
        new Color(255, 69, 0), new Color(255, 99, 72), new Color(109, 130, 180), new Color(233, 0, 0), new Color(139, 69, 19), new Color(255, 127, 80),
        new Color(140, 105, 225)};

    private List<MessageFilter> messageFilters = new ArrayList<MessageFilter>();
    private List<GlobalMessageListener> globalMessageListeners = new ArrayList<GlobalMessageListener>();
    private List<RoomInvitationListener> invitationListeners = new ArrayList<RoomInvitationListener>();
    private List<TranscriptWindowInterceptor> interceptors = new ArrayList<TranscriptWindowInterceptor>();
    private List<ChatsyTabHandler> chatsyTabHandlers = new CopyOnWriteArrayList<ChatsyTabHandler>();
    private final ChatContainer chatContainer;
    private String conferenceService;
    private List<ContactItemHandler> contactItemHandlers = new ArrayList<ContactItemHandler>();
    private Set<ChatRoom> typingNotificationList = new HashSet<ChatRoom>();


    public static ChatManager getInstance()
	{
        synchronized (LOCK)
		{
            if (null == singleton)
			{
                ChatManager controller = new ChatManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private ChatManager()
	{
        chatContainer = new ChatContainer();
        ChatsyManager.getMessageEventManager().addMessageEventNotificationListener(this);
        MessageEventRequestListener messageEventRequestListener = new ChatMessageEventRequestListener();
        ChatsyManager.getMessageEventManager().addMessageEventRequestListener(messageEventRequestListener);
        addChatsyTabHandler(new DefaultTabHandler());
    }

    public void addChatRoomListener(ChatRoomListener listener)
	{
        getChatContainer().addChatRoomListener(listener);
    }

    public void removeChatRoomListener(ChatRoomListener listener)
	{
        getChatContainer().removeChatRoomListener(listener);
    }

    public void removeChat(ChatRoom chatRoom)
	{
        chatContainer.closeTab(chatRoom);
    }

    public ChatContainer getChatContainer()
	{
        return chatContainer;
    }

    public GroupChatRoom getGroupChat(String roomName)
		throws ChatNotFoundException
	{
        for (ChatRoom chatRoom : getChatContainer().getChatRooms())
		{
            if (chatRoom instanceof GroupChatRoom)
			{
                GroupChatRoom groupChat = (GroupChatRoom)chatRoom;
                if (groupChat.getRoomname().equals(roomName)) 
                    return groupChat;
            }
        }
        throw new ChatNotFoundException("Could not locate Group Chat Room - " + roomName);
    }

    public ChatRoom createChatRoom(String userJID, String nickname, String title)
	{
        ChatRoom chatRoom = getChatContainer().getChatRoom(userJID);
		if (chatRoom == null)
		{
			chatRoom = new ChatRoomImpl(userJID, nickname, title);
			getChatContainer().addChatRoom(chatRoom);
		}
        return chatRoom;
    }

    public ChatRoom getChatRoom(String jid)
	{
        ChatRoom chatRoom = getChatContainer().getChatRoom(jid);
		if (chatRoom == null)
		{
			ContactList contactList = ChatsyManager.getWorkspace().getContactList();
            ContactItem item = contactList.getContactItemByJID(jid);
            if (item != null)
			{
                String nickname = item.getDisplayName();
                chatRoom = new ChatRoomImpl(jid, nickname, nickname);
            }
            else
			{
                chatRoom = new ChatRoomImpl(jid, jid, jid);
            }
            getChatContainer().addChatRoom(chatRoom);
		}
        return chatRoom;
    }

    public ChatRoom createConferenceRoom(String roomName, String serviceName)
	{
        final MultiUserChat chatRoom = new MultiUserChat(ChatsyManager.getConnection(), roomName + "@" + serviceName);
        final GroupChatRoom room = new GroupChatRoom(chatRoom);
        try
		{
            chatRoom.create(NbPreferences.root().node("/org/chartsy/chat").get("nickname", ""));
            chatRoom.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
        }
        catch (XMPPException e)
		{
			Log.error("Unable to send conference room chat configuration form.", e);
            return null;
        }

        getChatContainer().addChatRoom(room);
        return room;
    }

    public void activateChat(final String jid, final String nickname)
	{
        if (!ModelUtil.hasLength(jid))
            return;

        SwingWorker worker = new SwingWorker()
		{
            final ChatManager chatManager = ChatsyManager.getChatManager();
            ChatRoom chatRoom;
            public Object construct()
			{
                try
				{
                    Thread.sleep(10);
                }
                catch (InterruptedException e) 
				{
					Log.error("Error in activate chat.", e);
                }

                ChatContainer chatRooms = chatManager.getChatContainer();
				chatRoom = chatRooms.getChatRoom(jid);
				return chatRoom;
            }
            public void finished()
			{
                if (chatRoom == null)
				{
                    chatRoom = new ChatRoomImpl(jid, nickname, nickname);
                    chatManager.getChatContainer().addChatRoom(chatRoom);
                }
                chatManager.getChatContainer().activateChatRoom(chatRoom);
            }
        };
        worker.start();
    }

    public boolean chatRoomExists(String jid)
	{
		return getChatContainer().getChatRoom(jid) != null;
    }

    public void addMessageFilter(MessageFilter filter)
	{
        messageFilters.add(filter);
    }

    public void removeMessageFilter(MessageFilter filter)
	{
        messageFilters.remove(filter);
    }

    public Collection<MessageFilter> getMessageFilters()
	{
        return messageFilters;
    }

    public void addGlobalMessageListener(GlobalMessageListener listener)
	{
        globalMessageListeners.add(listener);
    }

    public void removeGlobalMessageListener(GlobalMessageListener listener)
	{
        globalMessageListeners.remove(listener);
    }

    public void fireGlobalMessageReceievedListeners(ChatRoom chatRoom, Message message)
	{
        for (GlobalMessageListener listener : globalMessageListeners) 
            listener.messageReceived(chatRoom, message);
    }

    public void fireGlobalMessageSentListeners(ChatRoom chatRoom, Message message)
	{
        for (GlobalMessageListener listener : globalMessageListeners)
            listener.messageSent(chatRoom, message);
    }

    public void filterIncomingMessage(ChatRoom room, Message message)
	{
        final ChatManager chatManager = ChatsyManager.getChatManager();
        Iterator<MessageFilter> filters = chatManager.getMessageFilters().iterator();
        try
		{
            cancelledNotification(message.getFrom(), "");
        }
        catch (Exception e)
		{
            Log.error(e);
        }
        while (filters.hasNext()) 
            ((MessageFilter)filters.next()).filterIncoming(room, message);
    }

    public void filterOutgoingMessage(ChatRoom room, Message message)
	{
        final ChatManager chatManager = ChatsyManager.getChatManager();
        for (Object o : chatManager.getMessageFilters()) 
            ((MessageFilter) o).filterOutgoing(room, message);
    }

    public void addInvitationListener(RoomInvitationListener listener)
	{
        invitationListeners.add(listener);
    }

    public void removeInvitationListener(RoomInvitationListener listener)
	{
        invitationListeners.remove(listener);
    }

    public Collection<RoomInvitationListener> getInvitationListeners()
	{
        return Collections.unmodifiableCollection(invitationListeners);
    }

    public String getDefaultConferenceService()
	{
        if (conferenceService == null)
		{
            try
			{
                Collection<String> col = MultiUserChat.getServiceNames(ChatsyManager.getConnection());
                if (col.size() > 0) 
                    conferenceService = col.iterator().next();
            }
            catch (XMPPException e)
			{
                Log.error(e);
            }
        }
        return conferenceService;
    }

    public void addContactItemHandler(ContactItemHandler handler)
	{
        contactItemHandlers.add(handler);
    }

    public void removeContactItemHandler(ContactItemHandler handler)
	{
        contactItemHandlers.remove(handler);
    }

    public boolean fireContactItemPresenceChanged(ContactItem item, Presence presence)
	{
        for (ContactItemHandler handler : contactItemHandlers) 
            if (handler.handlePresence(item, presence)) 
                return true;
        return false;
    }

    public boolean fireContactItemDoubleClicked(ContactItem item)
	{
        for (ContactItemHandler handler : contactItemHandlers) 
            if (handler.handleDoubleClick(item)) 
                return true;
        return false;
    }

    public Icon getIconForContactHandler(String jid)
	{
        for (ContactItemHandler handler : contactItemHandlers)
		{
            Icon icon = handler.getIcon(jid);
            if (icon != null) 
                return icon;
        }
        return null;
    }

    public Icon getTabIconForContactHandler(Presence presence)
	{
        for (ContactItemHandler handler : contactItemHandlers)
		{
            Icon icon = handler.getTabIcon(presence);
            if (icon != null) 
                return icon;
        }
        return null;
    }

    public void deliveredNotification(String from, String packetID)
	{
    }

    public void displayedNotification(String from, String packetID)
	{
    }

    public void composingNotification(final String from, String packetID)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            public void run()
			{
                final ContactList contactList = ChatsyManager.getWorkspace().getContactList();
                ChatRoom chatRoom = getChatContainer().getChatRoom(StringUtils.parseBareAddress(from));
				if (chatRoom != null && chatRoom instanceof ChatRoomImpl)
				{
					typingNotificationList.add(chatRoom);
					notifyChatsyTabHandlers(chatRoom);
				}
                contactList.setIconFor(from, null);
            }
        });
    }

    public void offlineNotification(String from, String packetID)
	{
    }

    public void cancelledNotification(final String from, String packetID)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            public void run()
			{
                ContactList contactList = ChatsyManager.getWorkspace().getContactList();
                ChatRoom chatRoom = getChatContainer().getChatRoom(StringUtils.parseBareAddress(from));
				if (chatRoom != null && chatRoom instanceof ChatRoomImpl)
				{
					typingNotificationList.remove(chatRoom);
					notifyChatsyTabHandlers(chatRoom);
				}
                contactList.useDefaults(from);
            }
        });
    }

    public void addTypingNotification(ChatRoom chatRoom)
	{
        typingNotificationList.add(chatRoom);
    }

    public void removeTypingNotification(ChatRoom chatRoom)
	{
        typingNotificationList.remove(chatRoom);
    }

    public boolean containsTypingNotification(ChatRoom chatRoom)
	{
        return typingNotificationList.contains(chatRoom);
    }

    private class ChatMessageEventRequestListener implements MessageEventRequestListener 
	{

        public void deliveredNotificationRequested(String from, String packetID, MessageEventManager messageEventManager)
		{
        }

        public void displayedNotificationRequested(String from, String packetID, MessageEventManager messageEventManager)
		{
        }

        public void composingNotificationRequested(final String from, String packetID, MessageEventManager messageEventManager)
		{
            SwingUtilities.invokeLater(new Runnable()
			{
                public void run()
				{
                    ChatRoom chatRoom = getChatContainer().getChatRoom(StringUtils.parseBareAddress(from));
					if (chatRoom == null)
						return;
                    if (chatRoom != null
						&& chatRoom instanceof ChatRoomImpl)
                        ((ChatRoomImpl)chatRoom).setSendTypingNotification(true);
                }
            });
        }

        public void offlineNotificationRequested(String from, String packetID, MessageEventManager messageEventManager)
		{
        }
		
    }

    public void addTranscriptWindowInterceptor(TranscriptWindowInterceptor interceptor)
	{
        interceptors.add(interceptor);
    }

    public void removeTranscriptWindowInterceptor(TranscriptWindowInterceptor interceptor)
	{
        interceptors.remove(interceptor);
    }

    public Collection<TranscriptWindowInterceptor> getTranscriptWindowInterceptors()
	{
        return interceptors;
    }

    public void addChatsyTabHandler(ChatsyTabHandler decorator)
	{
        chatsyTabHandlers.add(0, decorator);
    }

    public void removeChatsyTabHandler(ChatsyTabHandler decorator)
	{
        chatsyTabHandlers.remove(decorator);
    }

    public void notifyChatsyTabHandlers(Component component)
	{
        final ChatsyTab tab = chatContainer.getTabContainingComponent(component);
        if (tab == null) 
            return;
        boolean isChatFrameInFocus = getChatContainer().getChatFrame().isInFocus();
        boolean isSelectedTab = getChatContainer().getSelectedComponent() == component;
        for (ChatsyTabHandler decorator : chatsyTabHandlers)
		{
            boolean isHandled = decorator.isTabHandled(tab, component, isSelectedTab, isChatFrameInFocus);
            if (isHandled)
			{
                tab.validateTab();
                return;
            }
        }
    }

    public Collection<ContactItem> getSelectedContactItems()
	{
        final ContactList contactList = ChatsyManager.getWorkspace().getContactList();
        return contactList.getSelectedUsers();
    }

    public void handleURIMapping(String arguments)
	{
        if (arguments == null)
            return;
        
        if (arguments.indexOf("xmpp") == -1) 
            return;

        if (arguments.indexOf("?message") != -1)
		{
            try
			{
                handleJID(arguments);
            }
            catch (Exception e)
			{
                Log.error(e);
            }
        }
        else if (arguments.indexOf("?join") != -1)
		{
            try
			{
                handleConference(arguments);
            }
            catch (Exception e)
			{
                Log.error(e);
            }
        }
        else if (arguments.indexOf("?") == -1)
		{
            int index = arguments.indexOf(":");
            if (index != -1)
			{
                String jid = arguments.substring(index + 1);
                UserManager userManager = ChatsyManager.getUserManager();
                String nickname = userManager.getUserNicknameFromJID(jid);
                if (nickname == null) 
                    nickname = jid;

                ChatManager chatManager = ChatsyManager.getChatManager();
                ChatRoom chatRoom = chatManager.createChatRoom(jid, nickname, nickname);
                chatManager.getChatContainer().activateChatRoom(chatRoom);
            }
        }
    }

    private void handleJID(String uriMapping)
	{
        int index = uriMapping.indexOf("xmpp:");
        int messageIndex = uriMapping.indexOf("?message");

        int bodyIndex = uriMapping.indexOf("body=");

        String jid = uriMapping.substring(index + 5, messageIndex);
        String body = null;

        if (bodyIndex != -1)
            body = uriMapping.substring(bodyIndex + 5);

        body = org.chartsy.chatsy.chat.util.StringUtils.unescapeFromXML(body);
        body = org.chartsy.chatsy.chat.util.StringUtils.replace(body, "%20", " ");

        UserManager userManager = ChatsyManager.getUserManager();
        String nickname = userManager.getUserNicknameFromJID(jid);
        if (nickname == null)
            nickname = jid;

        ChatManager chatManager = ChatsyManager.getChatManager();
        ChatRoom chatRoom = chatManager.createChatRoom(jid, nickname, nickname);
        if (body != null)
		{
            Message message = new Message();
            message.setBody(body);
            chatRoom.sendMessage(message);
        }
        chatManager.getChatContainer().activateChatRoom(chatRoom);
    }

    private void handleConference(String uriMapping) throws Exception
	{
        int index = uriMapping.indexOf("xmpp:");
        int join = uriMapping.indexOf("?join");

        String conference = uriMapping.substring(index + 5, join);
        ConferenceUtils.joinConferenceOnSeperateThread(conference, conference, null);
    }

}
