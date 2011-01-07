package org.chartsy.chatsy.chat.ui.conferences;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.chartsy.chatsy.chat.ChatNotFoundException;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.RolloverButton;
import org.chartsy.chatsy.chat.ui.ChatRoom;
import org.chartsy.chatsy.chat.ui.rooms.GroupChatRoom;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingTimerTask;
import org.chartsy.chatsy.chat.util.TaskEngine;
import org.chartsy.chatsy.chat.util.log.Log;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class GroupChatInvitationUI extends JPanel implements ActionListener
{

    private RolloverButton acceptButton;
    private String room;
    private String inviter;
    private String password;

    private String invitationDateFormat = ((SimpleDateFormat)SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM)).toPattern();

    public GroupChatInvitationUI(String room, String inviter, String password, String reason)
	{
        setLayout(new GridBagLayout());
        setBackground(new Color(230, 239, 249));
        this.room = room;
        this.inviter = inviter;
        this.password = password;
        final Date now = new Date();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(invitationDateFormat);
        final String invitationTime = dateFormatter.format(now);
        String nickname = ChatsyManager.getUserManager().getUserNicknameFromJID(inviter);
		
        JLabel iconLabel = new JLabel();

        JTextPane titleLabel = new JTextPane();
        titleLabel.setOpaque(false);
        titleLabel.setEditable(false);
        titleLabel.setBackground(new Color(230, 239, 249));

        acceptButton = new RolloverButton("Accept");
        acceptButton.setForeground(new Color(63, 158, 61));

        RolloverButton rejectButton = new RolloverButton("Reject");
        rejectButton.setForeground(new Color(185, 33, 33));

        add(iconLabel, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(2, 2, 2, 2), 0, 0));
        add(titleLabel, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(2, 2, 2, 2), 0, 0));
        add(acceptButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(0, 2, 2, 2), 0, 0));
        add(rejectButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(0, 2, 2, 2), 0, 0));

        final SimpleAttributeSet styles = new SimpleAttributeSet();
        StyleConstants.setForeground(styles, new Color(13, 104, 196));

        Document document = titleLabel.getDocument();
        try
		{
            document.insertString(0, "[" + invitationTime + "] ", styles);
            StyleConstants.setBold(styles, true);            
            document.insertString(document.getLength(), "invite " + nickname , styles);

            if (ModelUtil.hasLength(reason))
			{
                StyleConstants.setBold(styles, false);
                document.insertString(document.getLength(), "\nMessage: " + reason, styles);
            }
        }
        catch (BadLocationException e)
		{
            Log.error(e);
        }

        acceptButton.addActionListener(this);
        rejectButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent event)
	{
        if (event.getSource() == acceptButton)
            acceptInvitation();
        else 
            rejectInvitation();
    }

    private void acceptInvitation()
	{
        setVisible(false);
        String name = StringUtils.parseName(room);
        ConferenceUtils.enterRoomOnSameThread(name, room, password);
        final TimerTask removeUITask = new SwingTimerTask()
		{
            public void doRun()
			{
                removeUI();
            }
        };
        TaskEngine.getInstance().schedule(removeUITask, 2000);
    }

    private void rejectInvitation()
	{
        removeUI();
        try
		{
            ChatRoom chatRoom = ChatsyManager.getChatManager().getGroupChat(room);
            if (chatRoom instanceof GroupChatRoom)
			{
                GroupChatRoom gcr = (GroupChatRoom)chatRoom;
                if (!gcr.getMultiUserChat().isJoined())
                    chatRoom.closeChatRoom();
            }
        }
        catch (ChatNotFoundException e)
		{
        }
        MultiUserChat.decline(ChatsyManager.getConnection(), room, inviter, "No thank you");
    }

    private void removeUI()
	{
        final Container par = getParent();
        if (par != null)
		{
            par.remove(this);
            par.invalidate();
            par.validate();
            par.repaint();
        }
    }
	
}
