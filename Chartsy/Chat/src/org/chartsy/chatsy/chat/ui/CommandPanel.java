package org.chartsy.chatsy.chat.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.chartsy.chatsy.chat.ChatManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.Workspace;
import org.chartsy.chatsy.chat.ui.rooms.GroupChatRoom;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chatimpl.search.users.UserSearchService;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;

public class CommandPanel extends JPanel implements ActionListener
{

	private static final String COMMAND_BUTTON_BG = "org/chartsy/chatsy/resources/command-btn.png";
	private static final String ADD_ICON = "org/chartsy/chatsy/resources/add-icon.png";
	private static final String CHAT_ICON = "org/chartsy/chatsy/resources/chat-icon.png";
	private static final String SEARCH_ICON = "org/chartsy/chatsy/resources/search-icon.png";
	private static Image buttonBackground;
	private static String[] addButtonTooltips =
	{
		"Create conference room",
		"Add a contact"
	};
	private static String[] chatButtonTooltips =
	{
		"Join conference",
		"Start chat"
	};
	private int selectedTab = 0;

	private CommandButton addButton;
	private CommandButton chatButton;
	private CommandButton searchButton;

    public CommandPanel()
	{
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		setOpaque(true);
		setBackground(Color.decode("0xbdbebd"));
		buttonBackground = ImageUtilities.loadImage(COMMAND_BUTTON_BG, true);

		addButton = new CommandButton(ADD_ICON, addButtonTooltips[0]);
		chatButton = new CommandButton(CHAT_ICON, chatButtonTooltips[0]);
		searchButton = new CommandButton(SEARCH_ICON, "Search for buddies");

		add(addButton);
		add(chatButton);
		add(searchButton);

		addButton.addActionListener((ActionListener)this);
		chatButton.addActionListener((ActionListener)this);
		searchButton.addActionListener((ActionListener)this);
    }

	public void setSelectedTab(int index)
	{
		if (selectedTab != index)
		{
			selectedTab = index;
			updateAddButtonTooltip();
			updateChatButtonTooltip();
		}
	}

	public void updateAddButtonTooltip()
	{
		addButton.setToolTipText(addButtonTooltips[selectedTab]);
	}

	public void updateChatButtonTooltip()
	{
		chatButton.setToolTipText(chatButtonTooltips[selectedTab]);
	}

	public void addButtonClick()
	{
		addButton.doClick();
	}

	public void chatButtonClick()
	{
		chatButton.doClick();
	}

