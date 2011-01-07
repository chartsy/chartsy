package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.packet.Message;

public interface MessageFilter
{

    void filterOutgoing(ChatRoom room, Message message);
    void filterIncoming(ChatRoom room, Message message);

}
