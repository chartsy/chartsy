package org.chartsy.chatsy.chat.ui;

public abstract class ChatRoomListenerAdapter implements ChatRoomListener
{

    public void chatRoomOpened(ChatRoom room)
	{
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
	
}
