package org.chartsy.chatsy.chat.ui;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.UserManager;
import org.chartsy.chatsy.chat.component.TitlePanel;
import org.chartsy.chatsy.chat.component.borders.ComponentTitledBorder;
import org.chartsy.chatsy.chat.component.renderer.JPanelRenderer;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chatimpl.plugin.gateways.Gateway;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.Transport;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.TransportUtils;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

public class RosterDialog implements PropertyChangeListener, ActionListener {
    private JPanel panel;
    private JTextField jidField;
    private JTextField nicknameField;
    //private final Vector<String> groupModel = new Vector<String>();
	private final DefaultComboBoxModel groupModel = new DefaultComboBoxModel();
    private final JPanel networkPanel = new JPanel(new GridBagLayout());

    private JComboBox groupBox;
    private JComboBox accounts;
    private JOptionPane pane;
    private JDialog dialog;
    private ContactList contactList;
    private JCheckBox publicBox;

    public RosterDialog()
	{
        contactList = ChatsyManager.getWorkspace().getContactList();
        panel = new JPanel();
        JLabel contactIDLabel = new JLabel("Username");
        jidField = new JTextField();
        JLabel nicknameLabel = new JLabel("Nickname");
        nicknameField = new JTextField();
        JLabel groupLabel = new JLabel("Group");
        groupBox = new JComboBox(groupModel);

        JButton newGroupButton = new JButton("New Group");

        JLabel accountsLabel = new JLabel();
        accounts = new JComboBox();
        publicBox = new JCheckBox("User on public access");

		publicBox.setText("local network");

        pane = null;
        dialog = null;
        panel.setLayout(new GridBagLayout());
        panel.add(contactIDLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(jidField, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(nicknameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(nicknameField, new GridBagConstraints(1, 1, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));

        ComponentTitledBorder componentBorder = new ComponentTitledBorder(publicBox, networkPanel, BorderFactory.createEtchedBorder());
        networkPanel.add(accountsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        networkPanel.add(accounts, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        networkPanel.setBorder(componentBorder);
        networkPanel.setVisible(false);
        accounts.setEnabled(false);

        panel.add(groupLabel, new GridBagConstraints(0, 4, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(groupBox, new GridBagConstraints(1, 4, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        panel.add(newGroupButton, new GridBagConstraints(2, 4, 1, 1, 0.0D, 0.0D, 17, 2, new Insets(5, 5, 5, 5), 0, 0));
        newGroupButton.addActionListener(this);
        panel.add(networkPanel, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        accounts.setRenderer(new JPanelRenderer());

        for (ContactGroup group : contactList.getContactGroups())
			groupModel.addElement(group.getGroupName());
		
        groupBox.setEditable(true);
        if (groupModel.getSize() == 0)
            groupBox.addItem("Friends");

        if (groupModel.getSize() > 0)
            groupBox.setSelectedIndex(0);

        jidField.addFocusListener(new FocusListener()
		{
            public void focusGained(FocusEvent e)
			{
            }
            public void focusLost(FocusEvent e)
			{
                String jid = getJID();
                String vcardNickname = null;

                if (!publicBox.isSelected())
				{
                    String fullJID = getJID();
                    if (fullJID.indexOf("@") == -1)
                        fullJID = fullJID + "@" + ChatsyManager.getConnection().getServiceName();

                    VCard vCard = ChatsyManager.getVCardManager().getVCard(fullJID);
                    if (vCard != null && vCard.getError() == null)
					{
                        String firstName = vCard.getFirstName();
                        String lastName = vCard.getLastName();
                        String nickname = vCard.getNickName();
                        if (ModelUtil.hasLength(nickname))
                            vcardNickname = nickname;
                        else if (ModelUtil.hasLength(firstName) && ModelUtil.hasLength(lastName))
                            vcardNickname = firstName + " " + lastName;
                        else if (ModelUtil.hasLength(firstName))
                            vcardNickname = firstName;
                    }
                }

                String nickname = nicknameField.getText();
                if (!ModelUtil.hasLength(nickname) && ModelUtil.hasLength(jid))
				{
                    nickname = StringUtils.parseName(jid);
                    if (!ModelUtil.hasLength(nickname))
                        nickname = jid;
                    nicknameField.setText(vcardNickname != null ? vcardNickname : nickname);
                }
            }
        });

        final List<AccountItem> accountCol = getAccounts();
        for (AccountItem item : accountCol)
            accounts.addItem(item);

        if (accountCol.size() > 0)
		{
            accountsLabel.setVisible(true);
            accounts.setVisible(true);
            publicBox.setVisible(true);
            networkPanel.setVisible(true);
        }

        publicBox.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                accounts.setEnabled(publicBox.isSelected());
            }
        });
    }

    public void setDefaultGroup(ContactGroup contactGroup)
	{
        String groupName = contactGroup.getGroupName();
        if (groupModel.getIndexOf(groupName) != -1)
            groupBox.setSelectedItem(groupName);
        else if (groupModel.getSize() > 0)
		{
            groupBox.addItem(groupName);
            groupBox.setSelectedItem(groupName);
        }
    }

    public void setDefaultJID(String jid)
	{
        jidField.setText(jid);
    }

    public void setDefaultNickname(String nickname)
	{
        nicknameField.setText(nickname);
    }


    public void actionPerformed(ActionEvent e)
	{
        String group = JOptionPane.showInputDialog(dialog, "Group:", "New Group", 3);
        if (group != null && group.length() > 0 && groupModel.getIndexOf(group) == -1)
		{
            ChatsyManager.getConnection().getRoster().createGroup(group);
            groupModel.addElement(group);
            int size = groupModel.getSize();
            groupBox.setSelectedIndex(size - 1);
        }
    }

    public void showRosterDialog(JFrame parent)
	{
        TitlePanel titlePanel = new TitlePanel("Add Contact", "Add a new contact to list", null, true);
        JPanel mainPanel = new JPanel()
		{
			public Dimension getPreferredSize()
			{
                final Dimension size = super.getPreferredSize();
                size.width = 350;
                return size;
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        Object[] options = {"Add", "Cancel"};
        pane = new JOptionPane(panel, -1, 2, null, options, options[0]);
        mainPanel.add(pane, BorderLayout.CENTER);
        dialog = new JDialog(parent, "Add contact", false);
        dialog.setContentPane(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        pane.addPropertyChangeListener(this);
        dialog.setVisible(true);
        dialog.toFront();
        dialog.requestFocus();
        jidField.requestFocus();
    }

    public void showRosterDialog()
	{
        showRosterDialog(new JFrame());
    }

    public void propertyChange(PropertyChangeEvent e)
	{
        if (pane != null && pane.getValue() instanceof Integer)
		{
            pane.removePropertyChangeListener(this);
            dialog.dispose();
            return;
        }

        try
		{
            String value = (String)pane.getValue();
            String errorMessage = "Error";
            if ("Cancel".equals(value))
			{
                dialog.setVisible(false);
            }
            else if ("Add".equals(value))
			{
                String jid = getJID();
                String contact = UserManager.escapeJID(jid);
                String nickname = nicknameField.getText();
                String group = (String)groupBox.getSelectedItem();

                Transport transport = null;
                if (publicBox.isSelected())
				{
                    AccountItem item = (AccountItem)accounts.getSelectedItem();
                    transport = item.getTransport();
                }

                if (transport == null)
                    if (contact.indexOf("@") == -1) 
                        contact = contact + "@" + ChatsyManager.getConnection().getServiceName();
                else 
                    if (contact.indexOf("@") == -1) 
                        contact = contact + "@" + transport.getServiceName();

                if (!ModelUtil.hasLength(nickname) && ModelUtil.hasLength(contact))
				{
                    VCard vcard = new VCard();
                    try
					{
                        vcard.load(ChatsyManager.getConnection(), contact);
                        nickname = vcard.getNickName();
                    }
                    catch (XMPPException ex)
					{
                        Log.error(ex);
                    }
                    if (!ModelUtil.hasLength(nickname))
                        nickname = StringUtils.parseName(contact);
                    nicknameField.setText(nickname);
                }

				if (!ModelUtil.hasLength(contact))
                    errorMessage = "Please specify the contact JID";
                else if (StringUtils.parseBareAddress(contact).indexOf("@") == -1)
                    errorMessage = "The JID specified is invalid";
                else if (!ModelUtil.hasLength(group))
                    errorMessage = "Specify contact group to add the new user to";
                else if (ModelUtil.hasLength(contact) && ModelUtil.hasLength(group))
				{
                    addEntry();
                    dialog.setVisible(false);
                    return;
                }

                JOptionPane.showMessageDialog(dialog, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
        }
        catch (NullPointerException ee)
		{
            Log.error(ee);
        }
    }

    private void addEntry()
	{
        Transport transport = null;
        AccountItem item;
        if (publicBox.isSelected())
		{
            item = (AccountItem)accounts.getSelectedItem();
            transport = item.getTransport();
        }
        if (transport == null)
		{
            String jid = getJID();
            if (jid.indexOf("@") == -1)
                jid = jid + "@" + ChatsyManager.getConnection().getServiceName();
            String nickname = nicknameField.getText();
            String group = (String)groupBox.getSelectedItem();

            jid = UserManager.escapeJID(jid);
            addRosterEntry(jid, nickname, group);
        }
        else
		{
            String jid = getJID();
            try
			{
                jid = Gateway.getJID(transport.getServiceName(), jid);
            }
            catch (XMPPException e)
			{
                Log.error(e);
            }
            String nickname = nicknameField.getText();
            String group = (String)groupBox.getSelectedItem();
            addRosterEntry(jid, nickname, group);
        }
    }

    private String getJID()
	{
        return jidField.getText().trim();
    }

    private void addRosterEntry(final String jid, final String nickname, final String group)
	{
        final SwingWorker rosterEntryThread = new SwingWorker()
		{
            public Object construct()
			{
                return addEntry(jid, nickname, group);
            }
            public void finished()
			{
                if (get() == null)
                    JOptionPane.showMessageDialog(dialog, "Unable to add contact", "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        rosterEntryThread.start();
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

    public List<AccountItem> getAccounts()
	{
        List<AccountItem> list = new ArrayList<AccountItem>();
        for (Transport transport : TransportUtils.getTransports())
		{
            if (TransportUtils.isRegistered(ChatsyManager.getConnection(), transport))
			{
                AccountItem item = new AccountItem(transport.getIcon(), transport.getName(), transport);
                list.add(item);
            }
        }
        return list;
    }

    class AccountItem extends JPanel
	{
		
		private Transport transport;

        public AccountItem(Icon icon, String name, Transport transport)
		{
            setLayout(new GridBagLayout());
            this.transport = transport;

            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(icon);

            JLabel label = new JLabel();
            label.setText(name);
            label.setFont(new Font("Dialog", Font.PLAIN, 11));
            label.setHorizontalTextPosition(JLabel.CENTER);

            add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
            add(label, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 10, 0, 0), 0, 0));
            setBackground(Color.white);
        }

        public Transport getTransport()
		{
            return transport;
        }
		
    }
	
}