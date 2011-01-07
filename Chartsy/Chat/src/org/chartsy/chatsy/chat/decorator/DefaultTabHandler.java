package org.chartsy.chatsy.chat.decorator;

import org.jivesoftware.smack.packet.Presence;
import org.chartsy.chatsy.chat.PresenceManager;
import org.chartsy.chatsy.chat.component.tabbedPane.ChatsyTab;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.ChatsyTabHandler;
import org.chartsy.chatsy.chat.ui.rooms.ChatRoomImpl;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;

public class DefaultTabHandler extends ChatsyTabHandler
{

    public DefaultTabHandler()
	{
    }

    public boolean isTabHandled(ChatsyTab tab, Component component, boolean isSelectedTab, boolean chatFrameFocused)
	{
        if (component instanceof ChatRoom)
		{
            ChatRoom room = (ChatRoom)component;
            if (room instanceof ChatRoomImpl)
			{
                String participantJID = ((ChatRoomImpl)room).getParticipantJID();
                Presence presence = PresenceManager.getPresence(participantJID);
                Icon icon = PresenceManager.getIconFromPresence(presence);
                tab.setIcon(icon);
            }

            if (!chatFrameFocused || !isSelectedTab)
			{
                if (room.getUnreadMessageCount() > 0)
				{
                    tab.setTitleColor(Color.red);
                    tab.setTabBold(true);
                }

                int unreadMessageCount = room.getUnreadMessageCount();
                String appendedMessage = "";
                if (unreadMessageCount > 1)
                    appendedMessage = " (" + unreadMessageCount + ")";
                tab.setTabTitle(room.getTabTitle() + appendedMessage);
            }
            else if (isSelectedTab && chatFrameFocused)
			{
                tab.setTitleColor(Color.black);
                tab.setTabFont(tab.getDefaultFont());
                tab.setTabTitle(room.getTabTitle());
                room.clearUnreadMessageCount();
            }
        }
        else
		{
            if (!chatFrameFocused || !isSelectedTab)
			{
                tab.setTitleColor(Color.red);
                tab.setTabBold(true);
            }
            if (isSelectedTab && chatFrameFocused)
			{
                tab.setTitleColor(Color.black);
                tab.setTabFont(tab.getDefaultFont());
            }
        }
        return true;
    }

}
