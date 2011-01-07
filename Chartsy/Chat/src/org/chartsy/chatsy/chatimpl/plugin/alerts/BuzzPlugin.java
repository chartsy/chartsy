package org.chartsy.chatsy.chatimpl.plugin.alerts;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.plugin.Plugin;
import org.chartsy.chatsy.chat.ui.ChatFrame;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.ChatRoomListener;
import org.chartsy.chatsy.chat.ui.ContactItem;
import org.chartsy.chatsy.chat.ui.rooms.ChatRoomImpl;
import org.chartsy.chatsy.chat.util.SwingTimerTask;
import org.chartsy.chatsy.chat.util.TaskEngine;
import java.util.TimerTask;
import javax.swing.SwingUtilities;

public class BuzzPlugin implements Plugin
{

    public void initialize()
	{
        ProviderManager.getInstance().addExtensionProvider("buzz", "http://www.jivesoftware.com/spark", BuzzPacket.class);
        ChatsyManager.getConnection().addPacketListener(new PacketListener()
		{
            public void processPacket(Packet packet)
			{
                if (packet instanceof Message)
				{
                    final Message message = (Message)packet;
                    if (message.getExtension("buzz", "http://www.jivesoftware.com/spark") != null)
					{
                        SwingUtilities.invokeLater(new Runnable()
						{
                            public void run()
							{
                                shakeWindow(message);
                            }
                        });
                    }
                }
            }
        }, new PacketTypeFilter(Message.class));


        ChatsyManager.getChatManager().addChatRoomListener(new ChatRoomListener()
		{
            public void chatRoomOpened(final ChatRoom room)
			{
                TimerTask task = new SwingTimerTask()
				{
                    public void doRun()
					{
                        addBuzzFeatureToChatRoom(room);
                    }
                };
                TaskEngine.getInstance().schedule(task, 100);
            }

            public void chatRoomLeft(ChatRoom room)
			{
            }

            public void chatRoomClosed(ChatRoom room)
			{
            }

            public void chatRoomActivated(ChatRoom room)
			{
            }

            public void userHasJoined(ChatRoom room, String userid)
			{
            }

            public void userHasLeft(ChatRoom room, String userid)
			{
            }
        });
    }

    private void addBuzzFeatureToChatRoom(final ChatRoom room)
	{
        if (room instanceof ChatRoomImpl)
            new BuzzRoomDecorator(room);
    }

    private void shakeWindow(Message message)
	{
        String bareJID = StringUtils.parseBareAddress(message.getFrom());
        ContactItem contact = ChatsyManager.getWorkspace().getContactList().getContactItemByJID(bareJID);
        String nickname = StringUtils.parseName(bareJID);
        if (contact != null)
            nickname = contact.getDisplayName();

        ChatRoom room = ChatsyManager.getChatManager().getChatContainer().getChatRoom(bareJID);
		if (room == null)
			room = ChatsyManager.getChatManager().createChatRoom(bareJID, nickname, nickname);

        ChatFrame chatFrame = ChatsyManager.getChatManager().getChatContainer().getChatFrame();
        if (chatFrame != null )
		{
            chatFrame.buzz();
            ChatsyManager.getChatManager().getChatContainer().activateChatRoom(room);
        }

        room.getTranscriptWindow().insertNotificationMessage("BUZZ", ChatManager.NOTIFICATION_COLOR);
        room.scrollToBottom();
    }

    public void shutdown()
	{
    }

    public boolean canShutDown()
	{
        return true;
    }

    public void uninstall()
	{
    }
	
}
