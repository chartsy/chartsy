package org.chartsy.chatsy.chatimpl.plugin.alerts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TimerTask;
import javax.swing.JLabel;
import org.jivesoftware.smack.packet.Message;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.RolloverButton;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.rooms.ChatRoomImpl;
import org.chartsy.chatsy.chat.util.SwingTimerTask;
import org.chartsy.chatsy.chat.util.TaskEngine;

public class BuzzRoomDecorator implements ActionListener
{

    private ChatRoom chatRoom;
    private RolloverButton buzzButton;
    private static ArrayList<BuzzRoomDecorator> objects = new ArrayList<BuzzRoomDecorator>();
    private String jid;
    
    public BuzzRoomDecorator(ChatRoom chatRoom)
	{
        this.chatRoom = chatRoom;
        jid = ((ChatRoomImpl)chatRoom).getParticipantJID();
        boolean added = false;
        
        for(BuzzRoomDecorator buzz : objects) 
		{
			if(buzz.jid == ((ChatRoomImpl)chatRoom).getParticipantJID())
			{
				addBuzzButton(buzz);
				added = true;
			}
        }
        
        if(!added)
        {
	        buzzButton = new RolloverButton("Buzz");
	        buzzButton.addActionListener(this);
	
	        final JLabel dividerLabel = new JLabel("div");
	        chatRoom.getEditorBar().add(dividerLabel);
	        chatRoom.getEditorBar().add(buzzButton);
	        objects.add(this);
        }
    }

    public void addBuzzButton(BuzzRoomDecorator buzzer)
    {
    	final JLabel dividerLabel = new JLabel("div");
    	chatRoom.getEditorBar().add(dividerLabel);
    	chatRoom.getEditorBar().add(buzzer.buzzButton);
    }

    public void actionPerformed(ActionEvent e)
	{
        final String jid = ((ChatRoomImpl)chatRoom).getParticipantJID();
        Message message = new Message();
        message.setTo(jid);
        message.addExtension(new BuzzPacket());
        ChatsyManager.getConnection().sendPacket(message);

        chatRoom.getTranscriptWindow().insertNotificationMessage("buzz", ChatManager.NOTIFICATION_COLOR);
        buzzButton.setEnabled(false);

        final TimerTask enableTask = new SwingTimerTask()
		{
            public void doRun()
			{
                buzzButton.setEnabled(true);
            }
        };
        TaskEngine.getInstance().schedule(enableTask, 30000);
    }
}
