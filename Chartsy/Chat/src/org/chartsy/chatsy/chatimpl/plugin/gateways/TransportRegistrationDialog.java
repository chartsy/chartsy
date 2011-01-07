package org.chartsy.chatsy.chatimpl.plugin.gateways;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.RolloverButton;
import org.chartsy.chatsy.chat.component.TitlePanel;
import org.chartsy.chatsy.chat.ui.status.StatusBar;
import org.chartsy.chatsy.chat.util.ModelUtil;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.Transport;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.TransportUtils;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

public class TransportRegistrationDialog extends JPanel implements ActionListener, KeyListener
{
	
	private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JTextField nicknameField = new JTextField();
    private RolloverButton registerButton = new RolloverButton("Save", null);
    private RolloverButton cancelButton = new RolloverButton("Cancel", null);
    private JDialog dialog;
    private String serviceName;
    private Transport transport;

    public TransportRegistrationDialog(String serviceName)
	{
        setLayout(new GridBagLayout());
        this.serviceName = serviceName;
		
        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(registerButton);
        registerButton.requestFocus();
        buttonPanel.add(cancelButton);

        transport = TransportUtils.getTransport(serviceName);
        final TitlePanel titlePanel = new TitlePanel(
			transport.getTitle(),
			transport.getInstructions(),
			transport.getIcon(),
			true);

        int line = 0;
        add(titlePanel, new GridBagConstraints(0, line, 2, 1, 1.0, 1.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 0, 0), 0, 0));

        line++;
        final JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        add(usernameLabel, new GridBagConstraints(0, line, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(usernameField, new GridBagConstraints(1, line, 1, 1, 1.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        line++;
        final JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        add(passwordLabel, new GridBagConstraints(0, line, 1, 1, 0.0, 0.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5), 0, 0));
        add(passwordField, new GridBagConstraints(1, line, 1, 1, 1.0, 1.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));

        if (transport.requiresNickname())
		{
            line++;
            final JLabel nicknameLabel = new JLabel("Nickname");
            nicknameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
            add(nicknameLabel, new GridBagConstraints(0, line, 1, 1, 0.0, 0.0, 
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
            add(nicknameField, new GridBagConstraints(1, line, 1, 1, 1.0, 0.0, 
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0,0));
        }

        line++;
        add(buttonPanel, new GridBagConstraints(0, line, 2, 1, 1.0, 1.0, 
			GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			new Insets(5, 5, 5, 5), 0, 0));
    }

    public void invoke()
	{
		JFrame parent = new JFrame();
        dialog = new JDialog(parent, transport.getTitle(), false);
        dialog.add(this);
        dialog.pack();
        dialog.setSize(400, 200);
        dialog.setVisible(true);

        usernameField.requestFocus();
        usernameField.addKeyListener(this);
        passwordField.addKeyListener(this);
        registerButton.addActionListener(this);
        cancelButton.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                dialog.dispose();
            }
        });
    }

    public String getScreenName()
	{
        return usernameField.getText();
    }

    public String getPassword()
	{
        return new String(passwordField.getPassword());
    }

    public String getNickname()
	{
        return nicknameField.getText();
    }

    public void actionPerformed(ActionEvent e)
	{
        String username = getScreenName();
        String password = getPassword();
        String nickname = getNickname();
		
        if (transport.requiresUsername() && !ModelUtil.hasLength(username))
		{
            JOptionPane.showMessageDialog(
				this,
				"Username needs to be supplied",
				"Registration error",
				JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (transport.requiresPassword() && !ModelUtil.hasLength(password))
		{
            JOptionPane.showMessageDialog(
				this,
				"Password needs to be supplied",
				"Registration error",
				JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (transport.requiresNickname() && !ModelUtil.hasLength(nickname))
		{
            JOptionPane.showMessageDialog(
				this,
				"Nickname needs to be supplied",
				"Registration error",
				JOptionPane.ERROR_MESSAGE);
            return;
        }

        try
		{
            TransportUtils.registerUser(ChatsyManager.getConnection(), serviceName, username, password, nickname);
            final StatusBar statusBar = ChatsyManager.getWorkspace().getStatusBar();
            Presence presence = statusBar.getPresence();
            presence.setTo(transport.getServiceName());
            ChatsyManager.getConnection().sendPacket(presence);
        }
        catch (XMPPException e1)
		{
            e1.printStackTrace();
            JOptionPane.showMessageDialog(
				this,
				"Unable to register with gateway",
				"Registration error",
				JOptionPane.ERROR_MESSAGE);
        }

        dialog.dispose();
    }


    public void keyTyped(KeyEvent keyEvent)
	{
    }

    public void keyPressed(KeyEvent keyEvent)
	{
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
		{
            actionPerformed(null);
        }
        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
            dialog.dispose();
        }
    }

    public void keyReleased(KeyEvent keyEvent)
	{
    }
	
}
