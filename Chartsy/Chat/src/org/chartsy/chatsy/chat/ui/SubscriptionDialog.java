package org.chartsy.chatsy.chat.ui;

import org.chartsy.chatsy.Chatsy;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.util.StringUtils;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.UserManager;
import org.chartsy.chatsy.chat.component.TitlePanel;
import org.chartsy.chatsy.chat.component.borders.ComponentTitledBorder;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.Transport;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.TransportUtils;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class SubscriptionDialog
{

    private final JButton acceptButton = new JButton("Accept");
    private final JButton viewInfoButton = new JButton("View");
    private final JButton denyButton = new JButton("Deny");
    private final JPanel mainPanel;
    private final JCheckBox rosterBox = new JCheckBox();
    private final JLabel nicknameLabel = new JLabel();
    private final JTextField nicknameField = new JTextField();
    private final JLabel groupLabel = new JLabel();
    private final JComboBox groupBox = new JComboBox();
    private JLabel usernameLabel = new JLabel();
    private JLabel usernameLabelValue = new JLabel();
    private JFrame dialog;
    private String jid;


    public SubscriptionDialog()
	{
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        final JPanel rosterPanel = new JPanel();
        rosterPanel.setLayout(new GridBagLayout());

		usernameLabel.setText("Username");
		nicknameLabel.setText("Nickname");
		groupLabel.setText("Group");


        rosterBox.setText("Add to roster");
        groupBox.setEditable(true);

        rosterBox.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                nicknameField.setEnabled(rosterBox.isSelected());
                groupBox.setEnabled(rosterBox.isSelected());
            }
        });

        rosterBox.setSelected(true);
        ComponentTitledBorder componentBorder = new ComponentTitledBorder(rosterBox, rosterPanel, BorderFactory.createEtchedBorder());
        rosterPanel.add(usernameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        rosterPanel.add(usernameLabelValue, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        rosterPanel.add(nicknameLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        rosterPanel.add(nicknameField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        rosterPanel.add(groupLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        rosterPanel.add(groupBox, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        rosterPanel.add(new JLabel(), new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0, 
			GridBagConstraints.SOUTH, GridBagConstraints.BOTH,
			new Insets(5, 5, 5, 5), 0, 0));

        mainPanel.add(rosterPanel, new GridBagConstraints(2, 1, 5, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        rosterPanel.setBorder(componentBorder);
        mainPanel.add(acceptButton, new GridBagConstraints(3, 2, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 5));
        mainPanel.add(viewInfoButton, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 5));
        mainPanel.add(denyButton, new GridBagConstraints(5, 2, 1, 1, 0.0, 1.0, 
			GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 5));

        for (ContactGroup group : ChatsyManager.getWorkspace().getContactList().getContactGroups())
			groupBox.addItem(group.getGroupName());
        groupBox.setEditable(true);
        if (groupBox.getItemCount() == 0)
            groupBox.addItem("Friends");
        if (groupBox.getItemCount() > 0) 
            groupBox.setSelectedIndex(0);
    }

    public void invoke(final String jid)
	{
        this.jid = jid;
        final Roster roster = ChatsyManager.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(jid);
        if (entry != null && entry.getType() == RosterPacket.ItemType.to)
		{
            Presence response = new Presence(Presence.Type.subscribed);
            response.setTo(jid);
            ChatsyManager.getConnection().sendPacket(response);
            return;
        }

        String message = "Approve subscription to " + UserManager.unescapeJID(jid);
        Transport transport = TransportUtils.getTransport(StringUtils.parseServer(jid));
        Icon icon = null;
        if (transport != null)
            icon = transport.getIcon();

        TitlePanel messageLabel = new TitlePanel("", message, icon, true);
        mainPanel.add(messageLabel, new GridBagConstraints(0, 0, 6, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 0, 0), 0, 0));

        String username = StringUtils.parseName(UserManager.unescapeJID(jid));
        usernameLabelValue.setText(UserManager.unescapeJID(jid));
        nicknameField.setText(username);

        acceptButton.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent e)
			{
                if (!rosterBox.isSelected())
				{
                    Presence response = new Presence(Presence.Type.subscribed);
                    response.setTo(jid);
                    ChatsyManager.getConnection().sendPacket(response);
                    dialog.dispose();
                    return;
                }

                boolean addEntry = addEntry();
                if (addEntry)
				{
                    Presence response = new Presence(Presence.Type.subscribed);
                    response.setTo(jid);
                    ChatsyManager.getConnection().sendPacket(response);
                }
                else
				{
                    dialog.dispose();
                }
            }
        });

        denyButton.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent e)
			{
                Presence response = new Presence(Presence.Type.unsubscribe);
                response.setTo(jid);
                ChatsyManager.getConnection().sendPacket(response);
                dialog.dispose();
            }
        });

        viewInfoButton.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent e)
			{
                ChatsyManager.getVCardManager().viewProfile(jid, mainPanel);
            }
        });
        
        dialog = new JFrame("Subscription request")
		{
			public Dimension getPreferredSize()
			{
                final Dimension dim = super.getPreferredSize();
                dim.width = 400;
                return dim;
            }
        };
        dialog.setIconImage(ChatsyManager.getApplicationImage().getImage());
        dialog.getContentPane().add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(ChatsyManager.getMainWindow());

        if (ChatsyManager.getMainWindow().isFocusOwner())
		{
            dialog.setState(Frame.NORMAL);
            dialog.setVisible(true);
        }
        else if (!ChatsyManager.getMainWindow().isVisible() || !ChatsyManager.getMainWindow().isFocusOwner())
		{
            dialog.dispose();
            if (Chatsy.isWindows())
			{
                dialog.setFocusableWindowState(false);
                dialog.setState(Frame.ICONIFIED);
            }
            dialog.setVisible(true);
            dialog.setFocusableWindowState(true);
            ChatsyManager.getNativeManager().flashWindowStopOnFocus(dialog);
        }
    }

    private boolean addEntry()
	{
		String errorMessage = "";
        String nickname = nicknameField.getText();
        String group = (String)groupBox.getSelectedItem();

		if (!ModelUtil.hasLength(nickname))
            errorMessage = "Nickname not specified";
        else if (!ModelUtil.hasLength(group))
            errorMessage = "Group not specified";
        else if (ModelUtil.hasLength(nickname) && ModelUtil.hasLength(group))
		{
            addEntry(jid, nickname, group);
            dialog.setVisible(false);
            return true;
        }

        JOptionPane.showMessageDialog(
			dialog,
			errorMessage,
			"Error",
			JOptionPane.ERROR_MESSAGE);
        return false;
    }

    public RosterEntry addEntry(String jid, String nickname, String group)
	{
        String[] groups = {group};
        Roster roster = ChatsyManager.getConnection().getRoster();
        RosterEntry userEntry = roster.getEntry(jid);

        boolean isSubscribed = true;
        if (userEntry != null)
            isSubscribed = userEntry.getGroups().isEmpty();

        if (isSubscribed)
		{
            try
			{
                roster.createEntry(jid, nickname, new String[]{group});
            }
            catch (XMPPException e)
			{
                Log.error("Unable to add new entry " + jid, e);
            }
            return roster.getEntry(jid);
        }

        try
		{
            RosterGroup rosterGroup = roster.getGroup(group);
            if (rosterGroup == null)
                rosterGroup = roster.createGroup(group);
            if (userEntry == null)
			{
                roster.createEntry(jid, nickname, groups);
                userEntry = roster.getEntry(jid);
            }
            else
			{
                userEntry.setName(nickname);
                rosterGroup.addEntry(userEntry);
            }
            userEntry = roster.getEntry(jid);
        }
        catch (XMPPException ex)
		{
            Log.error(ex);
        }
        return userEntry;
    }
	
}
