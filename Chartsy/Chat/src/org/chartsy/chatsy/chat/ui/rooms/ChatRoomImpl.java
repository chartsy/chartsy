package org.chartsy.chatsy.chat.ui.rooms;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StreamError;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.MessageEventManager;
import org.jivesoftware.smackx.packet.MessageEvent;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.PresenceManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.ContactItem;
import org.chartsy.chatsy.chat.ui.ContactList;
import org.chartsy.chatsy.chat.ui.MessageEventListener;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.TaskEngine;

public class ChatRoomImpl extends ChatRoom
{

    private List<MessageEventListener> messageEventListeners = new ArrayList<MessageEventListener>();
    private String roomname;
	private String roomTitle;
    private String tabTitle;
    private String participantJID;
    private String participantNickname;
	private String threadID;
	
    private Presence presence;
    private Roster roster;
    private TimerTask typingTimerTask;
	private Icon tabIcon;

	private long lastTypedCharTime;
    private long lastActivity;

	private boolean sendNotification = true;
	private boolean sendTypingNotification = true;
	private boolean offlineSent;
    private boolean active;

    public ChatRoomImpl(final String participantJID, String participantNickname, String title)
	{
        this.active = true;
        this.participantJID = participantJID;
        this.participantNickname = participantNickname;

        PacketFilter fromFilter = new FromMatchesFilter(participantJID);
        PacketFilter orFilter = new OrFilter(new PacketTypeFilter(Presence.class), new PacketTypeFilter(Message.class));
        PacketFilter andFilter = new AndFilter(orFilter, fromFilter);
        ChatsyManager.getConnection().addPacketListener(this, andFilter);

		this.roomname = participantJID;
        this.tabTitle = title;
        this.roomTitle = participantNickname;

        this.getSplitPane().setRightComponent(null);
        getSplitPane().setDividerSize(0);

        presence = PresenceManager.getPresence(participantJID);
        roster = ChatsyManager.getConnection().getRoster();
        tabIcon = PresenceManager.getIconFromPresence(presence);

        typingTimerTask = new TimerTask()
		{
            public void run()
			{
                if (!sendTypingNotification)
                    return;
                long now = System.currentTimeMillis();
                if (now - lastTypedCharTime > 2000)
				{
                    if (!sendNotification)
					{
                        ChatsyManager.getMessageEventManager().sendCancelledNotification(getParticipantJID(), threadID);
                        sendNotification = true;
                    }
                }
            }
        };
        TaskEngine.getInstance().scheduleAtFixedRate(typingTimerTask, 2000, 2000);
        lastActivity = System.currentTimeMillis();
    }

    public void closeChatRoom()
	{
        if (!active)
            return;
        super.closeChatRoom();
        if (!sendNotification)
		{
            ChatsyManager.getMessageEventManager().sendCancelledNotification(getParticipantJID(), threadID);
            sendNotification = true;
        }
        ChatsyManager.getChatManager().removeChat(this);
        ChatsyManager.getConnection().removePacketListener(this);
        if (typingTimerTask != null)
		{
            TaskEngine.getInstance().cancelScheduledTask(typingTimerTask);
            typingTimerTask = null;
        }
        active = false;
    }

    public void sendMessage()
	{
        String text = getChatInputEditor().getText();
        sendMessage(text);
    }

    public void sendMessage(String text)
	{
        final Message message = new Message();
        if (threadID == null)
            threadID = StringUtils.randomString(6);
        message.setThread(threadID);
        message.setBody(text);
        if (!ModelUtil.hasLength(text))
            return;
        ChatsyManager.getChatManager().filterOutgoingMessage(this, message);
        ChatsyManager.getChatManager().fireGlobalMessageSentListeners(this, message);
        sendMessage(message);
        sendNotification = true;
    }

