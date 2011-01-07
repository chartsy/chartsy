package org.chartsy.chatsy.chat.ui;

import org.chartsy.chatsy.chat.component.tabbedPane.ChatsyTab;
import org.chartsy.chatsy.chat.ui.rooms.ChatRoomImpl;
import org.chartsy.chatsy.chat.PresenceManager;
import org.jivesoftware.smack.packet.Presence;

import java.awt.Component;
import java.awt.Color;

public abstract class ChatsyTabHandler
{

    public abstract boolean isTabHandled(ChatsyTab tab, Component component, boolean isSelectedTab, boolean chatFrameFocused);

    protected void decorateStaleTab(ChatsyTab tab, ChatRoom chatRoom)
	{
        tab.setTitleColor(Color.gray);
        tab.setTabFont(tab.getDefaultFont());

        String jid = ((ChatRoomImpl)chatRoom).getParticipantJID();
        Presence presence = PresenceManager.getPresence(jid);

        if (presence.isAvailable())
		{
            Presence.Mode mode = presence.getMode();
            if (mode == Presence.Mode.available || mode == null)
                tab.setIcon(PresenceManager.getIconFromPresence(PresenceManager.AVAILABLE));
            else if (mode == Presence.Mode.away)
                tab.setIcon(PresenceManager.getIconFromPresence(PresenceManager.AWAY));
            else if (mode == Presence.Mode.dnd)
                tab.setIcon(PresenceManager.getIconFromPresence(PresenceManager.BUSY));
            else if (mode == Presence.Mode.xa)
                tab.setIcon(PresenceManager.getIconFromPresence(PresenceManager.INVISIBLE));
        }
        tab.validateTab();
    }
    
}
