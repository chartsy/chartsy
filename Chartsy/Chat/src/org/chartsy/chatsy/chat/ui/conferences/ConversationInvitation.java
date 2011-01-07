package org.chartsy.chatsy.chat.ui.conferences;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.WrappedLabel;
import org.chartsy.chatsy.chat.ui.ContainerComponent;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ConversationInvitation extends JPanel implements ContainerComponent, ActionListener
{

    private JButton joinButton;

    private String roomName;
    private String password;
    private String inviter;

    private String invitationDateFormat = ((SimpleDateFormat)SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM)).toPattern();
    private String tabTitle;
    private String frameTitle;
    private String descriptionText;

    public ConversationInvitation(XMPPConnection conn, final String roomName, 
		final String inviter, String reason, final String password, Message message)
	{
        this.roomName = roomName;
        this.password = password;
        this.inviter = inviter;
        setLayout(new GridBagLayout());
        final JLabel titleLabel = new JLabel();
        final WrappedLabel description = new WrappedLabel();
        description.setBackground(Color.white);

        final JLabel dateLabel = new JLabel();
        final JLabel dateLabelValue = new JLabel();

        String nickname = ChatsyManager.getUserManager().getUserNicknameFromJID(inviter);
        add(titleLabel, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(2, 5, 2, 5), 0, 0));
        add(description, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 9, 2, 5), 0, 0));
        add(dateLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(2, 5, 2, 5), 0, 0));
        add(dateLabelValue, new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(2, 5, 2, 5), 0, 0));
		
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        description.setFont(new Font("Dialog", Font.PLAIN, 12));

        titleLabel.setText("Conference invitation");
        description.setText(nickname + " has invited you to chat in " + roomName + ". " + nickname + " writes \"" + reason + "\"");

        tabTitle = "Chat Invite";
        descriptionText = reason;
        frameTitle = "Conference invitation";

        dateLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        dateLabel.setText("Date:");
        final SimpleDateFormat formatter = new SimpleDateFormat(invitationDateFormat);
        final String date = formatter.format(new Date());
        dateLabelValue.setText(date);
        dateLabelValue.setFont(new Font("Dialog", Font.PLAIN, 12));

        joinButton = new JButton("Accept");
        JButton declineButton = new JButton("Decline");

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(joinButton);
        buttonPanel.add(declineButton);
        add(buttonPanel, new GridBagConstraints(2, 3, 1, 1, 1.0, 0.0, 
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(10, 5, 2, 5), 0, 0));
        add(new JLabel(), new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0, 
			GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
			new Insets(2, 5, 2, 5), 0, 0));

        joinButton.addActionListener(this);
        declineButton.addActionListener(this);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
        setBackground(Color.white);

        ChatManager chatManager = ChatsyManager.getChatManager();
        chatManager.getChatContainer().addContainerComponent(this);
    }

    public void actionPerformed(ActionEvent actionEvent)
	{
        final Object obj = actionEvent.getSource();
        if (obj == joinButton)
		{
            String name = StringUtils.parseName(roomName);
            ConferenceUtils.enterRoomOnSameThread(name, roomName, password);
        }
        else
		{
            MultiUserChat.decline(ChatsyManager.getConnection(), roomName, inviter, "No thank you");
        }

        ChatManager chatManager = ChatsyManager.getChatManager();
        chatManager.getChatContainer().closeTab(this);
    }

    public String getTabTitle()
	{
        return tabTitle;
    }

    public String getFrameTitle()
	{
        return frameTitle;
    }

    public ImageIcon getTabIcon()
	{
        return null;
    }

    public JComponent getGUI()
	{
        return this;
    }

    public String getToolTipDescription()
	{
        return descriptionText;
    }

    public boolean closing()
	{
        return true;
    }
	
}
