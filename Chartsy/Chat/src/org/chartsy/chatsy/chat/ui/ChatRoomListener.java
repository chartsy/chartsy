package org.chartsy.chatsy.chat.ui;

public interface ChatRoomListener
{

    void chatRoomOpened(ChatRoom room);
    void chatRoomLeft(ChatRoom room);
    void chatRoomClosed(ChatRoom room);
    void chatRoomActivated(ChatRoom room);
    void userHasJoined(ChatRoom room, String userid);
    void userHasLeft(ChatRoom room, String userid);

}