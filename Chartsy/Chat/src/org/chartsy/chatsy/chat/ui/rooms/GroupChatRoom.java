package org.chartsy.chatsy.chat.ui.rooms;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.MessageEventManager;
import org.jivesoftware.smackx.MessageEventNotificationListener;
import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;
import org.jivesoftware.smackx.muc.DefaultUserStatusListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.packet.MUCUser;
import org.jivesoftware.smackx.packet.MUCUser.Destroy;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.plugin.ContextMenuListener;
import org.chartsy.chatsy.chat.ui.ChatContainer;
import org.chartsy.chatsy.chat.ui.ChatFrame;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.conferences.ConferenceUtils;
import org.chartsy.chatsy.chat.ui.conferences.DataFormDialog;
import org.chartsy.chatsy.chat.ui.conferences.GroupChatParticipantList;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.log.Log;

public final class GroupChatRoom extends ChatRoom
{

    private final MultiUserChat chat;

    private final String roomname;
    private Icon tabIcon = null;
    private String tabTitle;
    private boolean isActive = true;
    private SubjectPanel subjectPanel;

    private List<String> currentUserList = new ArrayList<String>();

    private String conferenceService;
    private List<String> blockedUsers = new ArrayList<String>();

    private ChatRoomMessageManager messageManager;
    private Timer typingTimer;
    private int typedChars;

    private GroupChatParticipantList roomInfo;

    private long lastActivity;
    private Message lastMessage;
    
    private boolean showPresenceMessages = true;
    private boolean isMucHighlightingNameEnabled = true;
    private boolean isMucHighlightingTextEnabled = true;

