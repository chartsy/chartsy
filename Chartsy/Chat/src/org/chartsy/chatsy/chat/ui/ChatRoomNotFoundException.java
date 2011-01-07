package org.chartsy.chatsy.chat.ui;

public class ChatRoomNotFoundException extends Exception
{

	public ChatRoomNotFoundException()
	{
        super();
    }

    public ChatRoomNotFoundException(String msg)
	{
        super(msg);
    }
	
}