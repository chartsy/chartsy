package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.packet.Message;

public interface TranscriptWindowInterceptor
{

    boolean isMessageIntercepted(TranscriptWindow window, String userid, Message message);

}
