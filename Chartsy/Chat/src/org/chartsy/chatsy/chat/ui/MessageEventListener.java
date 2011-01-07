package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.packet.Message;

public interface MessageEventListener
{

    void sendingMessage(Message message);
    void receivingMessage(Message message);

}
