package org.chartsy.chatsy.chat.ui;

import org.chartsy.chatsy.chat.component.RolloverButton;

import javax.swing.Icon;

public class ChatRoomButton extends RolloverButton
{

    public ChatRoomButton()
	{
    }

    public ChatRoomButton(Icon icon)
	{
        super(icon);
    }

    public ChatRoomButton(String text, Icon icon)
	{
        super(text, icon);
    }

    public ChatRoomButton(String text)
	{
        super(text);
    }

}