    public GroupChatRoom(final MultiUserChat chat)
	{
        this.chat = chat;
        PacketFilter fromFilter = new FromContainsFilter(chat.getRoom());
        PacketFilter orFilter = new OrFilter(new PacketTypeFilter(Presence.class), new PacketTypeFilter(Message.class));
        PacketFilter andFilter = new AndFilter(orFilter, fromFilter);
        ChatsyManager.getConnection().addPacketListener(this, andFilter);
        roomname = chat.getRoom();
        tabTitle = StringUtils.parseName(StringUtils.unescapeNode(roomname));
        roomInfo = new GroupChatParticipantList();
        getSplitPane().setRightComponent(roomInfo.getGUI());

        roomInfo.setChatRoom(this);
        getSplitPane().setResizeWeight(.60);
        getSplitPane().setDividerLocation(.60);

        setupListeners();
        conferenceService = StringUtils.parseServer(chat.getRoom());
        subjectPanel = new SubjectPanel();
        getToolBar().add(subjectPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 2, 0, 2), 0, 0));

        getTranscriptWindow().addContextMenuListener(new ContextMenuListener()
		{
            public void poppingUp(Object component, JPopupMenu popup)
			{
                popup.addSeparator();
                Action inviteAction = new AbstractAction()
				{
					public void actionPerformed(ActionEvent actionEvent)
					{
                        ConferenceUtils.inviteUsersToRoom(conferenceService, getRoomname(), null);
                    }
                };
                inviteAction.putValue(Action.NAME, "Invite users");
                popup.add(inviteAction);

                Action configureAction = new AbstractAction() 
				{
					public void actionPerformed(ActionEvent actionEvent) {
                        try
						{
                            ChatFrame chatFrame = ChatsyManager.getChatManager().getChatContainer().getChatFrame();
                            Form form = chat.getConfigurationForm().createAnswerForm();
                            DataFormDialog dataFormDialog = new DataFormDialog(chatFrame, chat, form);
                        }
                        catch (XMPPException e)
						{
                            Log.error("Error configuring room.", e);
                        }
                    }
                };
                configureAction.putValue(Action.NAME, "Configure room");
                if (ChatsyManager.getUserManager().isOwner((GroupChatRoom)getChatRoom(), chat.getNickname()))
                    popup.add(configureAction);

                Action subjectAction = new AbstractAction()
				{
					public void actionPerformed(ActionEvent actionEvent)
					{
                        String newSubject = JOptionPane.showInputDialog(
							getChatRoom(),
							"New Subject:",
							"Change subject",
							JOptionPane.QUESTION_MESSAGE);
                        if (ModelUtil.hasLength(newSubject))
						{
                            try
							{
                                chat.changeSubject(newSubject);
                            }
                            catch (XMPPException e)
							{
                                Log.error(e);
                            }
                        }
                    }
                };
                subjectAction.putValue(Action.NAME, "Change subject");
                popup.add(subjectAction);

                Action destroyRoomAction = new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
                        int ok = JOptionPane.showConfirmDialog(
							getChatRoom(),
							"Destroying the room removes all users from the room, continue?",
							"Confirmation",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
                        if (ok == JOptionPane.NO_OPTION)
                            return;

                        String reason = JOptionPane.showInputDialog(
							getChatRoom(),
							"Reason for destroying the room?",
							"Enter reason",
							JOptionPane.QUESTION_MESSAGE);
                        if (ModelUtil.hasLength(reason))
						{
                            try
							{
                                chat.destroy(reason, null);
                                getChatRoom().leaveChatRoom();
                            }
                            catch (XMPPException e1)
							{
                                Log.warning("Unable to destroy room", e1);
                            }
                        }
                    }
                };
                destroyRoomAction.putValue(Action.NAME, "Destroy room");
                if (ChatsyManager.getUserManager().isOwner((GroupChatRoom)getChatRoom(), getNickname()))
                    popup.add(destroyRoomAction);
            }

            public void poppingDown(JPopupMenu popup)
			{
            }

            public boolean handleDefaultAction(MouseEvent e)
			{
                return false;
            }
        });

        messageManager = new ChatRoomMessageManager();
        lastActivity = System.currentTimeMillis();
    }

    public void setTabTitle(String tabTitle)
	{
        this.tabTitle = tabTitle;
    }

    public void hideParticipantList()
	{
        getSplitPane().setRightComponent(null);
    }
    
    public Message getLastMessage()
	{
		return lastMessage;
    }

    public void closeChatRoom()
	{
        super.closeChatRoom();
        ChatsyManager.getConnection().removePacketListener(this);
        ChatContainer container = ChatsyManager.getChatManager().getChatContainer();
        container.leaveChatRoom(this);
        container.closeTab(this);
    }

    private Color getMessageBackground(String nickname, String body)
	{
        String myNickName = chat.getNickname();
        String myUserName = ChatsyManager.getSessionManager().getUsername();
        Pattern usernameMatch = Pattern.compile(myUserName, Pattern.CASE_INSENSITIVE);
        Pattern nicknameMatch = Pattern.compile(myNickName, Pattern.CASE_INSENSITIVE);

        if (isMucHighlightingNameEnabled && myNickName.equalsIgnoreCase(nickname))
            return new Color(244, 248, 255);
		else if (isMucHighlightingTextEnabled && (usernameMatch.matcher(body).find() || nicknameMatch.matcher(body).find()))
            return new Color(255, 255, 153);
		else
            return Color.white;
    }

    public void sendMessage(Message message)
	{
        try
		{
            message.setTo(chat.getRoom());
            message.setType(Message.Type.groupchat);
            MessageEventManager.addNotificationsRequests(message, true, true, true, true);
            addPacketID(message.getPacketID());
            ChatsyManager.getChatManager().filterOutgoingMessage(this, message);
            ChatsyManager.getChatManager().fireGlobalMessageSentListeners(this, message);
            chat.sendMessage(message);
        }
        catch (XMPPException ex)
		{
            Log.error("Unable to send message in conference chat.", ex);
        }

        try
		{
            getTranscriptWindow().insertMessage(getNickname(), message, getColor(getNickname()), getMessageBackground(getNickname(), message.getBody()));
            getChatInputEditor().selectAll();

            getTranscriptWindow().validate();
            getTranscriptWindow().repaint();
            getChatInputEditor().clear();
        }
        catch (Exception ex)
		{
            Log.error("Error sending message", ex);
        }

        fireMessageSent(message);
        addToTranscript(message, false);
        getChatInputEditor().setCaretPosition(0);
        getChatInputEditor().requestFocusInWindow();
        scrollToBottom();
        lastActivity = System.currentTimeMillis();
    }

    public void sendMessageWithoutNotification(Message message)
	{
        try
		{
            message.setTo(chat.getRoom());
            message.setType(Message.Type.groupchat);
            MessageEventManager.addNotificationsRequests(message, true, true, true, true);
            addPacketID(message.getPacketID());
            chat.sendMessage(message);
        }
        catch (XMPPException ex)
		{
            Log.error("Unable to send message in conference chat.", ex);
        }

        try
		{
            getTranscriptWindow().insertMessage(getNickname(), message, getColor(getNickname()), getMessageBackground(getNickname(), message.getBody()));
            getChatInputEditor().selectAll();

            getTranscriptWindow().validate();
            getTranscriptWindow().repaint();
            getChatInputEditor().clear();
        }
        catch (Exception ex)
		{
            Log.error("Error sending message", ex);
        }

        addToTranscript(message, false);
        getChatInputEditor().setCaretPosition(0);
        getChatInputEditor().requestFocusInWindow();
        scrollToBottom();
        lastActivity = System.currentTimeMillis();
    }

    public String getRoomname()
	{
        return roomname;
    }

    public String getNickname()
	{
        return chat.getNickname();
    }

    public void setTabIcon(Icon tabIcon)
	{
        this.tabIcon = tabIcon;
    }

    public Icon getTabIcon()
	{
        return tabIcon;
    }

    public String getTabTitle()
	{
        return tabTitle;
    }

    public String getRoomTitle()
	{
        return getTabTitle();
    }

    public Message.Type getChatType()
	{
        return Message.Type.groupchat;
    }

    public void leaveChatRoom()
	{
        if (!isActive)
            return;
        ChatsyManager.getConnection().removePacketListener(this);
        getChatInputEditor().showAsDisabled();
        disableToolbar();
        getToolBar().setVisible(false);
        getTranscriptWindow().insertNotificationMessage(getNickname() + " has left the room", ChatManager.NOTIFICATION_COLOR);
        try
		{
            chat.leave();
        }
        catch (Exception e)
		{
            Log.error("Closing Group Chat Room error.", e);
        }
        getTranscriptWindow().showWindowDisabled();
        getNotificationLabel().setText("Chat session has ended on " + ChatsyManager.DATE_SECOND_FORMATTER.format(new java.util.Date()));
        getNotificationLabel().setIcon(null);
        getNotificationLabel().setEnabled(false);

        getSplitPane().setRightComponent(null);
        getSplitPane().setDividerSize(0);

        isActive = false;
    }

    public void showPresenceMessages(boolean showMessages)
	{
        showPresenceMessages = showMessages;
    }

    public boolean isActive()
	{
        return isActive;
    }

    public int getParticipantCount()
	{
        if (!isActive)
            return 0;
        return chat.getOccupantsCount();
    }

    public void processPacket(final Packet packet)
	{
        super.processPacket(packet);
        if (packet instanceof Presence)
		{
            SwingUtilities.invokeLater(new Runnable()
			{
                public void run()
				{
                    handlePresencePacket(packet);
                }
            });

        }
        if (packet instanceof Message)
		{
            SwingUtilities.invokeLater(new Runnable()
			{
                public void run()
				{
                    handleMessagePacket(packet);
                    lastActivity = System.currentTimeMillis();
                }
            });
        }
    }

    private void handleMessagePacket(Packet packet)
	{
        final Message message = (Message)packet;
        lastMessage = message;
        if (message.getType() == Message.Type.groupchat)
		{
            DelayInformation inf = (DelayInformation)message.getExtension("x", "jabber:x:delay");
            Date sentDate;
            if (inf != null)
                sentDate = inf.getStamp();
            else
                sentDate = new Date();

            String host = ChatsyManager.getSessionManager().getServerAddress();
            if (host.equals(message.getFrom()))
                return;

            String messageNickname = StringUtils.parseResource(message.getFrom());
            boolean isFromMe = messageNickname.equals(getNickname()) && inf == null;
            if (ModelUtil.hasLength(message.getBody()) && !isFromMe)
			{
                super.insertMessage(message);
                String from = StringUtils.parseResource(message.getFrom());
                if (inf != null)
                    getTranscriptWindow().insertHistoryMessage(from, message.getBody(), sentDate);
                else
				{
                    if (isBlocked(message.getFrom()))
                        return;
                    boolean isFromRoom = message.getFrom().indexOf("/") == -1;
                    if (!ChatsyManager.getUserManager().hasVoice(this, StringUtils.parseResource(message.getFrom())) && !isFromRoom)
                        return;
                    getTranscriptWindow().insertMessage(from, message, getColor(from), getMessageBackground(from, message.getBody()));
                }

                if (typingTimer != null)
                    showDefaultTabIcon();
            }
        }
        else if (message.getType() == Message.Type.chat)
		{
            ChatRoom chatRoom = ChatsyManager.getChatManager().getChatContainer().getChatRoom(message.getFrom());
			if (chatRoom != null)
				if (message.getBody() != null)
					chatRoom.insertMessage(message);
			else
			{
				String userNickname = StringUtils.parseResource(message.getFrom());
                String roomTitle = userNickname + " - " + StringUtils.parseName(getRoomname());

                chatRoom = new ChatRoomImpl(message.getFrom(), userNickname, roomTitle);
                ChatsyManager.getChatManager().getChatContainer().addChatRoom(chatRoom);

                ChatsyManager.getChatManager().getChatContainer().activateChatRoom(chatRoom);
                if (message.getBody() != null)
                    chatRoom.insertMessage(message);
			}

        }
        else if (message.getError() != null)
		{
            String errorMessage = "";
            if (message.getError().getCode() == 403 && message.getSubject() != null)
                errorMessage = "You are not allowed to change the subject of this room";
            else if (message.getError().getCode() == 403)
                errorMessage = "Received a forbidden error from the server";
            if (ModelUtil.hasLength(errorMessage))
                getTranscriptWindow().insertNotificationMessage(errorMessage, ChatManager.ERROR_COLOR);
        }
    }

    private void handlePresencePacket(Packet packet)
	{
        Presence presence = (Presence)packet;
        if (presence.getError() != null)
            return;

        final String from = presence.getFrom();
        final String nickname = StringUtils.parseResource(from);

        MUCUser mucUser = (MUCUser)packet.getExtension("x", "http://jabber.org/protocol/muc#user");
        String code = "";
        if (mucUser != null)
		{
            code = mucUser.getStatus() != null ? mucUser.getStatus().getCode() : "";
            Destroy destroy = mucUser.getDestroy();
            if (destroy != null)
			{
                String reason = destroy.getReason();
                JOptionPane.showMessageDialog(
					this,
					"This room has been destroyed due to the following reason: " + reason,
					"Room destroyed",
					JOptionPane.INFORMATION_MESSAGE);
                leaveChatRoom();
                return;
            }
        }

        if (presence.getType() == Presence.Type.unavailable && !"303".equals(code))
		{
            if (currentUserList.contains(from))
			{
                if (showPresenceMessages)
				{
                    getTranscriptWindow().insertNotificationMessage(nickname + " has left the room", ChatManager.NOTIFICATION_COLOR);
                    scrollToBottom();
                }
                currentUserList.remove(from);
            }
        }
        else
		{
            if (!currentUserList.contains(from))
			{
                currentUserList.add(from);
                getChatInputEditor().setEnabled(true);
                if (showPresenceMessages)
				{
                    getTranscriptWindow().insertNotificationMessage(nickname + " has joined the room", ChatManager.NOTIFICATION_COLOR);
                    scrollToBottom();
                }
            }
        }
    }

    private void setupListeners()
	{
        chat.addParticipantStatusListener(new DefaultParticipantStatusListener()
		{
            public void kicked(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText(nickname + " has been kicked out of the room");
            }

            public void voiceGranted(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText(nickname + " has been given a voice in this room");
            }

            public void voiceRevoked(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText("Voice has been revoked for " + nickname);
            }

            public void banned(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText(nickname + " has been banned from this room");
            }

            public void membershipGranted(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText(nickname + " has been given membership privileges");
            }

            public void membershipRevoked(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText("Membership has been revoked for " + nickname);
            }

            public void moderatorGranted(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText(nickname + " has been granted moderator privileges");
            }

            public void moderatorRevoked(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText("Moderator privileges have been revoked for " + nickname);
            }

            public void ownershipGranted(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText(nickname + " has been granted owner privileges");
            }

            public void ownershipRevoked(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText("Owner privileges have been revoked for " + nickname);
            }

            public void adminGranted(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText(nickname + " has been granted administrator privileges");
            }

            public void adminRevoked(String participant)
			{
                String nickname = StringUtils.parseResource(participant);
                insertText("Administrator privileges have been revoked for " + nickname);
            }

            public void nicknameChanged(String participant, String nickname)
			{
                insertText(StringUtils.parseResource(participant) + " is now known as " + nickname);
            }
        });

        chat.addUserStatusListener(new DefaultUserStatusListener()
		{
            public void kicked(String s, String reason)
			{
                if (ModelUtil.hasLength(reason))
                    insertText(reason);
                else
                    insertText("You have been kicked by " + s);

                getChatInputEditor().setEnabled(false);
                getSplitPane().setRightComponent(null);
                leaveChatRoom();
            }

            public void voiceGranted()
			{
                insertText("You have been given a voice in this chat");
                getChatInputEditor().setEnabled(true);
            }

            public void voiceRevoked()
			{
                insertText("Your voice has been revoked");
                getChatInputEditor().setEnabled(false);
            }

            public void banned(String s, String reason)
			{
                insertText("You have been banned from this room");
            }

            public void membershipGranted()
			{
                insertText("You have been granted membership privileges");
            }

            public void membershipRevoked()
			{
                insertText("Your membership has been revoked");
            }

            public void moderatorGranted()
			{
                insertText("You have been granted moderator privileges");
            }

            public void moderatorRevoked()
			{
                insertText("Your moderator privileges have been revoked");
            }

            public void ownershipGranted()
			{
                insertText("Your have been granted owner privileges");
            }

            public void ownershipRevoked()
			{
                insertText("Your owner privileges have been revoked");
            }

            public void adminGranted()
			{
                insertText("You have been granted administrator privileges");
            }

            public void adminRevoked()
			{
                insertText("Your admin privileges have been revoked");
            }
        });

        chat.addSubjectUpdatedListener(new SubjectListener());
    }

    public void insertText(String text)
	{
        getTranscriptWindow().insertNotificationMessage(text, ChatManager.NOTIFICATION_COLOR);
    }

    private class SubjectListener implements SubjectUpdatedListener
	{
        public void subjectUpdated(String subject, String by)
		{
            subjectPanel.setSubject(subject);
            subjectPanel.setToolTipText(subject);
            String nickname = StringUtils.parseResource(by);

            String insertMessage = nickname + " changed the subject to \"" + subject + "\" ";
            getTranscriptWindow().insertNotificationMessage(insertMessage, ChatManager.NOTIFICATION_COLOR);

        }
    }

    public Collection<String> getParticipants()
	{
        return currentUserList;
    }

    public void sendMessage()
	{
        final String text = getChatInputEditor().getText();
        sendMessage(text);
    }

    public void sendMessage(String text)
	{
        if (!ModelUtil.hasLength(text))
            return;
        Message message = new Message();
        message.setBody(text);
        sendMessage(message);
    }

    public MultiUserChat getMultiUserChat()
	{
        return chat;
    }

    public String getConferenceService()
	{
        return conferenceService;
    }

    public void addBlockedUser(String usersJID)
	{
        blockedUsers.add(usersJID);
    }

    public void removeBlockedUser(String usersJID)
	{
        blockedUsers.remove(usersJID);
    }

    public boolean isBlocked(String usersJID)
	{
        return blockedUsers.contains(usersJID);
    }

    public void setSendAndReceiveTypingNotifications(boolean sendAndReceiveTypingNotifications)
	{
        if (sendAndReceiveTypingNotifications)
		{
            typingTimer = new Timer(10000, new ActionListener()
			{
                public void actionPerformed(ActionEvent e)
				{
                    showDefaultTabIcon();
                }
            });
            ChatsyManager.getMessageEventManager().addMessageEventNotificationListener(messageManager);
        }
        else
		{
            if (typingTimer != null)
                typingTimer.stop();
            ChatsyManager.getMessageEventManager().removeMessageEventNotificationListener(messageManager);
        }
    }

    public void inviteUser(String jid, String message)
	{
        message = message != null ? message : "Please join me in a conference";
        getMultiUserChat().invite(jid, message);
        roomInfo.addInvitee(jid, message);
    }

    private class ChatRoomMessageManager implements MessageEventNotificationListener
	{

        ChatRoomMessageManager()
		{
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
                    String bareAddress = StringUtils.parseBareAddress(from);
                    if (bareAddress.equals(getRoomname()))
                        showUserIsTyping();
                }
            });
        }

        public void offlineNotification(String from, String packetID)
		{
        }

        public void cancelledNotification(String from, String packetID)
		{
        }
		
    }

    private void showUserIsTyping()
	{
        ChatsyManager.getChatManager().addTypingNotification(this);
        typingTimer.restart();
        ChatsyManager.getChatManager().notifyChatsyTabHandlers(this);
    }

    private void showDefaultTabIcon()
	{
        ChatsyManager.getChatManager().removeTypingNotification(this);
        ChatsyManager.getChatManager().notifyChatsyTabHandlers(this);
    }

    public void insertUpdate(DocumentEvent e)
	{
        checkForText(e);
        typedChars++;
        if (typedChars >= 10)
		{
            try
			{
                if (typingTimer != null)
				{
                    final Iterator<String> iter = chat.getOccupants();
                    while (iter.hasNext())
					{
                        String from = iter.next();
                        String tFrom = StringUtils.parseResource(from);
                        String nickname = chat.getNickname();
                        if (tFrom != null && !tFrom.equals(nickname))
                            ChatsyManager.getMessageEventManager().sendComposingNotification(from, "djn");
                    }
                }
                typedChars = 0;
            }
            catch (Exception exception)
			{
                Log.error("Error updating", exception);
            }
        }
    }

    public GroupChatParticipantList getConferenceRoomInfo()
	{
        return roomInfo;
    }

    public long getLastActivity()
	{
        return lastActivity;
    }

    public void connectionClosed()
	{
        handleDisconnect();
    }

    public void connectionClosedOnError(Exception ex)
	{
        handleDisconnect();
        getTranscriptWindow().showWindowDisabled();
        getSplitPane().setRightComponent(null);
        String message = "Your connection was closed due to an error, you will need to re-join this room after reconnecting";
        getTranscriptWindow().insertNotificationMessage(message, ChatManager.ERROR_COLOR);
    }

    private void handleDisconnect()
	{
        getChatInputEditor().setEnabled(false);
        getSendButton().setEnabled(false);
        ChatsyManager.getChatManager().getChatContainer().fireChatRoomStateUpdated(this);
    }

    public class SubjectPanel extends JPanel
	{

		private JLabel roomJIDLabel;
        private JLabel subjectLabel;

        public SubjectPanel()
		{
            setLayout(new GridBagLayout());
            roomJIDLabel = new JLabel("<" + getMultiUserChat().getRoom() + ">");
            subjectLabel = new JLabel(getMultiUserChat().getSubject());
            add(roomJIDLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(2, 2, 0, 2), 0, 0));
            add(subjectLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, 
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 2, 0, 2), 0, 0));
            setOpaque(false);
        }

        public void setSubject(String subject)
		{
            subjectLabel.setText(subject);
        }

        public void setRoomLabel(String label)
		{
            roomJIDLabel.setText(label);
        }
		
    }

    public SubjectPanel getSubjectPanel()
	{
        return subjectPanel;
    }

    public Color getColor(String nickname)
	{
        int index = 0;
        for (int i = 0; i < nickname.length(); i++)
            index += nickname.charAt(i) * i;
        return ChatManager.COLORS[index % ChatManager.COLORS.length];
    }
	
}