    public void sendMessage(Message message)
	{
        lastActivity = System.currentTimeMillis();
        try
		{
            getTranscriptWindow().insertMessage(getNickname(), message, ChatManager.TO_COLOR, Color.white);
            getChatInputEditor().selectAll();
            getTranscriptWindow().validate();
            getTranscriptWindow().repaint();
            getChatInputEditor().clear();
        }
        catch (Exception ex)
		{
        }

        message.setType(Message.Type.chat);
        message.setTo(participantJID);
        message.setFrom(ChatsyManager.getSessionManager().getJID());
        fireMessageSent(message);
        addToTranscript(message, false);
        getChatInputEditor().setCaretPosition(0);
        getChatInputEditor().requestFocusInWindow();
        scrollToBottom();
        MessageEventManager.addNotificationsRequests(message, true, false, false, true);

        try
		{
            fireOutgoingMessageSending(message);
            ChatsyManager.getConnection().sendPacket(message);
        }
        catch (Exception ex)
		{
        }
    }

    public String getRoomname()
	{
        return roomname;
    }

    public Icon getTabIcon()
	{
        return tabIcon;
    }

    public void setTabIcon(Icon icon)
	{
        this.tabIcon = icon;
    }

    public String getTabTitle()
	{
        return tabTitle;
    }

    public void setTabTitle(String tabTitle)
	{
        this.tabTitle = tabTitle;
    }

    public void setRoomTitle(String roomTitle)
	{
        this.roomTitle = roomTitle;
    }

    public String getRoomTitle()
	{
        return roomTitle;
    }

    public Message.Type getChatType()
	{
        return Message.Type.chat;
    }

    public void leaveChatRoom()
	{
    }

    public boolean isActive()
	{
        return true;
    }

    public String getParticipantJID()
	{
        return participantJID;
    }

    public String getJID()
	{
        presence = PresenceManager.getPresence(getParticipantJID());
        return presence.getFrom();
    }

