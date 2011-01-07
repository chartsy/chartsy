package org.chartsy.chatsy.chat.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.chartsy.chatsy.MainWindow;
import org.chartsy.chatsy.Chatsy;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.tabbedPane.ChatsyTab;
import org.chartsy.chatsy.chat.component.tabbedPane.ChatsyTabbedPane;
import org.chartsy.chatsy.chat.component.tabbedPane.ChatsyTabbedPaneListener;
import org.chartsy.chatsy.chat.ui.rooms.ChatRoomImpl;
import org.chartsy.chatsy.chat.ui.rooms.GroupChatRoom;
import org.chartsy.chatsy.chat.util.SwingTimerTask;
import org.chartsy.chatsy.chat.util.TaskEngine;

public class ChatContainer extends ChatsyTabbedPane
	implements MessageListener, ChangeListener
{

	private static final Logger LOG = Logger.getLogger(ChatContainer.class.getName());
    private final List<ChatRoomListener> chatRoomListeners = new ArrayList<ChatRoomListener>();
    private final List<ChatRoom> chatRoomList = new ArrayList<ChatRoom>();
    private final Map<String, PacketListener> presenceMap = new HashMap<String, PacketListener>();
    private static final String WELCOME_TITLE = "welcome";
    private ChatFrame chatFrame;
    private final TimerTask focusTask;

    public ChatContainer()
	{
        super(JTabbedPane.TOP);
        addChatsyTabbedPaneListener(new ChatsyTabbedPaneListener()
		{
            @Override public void tabRemoved(ChatsyTab tab, Component component, int index)
			{
                stateChanged(null);
                if (component instanceof ChatRoom) 
                    cleanupChatRoom((ChatRoom)component);
                else if (component instanceof ContainerComponent) 
                    ((ContainerComponent)component).closing();
            }
            @Override public void tabAdded(ChatsyTab tab, Component component, int index)
			{
                stateChanged(null);
            }
            @Override public void tabSelected(ChatsyTab tab, Component component, int index)
			{
                stateChanged(null);
                if (component instanceof ChatRoom) 
                    fireChatRoomActivated((ChatRoom)component);
            }
            @Override public void allTabsRemoved()
			{
                chatFrame.setTitle("");
                chatFrame.setVisible(false);
            }
            @Override public boolean canTabClose(ChatsyTab tab, Component component)
			{
                return true;
            }
        });

        setCloseButtonEnabled(true);
        setFocusable(false);
        setOpaque(true);
        setBackground(Color.white);
        focusTask = new SwingTimerTask()
		{
            @Override public void doRun()
			{
                try
				{
                    ChatRoom chatRoom = getActiveChatRoom();
                    chatRoom.requestFocusInWindow();
                    chatRoom.getChatInputEditor().requestFocusInWindow();
                }
                catch (ChatRoomNotFoundException e)
				{
                }
            }
        };

    }

    public synchronized void addChatRoom(final ChatRoom room)
	{
        createFrameIfNeeded();
        AndFilter presenceFilter = new AndFilter(new PacketTypeFilter(Presence.class), new FromContainsFilter(room.getRoomname()));
        PacketListener myListener = new PacketListener()
		{
            @Override public void processPacket(final Packet packet)
			{
                SwingUtilities.invokeLater(new Runnable()
				{
                    @Override public void run()
					{
                        handleRoomPresence((Presence)packet);
                    }
                });
            }
        };

        ChatsyManager.getConnection().addPacketListener(myListener, presenceFilter);
        presenceMap.put(room.getRoomname(), myListener);

        String tooltip;
        if (room instanceof ChatRoomImpl)
		{
            tooltip = ((ChatRoomImpl)room).getParticipantJID();
            String nickname = ChatsyManager.getUserManager().getUserNicknameFromJID(((ChatRoomImpl)room).getParticipantJID());
            tooltip = "<html><body><b>Contact:&nbsp;</b>" + nickname + "<br><b>JID:&nbsp;</b>" + tooltip;
        }
        else
		{
            tooltip = room.getRoomname();
        }

        ChatsyTab tab = addTab(room.getTabTitle(), room.getTabIcon(), room, tooltip);
        tab.addMouseListener(new MouseAdapter()
		{
            @Override public void mouseReleased(MouseEvent e)
			{
                checkTabPopup(e);
            }
            @Override public void mousePressed(MouseEvent e)
			{
                checkTabPopup(e);
            }
        });

        room.addMessageListener(this);

        final String title = getTabAt(0).getActualText();
        if (title.equals(WELCOME_TITLE)) 
            chatFrame.setTitle(room.getRoomTitle());

        final TimerTask visibleTask = new SwingTimerTask()
		{
            @Override public void doRun()
			{
                checkVisibility(room);
            }
        };

        TaskEngine.getInstance().schedule(visibleTask, 100);
        chatRoomList.add(room);
        fireChatRoomOpened(room);
        focusChat();
    }

    public void addContainerComponent(ContainerComponent comp)
	{
        createFrameIfNeeded();
        addTab(comp.getTabTitle(), comp.getTabIcon(), comp.getGUI(), comp.getToolTipDescription());
        chatFrame.setTitle(comp.getFrameTitle());
        checkVisibility(comp.getGUI());

        if (getSelectedComponent() != comp) 
            ChatsyManager.getChatManager().notifyChatsyTabHandlers(comp.getGUI());
    }

    private void handleRoomPresence(final Presence p)
	{
        final String roomname = StringUtils.parseBareAddress(p.getFrom());
        ChatRoom chatRoom = getChatRoom(roomname);
		if (chatRoom == null)
			return;

        final String userid = StringUtils.parseResource(p.getFrom());
        if (p.getType() == Presence.Type.unavailable) 
            fireUserHasLeft(chatRoom, userid);
        else if (p.getType() == Presence.Type.available) 
            fireUserHasJoined(chatRoom, userid);

        if (chatRoom instanceof ChatRoomImpl)
		{
            int tabLoc = indexOfComponent(chatRoom);
            if (tabLoc != -1) 
                ChatsyManager.getChatManager().notifyChatsyTabHandlers(chatRoom);
        }
    }

    private void checkVisibility(Component component)
	{
        if (!chatFrame.isVisible() && ChatsyManager.getMainWindow().isFocusOwner())
		{
            chatFrame.setState(Frame.NORMAL);
            chatFrame.setVisible(true);
        }
        else if (chatFrame.isVisible() && !chatFrame.isInFocus())
		{
            startFlashing(component, false, null, null);
        }
        else if (chatFrame.isVisible() && chatFrame.getState() == Frame.ICONIFIED)
		{
            int tabLocation = indexOfComponent(component);
            setSelectedIndex(tabLocation);
            startFlashing(component, false, null, null);
        }
        else if (chatFrame.isVisible() && !ChatsyManager.getMainWindow().isVisible())
		{
            startFlashing(component, false, null, null);
        }
        else if (!chatFrame.isVisible())
		{
            int tabLocation = indexOfComponent(component);
            setSelectedIndex(tabLocation);

            if (Chatsy.isWindows())
			{
                chatFrame.setFocusableWindowState(false);
                chatFrame.setState(Frame.ICONIFIED);
            }
            chatFrame.setVisible(true);
            chatFrame.setFocusableWindowState(true);

            if (!ChatsyManager.getMainWindow().isVisible())
                startFlashing(component, false, null, null);
            else if (chatFrame.getState() == Frame.ICONIFIED)
                startFlashing(component, false, null, null);

            if (component instanceof ChatRoom)
                chatFrame.setTitle(((ChatRoom)component).getRoomTitle());
        }
    }
    
    private void handleMessageNotification(final ChatRoom chatRoom, boolean customMsg, String customMsgText, String customMsgTitle)
	{
        ChatRoom activeChatRoom = null;
        try
		{
            activeChatRoom = getActiveChatRoom();
        }
        catch (ChatRoomNotFoundException e)
		{
            LOG.log(Level.SEVERE, "", e);
        }
                
        if (chatFrame.isVisible()
			&& (chatFrame.getState() == Frame.ICONIFIED
			|| chatFrame.getInactiveTime() > 20000))
		{
            int tabLocation = indexOfComponent(chatRoom);
            setSelectedIndex(tabLocation);
            groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
            return;
        }

        if (!chatFrame.isVisible() 
			&& ChatsyManager.getMainWindow().isFocusOwner())
		{
            chatFrame.setState(Frame.NORMAL);
            chatFrame.setVisible(true);
        }
        else if (chatFrame.isVisible()
			&& !chatFrame.isInFocus())
		{
            groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
        }
        else if (chatFrame.isVisible()
			&& chatFrame.getState() == Frame.ICONIFIED)
		{
            int tabLocation = indexOfComponent(chatRoom);
            setSelectedIndex(tabLocation);
            groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
        }
        else if (chatFrame.isVisible()
			&& !ChatsyManager.getMainWindow().isVisible()
			&& !chatFrame.isInFocus())
		{
            groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
        }
        else if (!chatFrame.isVisible())
		{
            int tabLocation = indexOfComponent(chatRoom);
            setSelectedIndex(tabLocation);
    
            if (Chatsy.isWindows())
            	chatFrame.setExtendedState(Frame.ICONIFIED);
            chatFrame.setVisible(true);
            
            if (!ChatsyManager.getMainWindow().isVisible())
                groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
            else if (chatFrame.getState() == Frame.ICONIFIED) 
                groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);

            chatFrame.setTitle(chatRoom.getRoomTitle());
        }
        else if (chatRoom != activeChatRoom)
		{
            groupChatMessageCheck(chatRoom, customMsg, customMsgText, customMsgTitle);
        }
    }

    private void cleanupChatRoom(ChatRoom room)
	{
        if (room.isActive())
		{
            room.leaveChatRoom();
            room.closeChatRoom();
        }

        final PacketListener listener = presenceMap.get(room.getRoomname());
        if (listener != null) 
            ChatsyManager.getConnection().removePacketListener(listener);

        fireChatRoomClosed(room);
        room.removeMessageListener(this);

        presenceMap.remove(room.getRoomname());
        chatRoomList.remove(room);
        room.getTranscriptWindow().cleanup();
    }

    public void closeAllChatRooms()
	{
        for (ChatRoom chatRoom : new ArrayList<ChatRoom>(chatRoomList))
		{
            closeTab(chatRoom);
            chatRoom.closeChatRoom();
        }

        for (int i = 0; i < getTabCount(); i++)
		{
            Component comp = getComponentAt(i);
            if (comp instanceof ContainerComponent) 
                ((ContainerComponent)comp).closing();
            closeTab(comp);
        }
    }

    public void leaveChatRoom(ChatRoom room)
	{
        fireChatRoomLeft(room);
        room.leaveChatRoom();
        final PacketListener listener = presenceMap.get(room.getRoomname());
        if (listener != null
			&& ChatsyManager.getConnection().isConnected())
            ChatsyManager.getConnection().removePacketListener(listener);
    }

    public ChatRoom getChatRoom(String roomName) 
	{
        for (int i = 0; i < getTabCount(); i++)
		{
            ChatRoom room = getChatRoom(i);
            if (room != null 
				&& room.getRoomname().equalsIgnoreCase(roomName)
				&& room.isActive()) 
                return room;
        }
		return null;
    }

    public ChatRoom getChatRoom(int location)
	{
        if (getTabCount() < location) 
        	return null;
		
        try
		{
            Component comp = getComponentAt(location);
            if (comp instanceof ChatRoom) 
                return (ChatRoom)comp;
        }
        catch (ArrayIndexOutOfBoundsException outOfBoundsEx)
		{
            LOG.log(Level.SEVERE, "", outOfBoundsEx);
        }

		return null;
    }

    public ChatRoom getActiveChatRoom() throws ChatRoomNotFoundException
	{
        int location = getSelectedIndex();
        if (location != -1) 
            return getChatRoom(location);
        throw new ChatRoomNotFoundException();
    }

    public Component getActiveRoom()
	{
        int location = getSelectedIndex();
        if (location != -1) 
            return getComponentAt(location);
        return null;
    }

    public void activateChatRoom(ChatRoom room)
	{
        int tabLocation = indexOfComponent(room);
        setSelectedIndex(tabLocation);
        chatFrame.bringFrameIntoFocus();
        focusChat();
    }

    public void activateComponent(Component component)
	{
        int tabLocation = indexOfComponent(component);
        if (tabLocation != -1) 
            setSelectedIndex(tabLocation);
        chatFrame.bringFrameIntoFocus();
        focusChat();
    }

    public void messageReceived(ChatRoom room, Message message)
	{
        room.increaseUnreadMessageCount();
        fireNotifyOnMessage(room, false, null, null);
    }
    
    public void fireNotifyOnMessage(final ChatRoom chatRoom, final boolean customMsg, final String customMsgText, final String customMsgTitle)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            @Override public void run()
			{
                handleMessageNotification(chatRoom, customMsg, customMsgText, customMsgTitle);
            }
        });
    }

    public void messageSent(ChatRoom room, Message message)
	{
        fireChatRoomStateUpdated(room);
    }

    public void stateChanged(ChangeEvent e)
	{
        if (chatFrame.isInFocus()) 
            stopFlashing();

        final Object o = getSelectedComponent();
        if (o instanceof ChatRoom)
		{
            final ChatRoom room = (ChatRoom)o;
            focusChat();
            chatFrame.setTitle(room.getRoomTitle());
        }
        else if (o instanceof ContainerComponent)
		{
            final ContainerComponent comp = (ContainerComponent)o;
            chatFrame.setTitle(comp.getFrameTitle());
            chatFrame.setIconImage(comp.getTabIcon().getImage());
            ChatsyManager.getChatManager().notifyChatsyTabHandlers(comp.getGUI());
        }
    }


    private void stopFlashing()
	{
        try
		{
            int selectedIndex = getSelectedIndex();
            if (selectedIndex != -1)
			{
                Component comp = getComponentAt(selectedIndex);
                if (comp != null) 
                    stopFlashing(comp);
            }
        }
        catch (Exception e)
		{
            LOG.log(Level.SEVERE, "", e);
        }
    }

    public void closeTab(Component component)
	{
        int location = indexOfComponent(component);
        if (location == -1) 
            return;
        if (getTabCount() == 0) 
            chatFrame.setTitle("");
        this.removeTabAt(location);
    }

    public void closeActiveRoom()
	{
        ChatRoom room;
        try
		{
            room = getActiveChatRoom();
        }
        catch (ChatRoomNotFoundException e)
		{
            Component comp = getActiveRoom();
            if (comp != null)
			{
                boolean canClose = ((ContainerComponent)comp).closing();
                if (canClose) 
                    closeTab(comp);
            }
            return;
        }

        boolean isGroupChat = room.getChatType() == Message.Type.groupchat;
        if (isGroupChat)
		{
            final int ok = JOptionPane.showConfirmDialog(
				chatFrame,
				"Would you like to end this session?",
				"Confirmation",
				JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.OK_OPTION) 
                room.closeChatRoom();
        }
        else
		{
            room.closeChatRoom();
        }
    }

    @Override public String toString()
	{
        StringBuilder buf = new StringBuilder();
        for (ChatRoom room : chatRoomList) 
            buf.append("Roomname=").append(room.getRoomname()).append("\n");
        return buf.toString();
    }

    public boolean hasRooms()
	{
        int count = getSelectedIndex();
        return count != -1;
    }

    public void addChatRoomListener(ChatRoomListener listener)
	{
        if (!chatRoomListeners.contains(listener)) 
            chatRoomListeners.add(listener);
    }

    public void removeChatRoomListener(ChatRoomListener listener)
	{
        chatRoomListeners.remove(listener);
    }

    protected void fireChatRoomOpened(ChatRoom room)
	{
        for (ChatRoomListener chatRoomListener : new ArrayList<ChatRoomListener>(chatRoomListeners)) 
            chatRoomListener.chatRoomOpened(room);
    }

    protected void fireChatRoomLeft(ChatRoom room)
	{
        for (ChatRoomListener chatRoomListener : new HashSet<ChatRoomListener>(chatRoomListeners)) 
            chatRoomListener.chatRoomLeft(room);
    }

    protected void fireChatRoomClosed(ChatRoom room)
	{
        for (ChatRoomListener chatRoomListener : new HashSet<ChatRoomListener>(chatRoomListeners)) 
            chatRoomListener.chatRoomClosed(room);
    }

    protected void fireChatRoomActivated(ChatRoom room)
	{
        for (ChatRoomListener chatRoomListener : new HashSet<ChatRoomListener>(chatRoomListeners)) 
            chatRoomListener.chatRoomActivated(room);
    }

    protected void fireUserHasJoined(final ChatRoom room, final String userid)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            @Override public void run()
			{
                for (ChatRoomListener chatRoomListener : new HashSet<ChatRoomListener>(chatRoomListeners))
                    chatRoomListener.userHasJoined(room, userid);
            }
        });

    }

    protected void fireUserHasLeft(final ChatRoom room, final String userid)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            @Override public void run()
			{
                for (ChatRoomListener chatRoomListener : new HashSet<ChatRoomListener>(chatRoomListeners))
                    chatRoomListener.userHasLeft(room, userid);
            }
        });

    }

    public void startFlashing(final Component comp, final boolean customMsg, final String customMsgText, final String customMsgTitle)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            @Override public void run()
			{
                try
				{
                    final int index = indexOfComponent(comp);
                    if (index != -1)
					{
                        if (!(comp instanceof GroupChatRoom))
						{
                            if (comp instanceof ChatRoom) 
							{
								if(comp instanceof GroupChatRoom)
								{
									if(!((GroupChatRoom) comp).isBlocked(((GroupChatRoom) comp).getLastMessage().getFrom()))
										checkNotificationPreferences((ChatRoom)comp, customMsg, customMsgText, customMsgTitle);
								}
								else
								{
									checkNotificationPreferences((ChatRoom)comp, customMsg, customMsgText, customMsgTitle);
								}
                            }
                        }
                        ChatsyManager.getChatManager().notifyChatsyTabHandlers(comp);
                    }

                    if (!chatFrame.isInFocus()) 
                        ChatsyManager.getNativeManager().flashWindow(chatFrame);
                }
                catch (Exception ex)
				{
                    LOG.log(Level.SEVERE, "", ex);
                }
            }
        });
    }

    public void fireChatRoomStateUpdated(final ChatRoom room)
	{
        final int index = indexOfComponent(room);
        if (index != -1) 
            ChatsyManager.getChatManager().notifyChatsyTabHandlers(room);
    }

    public void stopFlashing(final Component component)
	{
        SwingUtilities.invokeLater(new Runnable()
		{
            @Override public void run()
			{
                try
				{
                    ChatsyManager.getNativeManager().stopFlashing(chatFrame);
                    ChatsyManager.getChatManager().notifyChatsyTabHandlers(component);
                }
                catch (Exception ex)
				{
                    LOG.log(Level.SEVERE, "", ex);
                }


            }
        });
    }

    private void checkNotificationPreferences(final ChatRoom room, boolean customMsg, String customMsgText, String customMsgTitle)
	{
		chatFrame.setState(Frame.NORMAL);
		chatFrame.setVisible(true);
    }

    private void groupChatMessageCheck(ChatRoom chatRoom, boolean customMsg, String customMsgText, String customMsgTitle)
	{
        boolean isGroupChat = chatRoom.getChatType() == Message.Type.groupchat;
        int size = chatRoom.getTranscripts().size();

        if (isGroupChat)
		{
            Message lastChatMessage = chatRoom.getTranscripts().get(size - 1);
            String mucNickNameT = lastChatMessage.getFrom();
            String[] mucNickName = mucNickNameT.split("/");
            String finalRoomName = chatRoom.getRoomTitle();
            
                String myNickName = chatRoom.getNickname();
                String myUserName = ChatsyManager.getSessionManager().getUsername();
                Pattern usernameMatch = Pattern.compile(myUserName, Pattern.CASE_INSENSITIVE);
                Pattern nicknameMatch = Pattern.compile(myNickName, Pattern.CASE_INSENSITIVE);
                
                if (usernameMatch.matcher(lastChatMessage.getBody()).find() 
					|| nicknameMatch.matcher(lastChatMessage.getBody()).find())
				{
                    boolean customMsgS = true;
                    String customMsgTextS = "Your name has been said in group chat: "
						+ finalRoomName + " by " + mucNickName[1] + " (" + lastChatMessage + ")";
                    String customMsgTitleS = "Seen your name...";
                    startFlashing(chatRoom, customMsgS, customMsgTextS, customMsgTitleS);
                    return;
                } 
				else
				{
                    boolean customMsgS = true;
                    String customMsgTextS = mucNickName[1] + " says: " + lastChatMessage.getBody();
                    String customMsgTitleS = finalRoomName;                    
                    startFlashing(chatRoom, customMsgS, customMsgTextS, customMsgTitleS);
                    return;
                }
        } 
		else if (customMsg)
		{
            boolean customMsgS = customMsg;
            String customMsgTextS = customMsgText;
            String customMsgTitleS = customMsgTitle;
            startFlashing(chatRoom, customMsgS, customMsgTextS, customMsgTitleS);
            return;
        } 
		else
		{
            Message lastChatMessage = chatRoom.getTranscripts().get(size - 1);
            String finalRoomName = chatRoom.getRoomTitle();            
            boolean customMsgS = true;
            String customMsgTextS = lastChatMessage.getBody();
            String customMsgTitleS = finalRoomName;
            startFlashing(chatRoom, customMsgS, customMsgTextS, customMsgTitleS);
            return;
        }
    }
    
    public void setChatRoomTitle(ChatRoom room, String title)
	{
        int index = indexOfComponent(room);
        if (index != -1)
		{
            ChatsyTab tab = getTabAt(index);
            fireChatRoomStateUpdated(room);
            tab.setTabTitle(room.getTabTitle());
        }
    }

    private void createFrameIfNeeded()
	{
        if (chatFrame != null)
            return;
        
		chatFrame = new ChatFrame();
        chatFrame.addWindowListener(new WindowAdapter()
		{
            @Override public void windowActivated(WindowEvent windowEvent)
			{
                stopFlashing();
                int sel = getSelectedIndex();
                if (sel == -1) 
                    return;

                final ChatRoom room = getChatRoom(sel);
				if (room != null)
				{
					focusChat();
                    chatFrame.setTitle(room.getRoomTitle());
				}
            }
            @Override public void windowDeactivated(WindowEvent windowEvent)
			{
            }
            @Override public void windowClosing(WindowEvent windowEvent)
			{
                ChatsyManager.getChatManager().getChatContainer().closeAllChatRooms();
                chatFrame.setVisible(false);
            }
        });
    }

    public void focusChat()
	{
        TaskEngine.getInstance().schedule(focusTask, 50);
    }

    public Collection<ChatRoom> getChatRooms()
	{
        return new ArrayList<ChatRoom>(chatRoomList);
    }

    public ChatFrame getChatFrame()
	{
        return chatFrame;
    }

    public void blinkFrameIfNecessary(final JFrame frame)
	{
        final MainWindow mainWindow = ChatsyManager.getMainWindow();
        if (mainWindow.isFocusOwner())
		{
            frame.setVisible(true);
        }
        else
		{
            if (Chatsy.isWindows())
			{
                frame.setState(Frame.ICONIFIED);
                ChatsyManager.getNativeManager().flashWindow(frame);
                frame.setVisible(true);
                frame.addWindowListener(new WindowAdapter()
				{
                    @Override public void windowActivated(WindowEvent e)
					{
                        ChatsyManager.getNativeManager().stopFlashing(frame);
                    }
                });
            }
        }
    }

    private void checkTabPopup(MouseEvent e)
	{
        final ChatsyTab tab = (ChatsyTab)e.getSource();
        if (!e.isPopupTrigger()) 
            return;

        final JPopupMenu popup = new JPopupMenu();

        Action closeThisAction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
                ChatRoom chatRoom = (ChatRoom)getComponentInTab(tab);
                if (chatRoom != null) 
                    closeTab(chatRoom);
            }
        };
        closeThisAction.putValue(Action.NAME, "Close this chat");
        popup.add(closeThisAction);


        if (getChatRooms().size() > 1)
		{
            Action closeOthersAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
                    ChatRoom chatRoom = (ChatRoom)getComponentInTab(tab);
                    if (chatRoom != null) 
                        for (ChatRoom cRoom : getChatRooms()) 
                            if (chatRoom != cRoom) 
                                closeTab(cRoom);
                }
            };

            closeOthersAction.putValue(Action.NAME, "Close other chats");
            popup.add(closeOthersAction);
        }
		
        popup.show(tab, e.getX(), e.getY());
    }

    public int getTotalNumberOfUnreadMessages()
	{
        int messageCount = 0;
        for (ChatRoom chatRoom : chatRoomList) 
            messageCount += chatRoom.getUnreadMessageCount();
        return messageCount;
    }
	
}

