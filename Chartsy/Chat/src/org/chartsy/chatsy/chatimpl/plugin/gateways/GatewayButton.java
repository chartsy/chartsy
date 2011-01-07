package org.chartsy.chatsy.chatimpl.plugin.gateways;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.chartsy.chatsy.chat.PresenceManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.component.RolloverButton;
import org.chartsy.chatsy.chat.ui.status.StatusBar;
import org.chartsy.chatsy.chat.util.TaskEngine;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.Transport;
import org.chartsy.chatsy.chatimpl.plugin.gateways.transports.TransportUtils;

/**
 *
 */
public class GatewayButton extends JPanel
{
	
	private final RolloverButton button = new RolloverButton();
    private Transport transport;
    private boolean signedIn;

    public GatewayButton(final Transport transport)
	{
        setLayout(new GridBagLayout());
        setOpaque(false);
        this.transport = transport;
        final StatusBar statusBar = ChatsyManager.getWorkspace().getStatusBar();
        final JPanel commandPanel = ChatsyManager.getWorkspace().getCommandPanel();

        if (PresenceManager.isOnline(transport.getServiceName()))
		{
            button.setIcon(transport.getIcon());
        }
        else
		{
            button.setIcon(transport.getInactiveIcon());
        }
        button.setToolTipText(transport.getName());
        button.addMouseListener(new MouseAdapter()
		{
            public void mousePressed(MouseEvent mouseEvent)
			{
                handlePopup(mouseEvent);
            }
        });
		commandPanel.add(button);
		
        commandPanel.updateUI();
        final Runnable registerThread = new Runnable()
		{
            public void run()
			{
                final boolean isRegistered = TransportUtils.isRegistered(ChatsyManager.getConnection(), transport);
                if (isRegistered)
				{
                    boolean autoJoin = TransportUtils.autoJoinService(transport.getServiceName());
                    if (autoJoin)
					{
                        Presence oldPresence = statusBar.getPresence();
                        Presence presence = new Presence(oldPresence.getType(), oldPresence.getStatus(), oldPresence.getPriority(), oldPresence.getMode());
                        presence.setTo(transport.getServiceName());
                        ChatsyManager.getConnection().sendPacket(presence);
                    }
                }
            }
        };
        TaskEngine.getInstance().submit(registerThread);
    }

    private void handlePopup(MouseEvent event)
	{
        final JPopupMenu popupMenu = new JPopupMenu();
        final JMenuItem signOutMenu = new JMenuItem("Sign Out");
        signOutMenu.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                final Presence offlinePresence = new Presence(Presence.Type.unavailable);
                offlinePresence.setTo(transport.getServiceName());
                ChatsyManager.getConnection().sendPacket(offlinePresence);
            }
        });

        final JMenuItem signInMenu = new JMenuItem("Sing In");
        signInMenu.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                final Presence onlinePresence = new Presence(Presence.Type.available);
                onlinePresence.setTo(transport.getServiceName());
                ChatsyManager.getConnection().sendPacket(onlinePresence);
            }
        });

        final JCheckBoxMenuItem signInAtLoginMenu = new JCheckBoxMenuItem();
        signInAtLoginMenu.setText("Sign in at login");
        signInAtLoginMenu.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                TransportUtils.setAutoJoin(transport.getServiceName(), signInAtLoginMenu.isSelected());
            }
        });

        final JMenuItem registerMenu = new JMenuItem("Enter login information");
        registerMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                TransportRegistrationDialog registrationDialog = new TransportRegistrationDialog(transport.getServiceName());
                registrationDialog.invoke();
            }
        });

        final JMenuItem unregisterMenu = new JMenuItem("Delete login information");
        unregisterMenu.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                int confirm = JOptionPane.showConfirmDialog(
					ChatsyManager.getMainWindow(),
					"Remove login information from " + transport.getName() + "?",
					"Remove login information",
					JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION)
				{
                    try
					{
                        TransportUtils.unregister(ChatsyManager.getConnection(), transport.getServiceName());
                    }
                    catch (XMPPException e1)
					{
                        Log.error(e1);
                    }
                }
            }
        });

        boolean reg = TransportUtils.isRegistered(ChatsyManager.getConnection(), transport);
        if (!reg)
		{
            popupMenu.add(registerMenu);
            popupMenu.addSeparator();
            signInMenu.setEnabled(false);
            popupMenu.add(signInMenu);
            signInAtLoginMenu.setEnabled(false);
            popupMenu.add(signInAtLoginMenu);
            popupMenu.show((Component)event.getSource(), event.getX(), event.getY());
            return;
        }

        if (signedIn)
		{
            popupMenu.add(signOutMenu);
        }
        else
		{
            popupMenu.add(signInMenu);
        }

        boolean autoJoin = TransportUtils.autoJoinService(transport.getServiceName());
        signInAtLoginMenu.setSelected(autoJoin);

        popupMenu.add(signInAtLoginMenu);
        popupMenu.addSeparator();
        popupMenu.add(unregisterMenu);
        popupMenu.show((Component)event.getSource(), event.getX(), event.getY());
    }

    public void signedIn(boolean signedIn)
	{
        if (!signedIn)
		{
            button.setIcon(transport.getInactiveIcon());
        }
        else
		{
            button.setIcon(transport.getIcon());
        }

        this.signedIn = signedIn;
    }

    public boolean isLoggedIn()
	{
        return signedIn;
    }

}