    public void processPacket(final Packet packet)
	{
        final Runnable runnable = new Runnable()
		{
            public void run()
			{
                if (packet instanceof Presence)
				{
                    presence = (Presence)packet;
                    final Presence presence = (Presence)packet;
                    ContactList list = ChatsyManager.getWorkspace().getContactList();
                    ContactItem contactItem = list.getContactItemByJID(getParticipantJID());
                    String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
                    if (presence.getType() == Presence.Type.unavailable 
						&& contactItem != null)
					{
                        if (!isOnline())
                            getTranscriptWindow().insertNotificationMessage("*** " + participantNickname + " went offline at " + time, ChatManager.NOTIFICATION_COLOR);
                    }
                    else if (presence.getType() == Presence.Type.available)
					{
                        if (!isOnline())
                            getTranscriptWindow().insertNotificationMessage("*** " + participantNickname + " came online at " + time, ChatManager.NOTIFICATION_COLOR);
                    }
                }
                else if (packet instanceof Message)
				{
                    lastActivity = System.currentTimeMillis();
                    final Message message = (Message)packet;
                    if (message.getError() != null)
					{
                        if (message.getError().getCode() == 404)
						{
                            RosterEntry entry = roster.getEntry(participantJID);
                            if (!presence.isAvailable() && !offlineSent && entry != null)
							{
                                getTranscriptWindow().insertNotificationMessage("The user will be unable to receive offline messages", ChatManager.ERROR_COLOR);
                                offlineSent = true;
                            }
                        }
                        return;
                    }

                    RosterEntry entry = roster.getEntry(participantJID);
                    if (!presence.isAvailable()
						&& !offlineSent
						&& entry != null)
					{
                        getTranscriptWindow().insertNotificationMessage("The user is offline and will receive the message on their next login", ChatManager.ERROR_COLOR);
                        offlineSent = true;
                    }

                    if (threadID == null)
					{
                        threadID = message.getThread();
                        if (threadID == null)
                            threadID = StringUtils.randomString(6);
                    }

                    boolean broadcast = message.getProperty("broadcast") != null;
                    if (message.getType() == Message.Type.groupchat 
						|| broadcast
						|| message.getType() == Message.Type.normal
						|| message.getType() == Message.Type.headline)
                        return;

                    final String host = ChatsyManager.getSessionManager().getServerAddress();
                    if (host.equals(message.getFrom()))
                        return;

                    if (message.getBody() != null)
					{
                        participantJID = message.getFrom();
                        insertMessage(message);
                        showTyping(false);
                    }
                }
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    public String getParticipantNickname()
	{
        return participantNickname;
    }

    public void insertUpdate(DocumentEvent e)
	{
        checkForText(e);
        if (!sendTypingNotification)
            return;
        lastTypedCharTime = System.currentTimeMillis();
        if (sendNotification)
		{
            try
			{
                ChatsyManager.getMessageEventManager().sendComposingNotification(getParticipantJID(), threadID);
                sendNotification = false;
            }
            catch (Exception exception)
			{
            }
        }
    }

    public void insertMessage(Message message)
	{
        super.insertMessage(message);
        MessageEvent messageEvent = (MessageEvent)message.getExtension("x", "jabber:x:event");
        if (messageEvent != null)
            checkEvents(message.getFrom(), message.getPacketID(), messageEvent);
        getTranscriptWindow().insertMessage(participantNickname, message, ChatManager.FROM_COLOR, Color.white);
        participantJID = message.getFrom();
    }

    private void checkEvents(String from, String packetID, MessageEvent messageEvent)
	{
        if (messageEvent.isDelivered() 
			|| messageEvent.isDisplayed())
		{
            Message msg = new Message(from);
            MessageEvent event = new MessageEvent();
            if (messageEvent.isDelivered())
                event.setDelivered(true);
            if (messageEvent.isDisplayed())
                event.setDisplayed(true);
            event.setPacketID(packetID);
            msg.addExtension(event);
            ChatsyManager.getConnection().sendPacket(msg);
        }
    }

    public void addMessageEventListener(MessageEventListener listener)
	{
        messageEventListeners.add(listener);
    }

    public void removeMessageEventListener(MessageEventListener listener)
	{
        messageEventListeners.remove(listener);
    }

    public Collection<MessageEventListener> getMessageEventListeners()
	{
        return messageEventListeners;
    }

    public void fireOutgoingMessageSending(Message message)
	{
        for (MessageEventListener messageEventListener : new ArrayList<MessageEventListener>(messageEventListeners))
            messageEventListener.sendingMessage(message);
    }

    public void fireReceivingIncomingMessage(Message message)
	{
        for (MessageEventListener messageEventListener : new ArrayList<MessageEventListener>(messageEventListeners))
            messageEventListener.receivingMessage(message);
    }

    public void showTyping(boolean typing)
	{
        if (typing)
		{
            String isTypingText = participantNickname + " is typing a message...";
            getNotificationLabel().setText(isTypingText);
        }
        else
		{
            getNotificationLabel().setText("");
        }

    }

    public long getLastActivity()
	{
        return lastActivity;
    }

    public Presence getPresence()
	{
        return presence;
    }

    public void setSendTypingNotification(boolean isSendTypingNotification)
	{
        this.sendTypingNotification = isSendTypingNotification;
    }


    public void connectionClosed()
	{
        handleDisconnect();
        String message = "Your connection was closed due to an error";
        getTranscriptWindow().insertNotificationMessage(message, ChatManager.ERROR_COLOR);
    }

    public void connectionClosedOnError(Exception ex)
	{
        handleDisconnect();
        String message = "Your connection was closed due to an error";
        if (ex instanceof XMPPException)
		{
            XMPPException xmppEx = (XMPPException)ex;
            StreamError error = xmppEx.getStreamError();
            String reason = error.getCode();
            if ("conflict".equals(reason))
                message = "Your connection was closed due to the same user logging in from another location";
        }
        getTranscriptWindow().insertNotificationMessage(message, ChatManager.ERROR_COLOR);
    }

    public void reconnectionSuccessful()
	{
        Presence usersPresence = PresenceManager.getPresence(getParticipantJID());
        if (usersPresence.isAvailable())
            presence = usersPresence;
        ChatsyManager.getChatManager().getChatContainer().fireChatRoomStateUpdated(this);
        getChatInputEditor().setEnabled(true);
        getSendButton().setEnabled(true);
    }

    private void handleDisconnect()
	{
        presence = new Presence(Presence.Type.unavailable);
        getChatInputEditor().setEnabled(false);
        getSendButton().setEnabled(false);
        ChatsyManager.getChatManager().getChatContainer().fireChatRoomStateUpdated(this);
    }

    private boolean isOnline()
	{
        return roster.getPresence(getParticipantJID()).isAvailable();
    }

    public void actionPerformed(ActionEvent e)
	{
    }
	
}
