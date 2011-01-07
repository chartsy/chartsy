package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.packet.Message;

public interface MessageListener
{

    void messageReceived(ChatRoom room, Message message);
    void messageSent(ChatRoom room, Message message);

}