package org.chartsy.chatsy.chat.ui.conferences;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.DataForm;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.rooms.GroupChatRoom;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chat.util.log.NotifyUtil;
import org.openide.util.NbPreferences;

public class ConferenceUtils
{

    private ConferenceUtils()
	{
    }

    public static Collection<HostedRoom> getRoomList(String serviceName) throws Exception
	{
        return MultiUserChat.getHostedRooms(ChatsyManager.getConnection(), serviceName);
    }

    public static int getNumberOfOccupants(String roomJID) throws XMPPException
	{
        final RoomInfo roomInfo = MultiUserChat.getRoomInfo(ChatsyManager.getConnection(), roomJID);
        return roomInfo.getOccupantsCount();
    }

    public static String getCreationDate(String roomJID) throws Exception
	{
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(ChatsyManager.getConnection());
        final DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
        DiscoverInfo infoResult = discoManager.discoverInfo(roomJID);
        DataForm dataForm = (DataForm)infoResult.getExtension("x", "jabber:x:data");
        if (dataForm == null)
            return "Not available";
		
        Iterator<FormField> fieldIter = dataForm.getFields();
        String creationDate = "";
        while (fieldIter.hasNext())
		{
            FormField field = fieldIter.next();
            String label = field.getLabel();

            if (label != null && "Creation date".equalsIgnoreCase(label))
			{
                Iterator<String> valueIterator = field.getValues();
                while (valueIterator.hasNext())
				{
                    Object oo = valueIterator.next();
                    creationDate = "" + oo;
                    Date date = dateFormatter.parse(creationDate);
                    creationDate = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.MEDIUM).format(date);
                }
            }
        }
        return creationDate;
    }

    public static void joinConferenceOnSeperateThread(final String roomName, String roomJID, String password)
	{
        ChatManager chatManager = ChatsyManager.getChatManager();

        final MultiUserChat groupChat = new MultiUserChat(ChatsyManager.getConnection(), roomJID);
        final String nickname = NbPreferences.root().node("/org/chartsy/chat").get("nickname", "");

		GroupChatRoom chatRoom = (GroupChatRoom) chatManager.getChatContainer().getChatRoom(roomJID);
		if (chatRoom != null)
		{
			MultiUserChat muc = chatRoom.getMultiUserChat();
            if (!muc.isJoined())
                joinRoom(muc, nickname, password);
            chatManager.getChatContainer().activateChatRoom(chatRoom);
            return;
		}

        final GroupChatRoom room = new GroupChatRoom(groupChat);
        room.setTabTitle(roomName);

        final List<String> errors = new ArrayList<String>();
        final String userPassword = password;

        final SwingWorker startChat = new SwingWorker()
		{
            public Object construct()
			{
                if (!groupChat.isJoined())
				{
                    int groupChatCounter = 0;
                    while (true)
					{
                        groupChatCounter++;
                        String joinName = nickname;
                        if (groupChatCounter > 1)
                            joinName = joinName + groupChatCounter;
                        if (groupChatCounter < 10)
						{
                            try
							{
                                if (ModelUtil.hasLength(userPassword))
                                    groupChat.join(joinName, userPassword);
                                else 
                                    groupChat.join(joinName);
                                break;
                            }
                            catch (XMPPException ex)
							{
                                int code = 0;
                                if (ex.getXMPPError() != null)
                                    code = ex.getXMPPError().getCode();

                                if (code == 0)
                                    errors.add("No response from server.");
                                else if (code == 401)
                                    errors.add("The password did not match the rooms password.");
                                else if (code == 403)
                                    errors.add("You have been banned from this room.");
                                else if (code == 404)
                                    errors.add("The room you are trying to enter does not exist.");
                                else if (code == 407)
                                    errors.add("You are not a member of this room.\nThis room requires you to be a member to join.");
                                else if (code != 409)
                                    break;
                            }
                        }
                        else
						{
                            break;
                        }
                    }
                }
                return "ok";
            }

            public void finished()
			{
                if (errors.size() > 0)
				{
                    String error = errors.get(0);
					NotifyUtil.error("Unable to join the room at this time.", error, false);
                }
                else if (groupChat.isJoined())
				{
                    ChatManager chatManager = ChatsyManager.getChatManager();
                    chatManager.getChatContainer().addChatRoom(room);
                    chatManager.getChatContainer().activateChatRoom(room);
                }
                else
				{
					NotifyUtil.error("Error", "Unable to join the room.", false);
                }
            }
        };
        startChat.start();
    }

    public static void joinConferenceRoom(final String roomName, String roomJID)
	{
        JoinConferenceRoomDialog joinDialog = new JoinConferenceRoomDialog();
        joinDialog.joinRoom(roomJID, roomName);
    }

    public static List<String> joinRoom(MultiUserChat groupChat, String nickname, String password)
	{
        final List<String> errors = new ArrayList<String>();
        if (!groupChat.isJoined())
		{
            int groupChatCounter = 0;
            while (true)
			{
                groupChatCounter++;
                String joinName = nickname;
                if (groupChatCounter > 1)
                    joinName = joinName + groupChatCounter;
                if (groupChatCounter < 10)
				{
                    try
					{
                        if (ModelUtil.hasLength(password))
                            groupChat.join(joinName, password);
                        else
                            groupChat.join(joinName);
                        break;
                    }
                    catch (XMPPException ex)
					{
                        int code = 0;
                        if (ex.getXMPPError() != null)
                            code = ex.getXMPPError().getCode();

                        if (code == 0)
                            errors.add("No response from server.");
                        else if (code == 401)
                            errors.add("A Password is required to enter this room.");
                        else if (code == 403)
                            errors.add("You have been banned from this room.");
                        else if (code == 404)
                            errors.add("The room you are trying to enter does not exist.");
                        else if (code == 407)
                            errors.add("You are not a member of this room.\nThis room requires you to be a member to join.");
                        else if (code != 409)
                            break;
                    }
                }
                else
				{
                    break;
                }
            }
        }
        return errors;
    }

    public static void inviteUsersToRoom(String serviceName, String roomName, Collection<String> jids)
	{
        InvitationDialog inviteDialog = new InvitationDialog();
        inviteDialog.inviteUsersToRoom(serviceName, roomName, jids);
    }

    public static boolean isPasswordRequired(String roomJID)
	{
        ServiceDiscoveryManager discover = new ServiceDiscoveryManager(ChatsyManager.getConnection());
        try
		{
            DiscoverInfo info = discover.discoverInfo(roomJID);
            return info.containsFeature("muc_passwordprotected");
        }
        catch (XMPPException e)
		{
            Log.error(e);
        }
        return false;
    }

    public static void createPrivateConference(String serviceName, String message, String roomName, Collection<String> jids) throws XMPPException
	{
        final String roomJID = StringUtils.escapeNode(roomName) + "@" + serviceName;
        final MultiUserChat multiUserChat = new MultiUserChat(ChatsyManager.getConnection(), roomJID);

        final GroupChatRoom room = new GroupChatRoom(multiUserChat);
        try
		{
            multiUserChat.create(NbPreferences.root().node("/org/chartsy/chat").get("nickname", ""));
        }
        catch (XMPPException e)
		{
            throw new XMPPException(e);
        }

        try
		{
            Form submitForm = multiUserChat.getConfigurationForm().createAnswerForm();
            submitForm.setAnswer("muc#roomconfig_publicroom", false);
            submitForm.setAnswer("muc#roomconfig_roomname", roomName);

            final List<String> owners = new ArrayList<String>();
            owners.add(ChatsyManager.getSessionManager().getBareAddress());
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);

            multiUserChat.sendConfigurationForm(submitForm);
        }
        catch (XMPPException e1)
		{
            Log.error("Unable to send conference room chat configuration form.", e1);
        }

        ChatManager chatManager = ChatsyManager.getChatManager();
		ChatRoom chatRoom = chatManager.getChatContainer().getChatRoom(room.getRoomname());
		if (chatRoom == null)
		{
			chatManager.getChatContainer().addChatRoom(room);
            chatManager.getChatContainer().activateChatRoom(room);
		}

        for (String jid : jids)
		{
            multiUserChat.invite(jid, message);
            room.getTranscriptWindow().insertNotificationMessage("Waiting for "+jid+" to join the conference.", ChatManager.NOTIFICATION_COLOR);
        }
    }

    public static String getReason(XMPPException ex)
	{
        String reason = "";
        int code = 0;
        if (ex.getXMPPError() != null)
            code = ex.getXMPPError().getCode();

        if (code == 0)
            reason = "No response from server.";
        else if (code == 401)
            reason = "The password did not match the room's password.";
        else if (code == 403)
            reason = "You have been banned from this room.";
        else if (code == 404)
            reason = "The room you are trying to enter does not exist.";
        else if (code == 405)
            reason = "You do not have permission to create a room.";
        else if (code == 407)
            reason = "You are not a member of this room.\nThis room requires you to be a member to join.";

        return reason;
    }

    public static GroupChatRoom enterRoomOnSameThread(final String roomName, String roomJID, String password)
	{
        ChatManager chatManager = ChatsyManager.getChatManager();
        final String nickname = NbPreferences.root().node("/org/chartsy/chat").get("nickname", "");

		GroupChatRoom chatRoom = (GroupChatRoom)chatManager.getChatContainer().getChatRoom(roomJID);
		if (chatRoom != null)
		{
            MultiUserChat muc = chatRoom.getMultiUserChat();
            if (!muc.isJoined())
                joinRoom(muc, nickname, password);
            chatManager.getChatContainer().activateChatRoom(chatRoom);
            return chatRoom;
		}

        final MultiUserChat groupChat = new MultiUserChat(ChatsyManager.getConnection(), roomJID);

        final GroupChatRoom room = new GroupChatRoom(groupChat);
        room.setTabTitle(roomName);

        if (isPasswordRequired(roomJID) && password == null)
		{
            password = JOptionPane.showInputDialog(
				null,
				"Enter Room Password",
				"Need Password",
				JOptionPane.QUESTION_MESSAGE);
            if (!ModelUtil.hasLength(password))
                return null;
        }

        final List<String> errors = new ArrayList<String>();
        final String userPassword = password;

        if (!groupChat.isJoined())
		{
            int groupChatCounter = 0;
            while (true)
			{
                groupChatCounter++;
                String joinName = nickname;
                if (groupChatCounter > 1)
                    joinName = joinName + groupChatCounter;
                if (groupChatCounter < 10)
				{
                    try
					{
                        if (ModelUtil.hasLength(userPassword))
                            groupChat.join(joinName, userPassword);
                        else
                            groupChat.join(joinName);
                        break;
                    }
                    catch (XMPPException ex)
					{
                        int code = 0;
                        if (ex.getXMPPError() != null)
                            code = ex.getXMPPError().getCode();

                        if (code == 0)
                            errors.add("No response from server.");
                        else if (code == 401)
                            errors.add("The password did not match the room's password.");
                        else if (code == 403)
                            errors.add("You have been banned from this room.");
                        else if (code == 404)
                            errors.add("The room you are trying to enter does not exist.");
                        else if (code == 407)
                            errors.add("You are not a member of this room.\nThis room requires you to be a member to join.");
                        else if (code != 409)
                            break;
                    }
                }
                else
				{
                    break;
                }
            }
        }

        if (errors.size() > 0)
		{
            String error = errors.get(0);
            JOptionPane.showMessageDialog(
				ChatsyManager.getMainWindow(),
				error,
				"Unable to join the room at this time.",
				JOptionPane.ERROR_MESSAGE);
            return null;
        }
        else if (groupChat.isJoined())
		{
            chatManager.getChatContainer().addChatRoom(room);
            chatManager.getChatContainer().activateChatRoom(room);
        }
        else
		{
            JOptionPane.showMessageDialog(
				ChatsyManager.getMainWindow(),
				"Unable to join the room.",
				"Error",
				JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return room;
    }

    public static void enterRoom(final MultiUserChat groupChat, String tabTitle, final String nickname, final String password)
	{
        final GroupChatRoom room = new GroupChatRoom(groupChat);
        room.setTabTitle(tabTitle);
        if (room == null)
            return;

        final List<String> errors = new ArrayList<String>();
        if (!groupChat.isJoined())
		{
            int groupChatCounter = 0;
            while (true)
			{
                groupChatCounter++;
                String joinName = nickname;
                if (groupChatCounter > 1)
                    joinName = joinName + groupChatCounter;
                if (groupChatCounter < 10)
				{
                    try
					{
                        if (ModelUtil.hasLength(password)) 
                            groupChat.join(joinName, password);
                        else 
                            groupChat.join(joinName);
                        break;
                    }
                    catch (XMPPException ex)
					{
                        int code = 0;
                        if (ex.getXMPPError() != null)
                            code = ex.getXMPPError().getCode();

                        if (code == 0) 
                            errors.add("No response from server.");
                        else if (code == 401) 
                            errors.add("A Password is required to enter this room.");
                        else if (code == 403)
                            errors.add("You have been banned from this room.");
                        else if (code == 404)
                            errors.add("The room you are trying to enter does not exist.");
                        else if (code == 407)
                            errors.add("You are not a member of this room.\nThis room requires you to be a member to join.");
                        else if (code != 409)
                            break;
                    }
                }
                else
				{
                    break;
                }
            }
        }

        if (errors.size() > 0)
		{
            String error = errors.get(0);
            JOptionPane.showMessageDialog(
				ChatsyManager.getMainWindow(),
				error,
				"Could Not Join Room",
				JOptionPane.ERROR_MESSAGE);
        }
        else if (groupChat.isJoined())
		{
            ChatManager chatManager = ChatsyManager.getChatManager();
            chatManager.getChatContainer().addChatRoom(room);
            chatManager.getChatContainer().activateChatRoom(room);
        }
        else
		{
            JOptionPane.showMessageDialog(
				ChatsyManager.getMainWindow(),
				"Unable to join room.",
				"Error",
				JOptionPane.ERROR_MESSAGE);
        }
    }
    
    final static List<String> unclosableChatRooms = new ArrayList<String>();
	public synchronized static void addUnclosableChatRoom(String jid)
	{
		unclosableChatRooms.add(jid);
	}
	
	public static boolean isChatRoomClosable(Component c)
	{
		if (c instanceof GroupChatRoom )
		{
			GroupChatRoom groupChatRoom = (GroupChatRoom) c;
    		String roomName = groupChatRoom.getChatRoom().getRoomname();                        		
    		if (unclosableChatRooms.contains(roomName))
    			return false;
		}
		return true;
	}

}

