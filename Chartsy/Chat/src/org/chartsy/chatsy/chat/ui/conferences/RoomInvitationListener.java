package org.chartsy.chatsy.chat.ui.conferences;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;

public interface RoomInvitationListener
{
    boolean handleInvitation(final Connection connection, final String room, final String inviter, final String reason, final String password, final Message message);
}
