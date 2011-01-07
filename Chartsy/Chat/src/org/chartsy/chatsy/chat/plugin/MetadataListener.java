package org.chartsy.chatsy.chat.plugin;

import org.chartsy.chatsy.chat.ui.ChatRoom;
import java.util.Map;

public interface MetadataListener
{
    void metadataAssociatedWithRoom(ChatRoom room, Map metadata);
}