	@Override public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == addButton)
		{
			AddPanel addPanel = new AddPanel(selectedTab);
			DialogDescriptor descriptor = new DialogDescriptor(addPanel, "Add a contact", true, null);
			descriptor.setMessageType(DialogDescriptor.PLAIN_MESSAGE);
			descriptor.setOptions(new Object[]
			{
				DialogDescriptor.OK_OPTION,
				DialogDescriptor.CANCEL_OPTION
			});
			Object ret = DialogDisplayer.getDefault().notify(descriptor);
			if (ret.equals(DialogDescriptor.OK_OPTION))
			{
				switch (selectedTab)
				{
					case 0:
						addPanel.createGroupChat();
						break;
					case 1:
						addPanel.addRosterEntry();
						break;
				}
			}
		}
		else if (e.getSource() == chatButton)
		{
			switch (selectedTab)
			{
				case 0:
					break;
				case 1:
					List<ContactItem> selectedContacts = new ArrayList<ContactItem>(ChatsyManager.getChatManager().getSelectedContactItems());
					if (selectedContacts.isEmpty())
						return;
					if (selectedContacts.size() == 1)
					{
						ContactItem item = selectedContacts.get(0);
						ChatManager chatManager = ChatsyManager.getChatManager();
						boolean handled = chatManager.fireContactItemDoubleClicked(item);
						if (!handled)
							chatManager.activateChat(item.getJID(), item.getDisplayName());
					}
					else
					{
						String nickname = NbPreferences.root().node("/org/chartsy/chat").get("nickname", "");
						String serviceName = "conference.chat.mrswing.com";
						String message = nickname + " invites you to conference.";
						String roomName = nickname + " Conference";
						String roomJID = nickname + "_conference@" + serviceName;
						List<String> jids = new ArrayList<String>();
						for (ContactItem item : selectedContacts)
							jids.add(item.getJID());

						createPrivateConference(message, roomName, roomJID, jids);
					}
					break;
			}
		}
		else if (e.getSource() == searchButton)
		{
			UserSearchService searchService = new UserSearchService();
			searchService.search("");
		}
	}

	private void createPrivateConference(String message, String roomName, String roomJID, Collection<String> jids)
	{
        final MultiUserChat multiUserChat = new MultiUserChat(ChatsyManager.getConnection(), roomJID);
        final GroupChatRoom room = new GroupChatRoom(multiUserChat);
		room.setTabTitle(roomName);
        try
		{
            multiUserChat.create(NbPreferences.root().node("/org/chartsy/chat").get("nickname", ""));
        }
        catch (XMPPException ex)
		{
        }

        try
		{
            Form submitForm = multiUserChat.getConfigurationForm().createAnswerForm();
            submitForm.setAnswer("muc#roomconfig_publicroom", false);
            submitForm.setAnswer("muc#roomconfig_roomname", roomName);
			submitForm.setAnswer("muc#roomconfig_maxusers", 200);

            final List<String> owners = new ArrayList<String>();
            owners.add(ChatsyManager.getSessionManager().getBareAddress());
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);

            multiUserChat.sendConfigurationForm(submitForm);
        }
        catch (XMPPException ex)
		{
        }

        ChatManager chatManager = ChatsyManager.getChatManager();
		ChatRoom chatRoom = chatManager.getChatContainer().getChatRoom(room.getRoomname());
		if (chatRoom == null)
		{
			chatManager.getChatContainer().addChatRoom(room);
            chatManager.getChatContainer().activateChatRoom(room);
		}

        for (String jid : jids)
		{
            multiUserChat.invite(jid, message);
            room.getTranscriptWindow().insertNotificationMessage(
				"Waiting for " + jid + " to join the conference.",
				ChatManager.NOTIFICATION_COLOR);
        }
    }

	private static class CommandButton extends JButton
	{

		private CommandButton(String iconPath, String tooltip)
		{
			if (iconPath != null)
				setIcon(ImageUtilities.loadImageIcon(iconPath, true));
			setOpaque(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setHorizontalAlignment(SwingConstants.CENTER);
			setHorizontalTextPosition(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.CENTER);
			setVerticalTextPosition(SwingConstants.CENTER);
			setFocusable(false);
			setBorderPainted(false);
			setFocusPainted(false);
			setRolloverEnabled(false);
			setContentAreaFilled(false);
			if (tooltip != null)
				setToolTipText(tooltip);
			setPreferredSize(new Dimension(35, 36));
		}

		@Override protected void paintComponent(Graphics g)
		{
			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

			graphics2D.drawImage(buttonBackground, 0, 0, this);

			super.paintComponent(g);
		}

	}

	private static class AddPanel extends JPanel implements ActionListener
	{

		/* UI for conference */
		private JLabel roomNameLabel;
		private JLabel topicLabel;
		private JTextField roomNameField;
		private JTextField topicField;

		/* UI for contact */
		private JLabel usernameLabel;
		private JLabel nicknameLabel;
		private JLabel groupLabel;
		private JTextField usernameField;
		private JTextField nicknameField;
		private JComboBox groupBox;
		private JButton newGroupButton;
		private DefaultComboBoxModel groupModel;

		private AddPanel(int index)
		{
			setLayout(new GridBagLayout());
			setOpaque(false);

			switch (index)
			{
				case 0: // conferences
					roomNameLabel = new JLabel("Room Name:");
					roomNameField = new JTextField();
					topicLabel = new JLabel("Topic:");
					topicField = new JTextField();

					add(roomNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
					add(roomNameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(5, 5, 5, 5), 0, 0));
					add(topicLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 5, 0));
					add(topicField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, 
						GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(5, 5, 5, 5), 0, 0));

					roomNameLabel.setOpaque(false);
					topicLabel.setOpaque(false);
					break;
				case 1: // contacts
					usernameLabel = new JLabel("Username:");
					usernameField = new JTextField();
					nicknameLabel = new JLabel("Nickname:");
					nicknameField = new JTextField();
					groupLabel = new JLabel("Group:");
					groupBox = new JComboBox(groupModel = new DefaultComboBoxModel());
					newGroupButton = new JButton("New Group");

					add(usernameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 2,
						new Insets(5, 5, 5, 5), 0, 0));
					add(usernameField, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 17, 2,
						new Insets(5, 5, 5, 5), 0, 0));
					add(nicknameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 2,
						new Insets(5, 5, 5, 5), 0, 0));
					add(nicknameField, new GridBagConstraints(1, 1, 1, 1, 1.0D, 0.0D, 17, 2,
						new Insets(5, 5, 5, 5), 0, 0));
					add(groupLabel, new GridBagConstraints(0, 4, 1, 1, 0.0D, 0.0D, 17, 2,
						new Insets(5, 5, 5, 5), 0, 0));
					add(groupBox, new GridBagConstraints(1, 4, 1, 1, 1.0D, 0.0D, 17, 2,
						new Insets(5, 5, 5, 5), 0, 0));
					add(newGroupButton, new GridBagConstraints(2, 4, 1, 1, 0.0D, 0.0D, 17, 2,
						new Insets(5, 5, 5, 5), 0, 0));

					usernameLabel.setOpaque(false);
					nicknameLabel.setOpaque(false);
					groupLabel.setOpaque(false);
					newGroupButton.addActionListener((ActionListener)this);
					for (ContactGroup contactGroup : Workspace.getInstance().getContactList().getContactGroups())
						groupModel.addElement(contactGroup.getName());
					groupBox.setEditable(true);
					if (groupModel.getSize() == 0)
						groupBox.addItem("Friends");
					if (groupModel.getSize() > 0)
						groupBox.setSelectedIndex(0);

					break;
			}
		}

		@Override public Dimension getPreferredSize()
		{
			final Dimension dimension = super.getPreferredSize();
			dimension.width = 350;
			return dimension;
		}


		public String getJID()
		{
			String contact = usernameField.getText();
			if (contact.indexOf("@") == -1)
				contact = contact + "@" + ChatsyManager.getConnection().getServiceName();
			else
				if (contact.split("@", 2)[1].equals(ChatsyManager.getConnection().getServiceName()))
					contact = contact.split("@", 2)[0] + "@" + ChatsyManager.getConnection().getServiceName();
			return contact;
		}

		public String getNickname()
		{
			if (!nicknameField.getText().isEmpty())
				return nicknameField.getText();
			else
				return getJID().split("@", 2)[0];
		}

		public String getGroupName()
		{
			return (String) groupBox.getSelectedItem();
		}

		public String getRoomName()
		{
			return roomNameField.getText();
		}

		public String getTopic()
		{
			return topicField.getText();
		}

		public void addRosterEntry()
		{
			final SwingWorker rosterEntryThread = new SwingWorker()
			{
				public Object construct()
				{
					return addEntry(getJID(), getNickname(), getGroupName());
				}
				public void finished()
				{
				}
			};
			rosterEntryThread.start();
		}

		private RosterEntry addEntry(String jid, String nickname, String group)
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
					roster.createEntry(jid, nickname, groups);
				}
				catch (XMPPException ex)
				{
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
			}

			return userEntry;
		}

		public void createGroupChat()
		{
			String roomName = getRoomName();
			String roomJID = roomName.replaceAll(" ", "_") + "@conference.chat.mrswing.com";

			final MultiUserChat multiUserChat = new MultiUserChat(ChatsyManager.getConnection(), roomJID);
			final GroupChatRoom room = new GroupChatRoom(multiUserChat);
			room.setTabTitle(roomName);

			try
			{
				multiUserChat.create(NbPreferences.root().node("/org/chartsy/chat").get("nickname", ""));
			}
			catch (XMPPException ex)
			{
			}

			try
			{
				Form submitForm = multiUserChat.getConfigurationForm().createAnswerForm();
				submitForm.setAnswer("muc#roomconfig_publicroom", true);
				submitForm.setAnswer("muc#roomconfig_roomname", roomName);
				submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", false);
				submitForm.setAnswer("muc#roomconfig_moderatedroom", false);
				submitForm.setAnswer("muc#roomconfig_persistentroom", false);

				final List<String> owners = new ArrayList<String>();
				owners.add(ChatsyManager.getSessionManager().getBareAddress());
				submitForm.setAnswer("muc#roomconfig_roomowners", owners);

				multiUserChat.sendConfigurationForm(submitForm);
			}
			catch (XMPPException ex)
			{
			}

			ChatManager chatManager = ChatsyManager.getChatManager();
			ChatRoom chatRoom = chatManager.getChatContainer().getChatRoom(roomName);
			if (chatRoom == null)
			{
				chatManager.getChatContainer().addChatRoom(room);
				chatManager.getChatContainer().activateChatRoom(room);
			}
		}

		@Override public void actionPerformed(ActionEvent e)
		{
			
		}
		
	}

}
