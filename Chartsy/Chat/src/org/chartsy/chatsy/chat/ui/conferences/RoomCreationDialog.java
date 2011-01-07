package org.chartsy.chatsy.chat.ui.conferences;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.TitlePanel;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chat.util.log.Log;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RoomCreationDialog extends JPanel
{

	private static final long serialVersionUID = -8391698290385575601L;
    private JLabel nameLabel = new JLabel("Room name");
    private JLabel topicLabel = new JLabel("Room topic");
    private JLabel passwordLabel = new JLabel("Password");
    private JLabel confirmPasswordLabel = new JLabel("Confirm password");
    private JCheckBox permanentCheckBox = new JCheckBox("Room is permanent");
    private JCheckBox privateCheckbox = new JCheckBox("Room is private");
    private JTextField nameField = new JTextField();
    private JTextField topicField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JPasswordField confirmPasswordField = new JPasswordField();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private MultiUserChat groupChat = null;

    public RoomCreationDialog()
	{
        try
		{
            jbInit();
        }
        catch (Exception e)
		{
            Log.error(e);
        }
    }

    private void jbInit() throws Exception
	{
        this.setLayout(gridBagLayout1);
        this.add(confirmPasswordField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(passwordField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(topicField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(nameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(privateCheckbox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(permanentCheckBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(confirmPasswordLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(passwordLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        this.add(topicLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 5, 0));
        this.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
    }

    public MultiUserChat createGroupChat(Component parent, final String serviceName)
	{
        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;
        titlePanel = new TitlePanel(
			"Create/join",
			"Create or join a conference chat room", null, true);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        Object[] options = {"Create", "Close"};
        pane = new JOptionPane(
			this,
			JOptionPane.PLAIN_MESSAGE,
			JOptionPane.OK_CANCEL_OPTION,
			ChatsyManager.getApplicationImage(),
			options,
			options[0]);
        mainPanel.add(pane, BorderLayout.CENTER);

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog(parent, "Conference rooms");
        dlg.pack();
        dlg.setSize(400, 350);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener()
		{
            public void propertyChange(PropertyChangeEvent e)
			{
                Object o = pane.getValue();
                if (o instanceof Integer)
				{
                    dlg.setVisible(false);
                    return;
                }

                String value = (String)pane.getValue();
                if ("Close".equals(value))
				{
                    dlg.setVisible(false);
                }
                else if ("Create".equals(value))
				{
                    boolean isValid = validatePanel();
                    if (isValid)
					{
                        String room = nameField.getText().replaceAll(" ", "_") + "@" + serviceName;
                        try
						{
                            MultiUserChat.getRoomInfo(ChatsyManager.getConnection(), room);
                            pane.removePropertyChangeListener(this);
                            dlg.setVisible(false);
                            ConferenceUtils.joinConferenceRoom(room, room);
                            return;
                        }
                        catch (XMPPException ex)
						{
                        }

                        groupChat = createGroupChat(nameField.getText(), serviceName);
                        if (groupChat == null)
						{
                            showError("Could not join chat " + nameField.getText());
                            pane.setValue(JOptionPane.UNINITIALIZED_VALUE);

                        }
                        else
						{
                            pane.removePropertyChangeListener(this);
                            dlg.setVisible(false);
                        }
                    }
                    else
					{
                        pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    }
                }
            }
        };

        pane.addPropertyChangeListener(changeListener);
        nameField.requestFocusInWindow();

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();

        return groupChat;
    }

    private boolean validatePanel()
	{
        String roomName = nameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        boolean isPrivate = privateCheckbox.isSelected();

        if (!ModelUtil.hasLength(roomName))
		{
            showError("Specify a valid name");
            nameField.requestFocus();
            return false;
        }

        if (isPrivate)
		{
            if (!ModelUtil.hasLength(password))
			{
                showError("Specify password for the private room");
                passwordField.requestFocus();
                return false;
            }

            if (!ModelUtil.hasLength(confirmPassword))
			{
                showError("Specify a confirmation password");
                confirmPasswordField.requestFocus();
                return false;
            }

            if (!ModelUtil.areEqual(password, confirmPassword))
			{
                showError("Passwords do not match");
                passwordField.requestFocus();
                return false;
            }
        }

        return true;
    }

    private MultiUserChat createGroupChat(String roomName, String serviceName)
	{
        String room = roomName.replaceAll(" ", "_") + "@" + serviceName;
        return new MultiUserChat(ChatsyManager.getConnection(), room.toLowerCase());
    }

    private void showError(String errorMessage)
	{
        JOptionPane.showMessageDialog(
			this,
			errorMessage,
			"Error",
			JOptionPane.ERROR_MESSAGE);
    }

    public boolean isPrivate()
	{
        return privateCheckbox.isSelected();
    }

    public boolean isPermanent()
	{
        return permanentCheckBox.isSelected();
    }

    public boolean isPasswordProtected()
	{
        String password = new String(passwordField.getPassword());
        if (password.length() > 0) 
            return true;
        return false;
    }

    public String getPassword()
	{
        return new String(confirmPasswordField.getPassword());
    }

    public String getRoomName()
	{
        return nameField.getText();
    }

}