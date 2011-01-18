package org.chartsy.chatsy;

import java.awt.Dimension;
import org.chartsy.chatsy.chat.component.ChatCheckBox;
import org.chartsy.chatsy.chat.component.ChatButton;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.util.StringUtils;
import org.chartsy.chatsy.chat.SessionManager;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.Workspace;
import org.chartsy.chatsy.chat.util.SwingWorker;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chat.util.log.NotifyUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public class ChatTopComponent extends TopComponent implements PreferenceChangeListener
{

	private static ChatTopComponent instance;
	static final String ICON_PATH = "org/chartsy/chatsy/resources/chat.png";
	private static final String PREFERRED_ID = "ChatTopComponent";
	private Preferences preferences = NbPreferences.root().node("/org/chartsy/register");
	private Preferences chatPref = NbPreferences.root().node("/org/chartsy/chat");

	private RegisterPanel registerPanel = null;
	private LoginPanel loginPanel = null;

	private ChatTopComponent()
	{
		putClientProperty("TopComponentAllowDockAnywhere", Boolean.TRUE);
		setName(NbBundle.getMessage(ChatTopComponent.class, "CTL_ChatFrame"));
        setToolTipText(NbBundle.getMessage(ChatTopComponent.class, "HINT_ChatFrame"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
		initComponents();
	}

	public static synchronized ChatTopComponent getDefault()
	{
		if (instance == null)
			instance = new ChatTopComponent();
		return instance;
	}

	public static synchronized ChatTopComponent findInstance()
	{
		TopComponent tc = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
		if (tc == null)
		{
			Log.warning(
				"Cannot find " + PREFERRED_ID + " component. It will not be "
				+ "located properly in the window system.");
			return getDefault();
		}
		if (tc instanceof ChatTopComponent)
			return (ChatTopComponent) tc;
		Log.warning(
			"There seem to be multiple components with the '" + PREFERRED_ID +
			"' ID. That is a potential source of errors and unexpected behavior.");
		return getDefault();
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());
		setOpaque(true);

		chatPref.putBoolean("loggedin", false);
		if (preferences.getBoolean("registred", false))
		{
			if (loginPanel == null)
				loginPanel = new LoginPanel();
			add(loginPanel, BorderLayout.CENTER);
		}
		else
		{
			if (registerPanel == null)
				registerPanel = new RegisterPanel();
			add(registerPanel, BorderLayout.CENTER);
		}
		preferences.addPreferenceChangeListener((PreferenceChangeListener) this);
	}

	public void putLoginScreen()
	{
		removeAll();
		if (loginPanel == null)
			loginPanel = new LoginPanel();
		add(loginPanel, BorderLayout.CENTER);
		validate();
		repaint();
	}

	public void putChatsScreen()
	{
		removeAll();
		add(MainWindow.getInstance());
		validate();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		if (!isOpaque())
		{
			super.paintComponent(g);
			return;
		}

		Graphics2D g2 = (Graphics2D) g;
		int w = getWidth();
		int h = getHeight();

		GradientPaint gradientPaint = new GradientPaint(
			0, h, Color.decode("0x065a9d"),
			w, 0, Color.decode("0x0298db"));
		g2.setPaint(gradientPaint);
		g2.fillRect(0, 0, w, h);

		setOpaque(false);
		super.paintComponent(g);
		setOpaque(true);
	}

	@Override public int getPersistenceType()
	{
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override protected String preferredID()
	{
        return PREFERRED_ID;
    }

	@Override protected void componentClosed()
	{
		if (chatPref.getBoolean("loggedin", false))
		{
			MainWindow.getInstance().logout();
			chatPref.putBoolean("loggedin", false);
		}
		super.componentClosed();
	}

	@Override public void preferenceChange(PreferenceChangeEvent evt)
	{
		preferences = evt.getNode();
		if (preferences.getBoolean("registred", false))
		{
			if (loginPanel == null)
				loginPanel = new LoginPanel();
			removeAll();
			add(loginPanel, BorderLayout.CENTER);
			validate();
			repaint();
		}
		else
		{
			if (registerPanel == null)
				registerPanel = new RegisterPanel();
			removeAll();
			add(registerPanel, BorderLayout.CENTER);
			validate();
			repaint();
		}
	}

	private final class RegisterPanel extends JPanel
	{

		private JLabel registerLabel;

		public RegisterPanel()
		{
			setLayout(new BorderLayout());
			setOpaque(false);
			registerLabel = new JLabel("<html>To start the chat you need to be registred.</html>");
			registerLabel.setOpaque(false);
			registerLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			registerLabel.setForeground(Color.white);
			registerLabel.setHorizontalTextPosition(JLabel.CENTER);
			registerLabel.setVerticalTextPosition(JLabel.CENTER);
			add(registerLabel, BorderLayout.CENTER);
		}

	}

	private final class LoginPanel extends JPanel
		implements ActionListener, CallbackHandler
	{

		private static final String ICON_PATH = "org/chartsy/chatsy/resources/login_logo.png";
		private final JLabel logoLabel = new JLabel();
		private final ChatCheckBox autoLoginBox = new ChatCheckBox("Auto Login");
		private final ChatButton loginButton = new ChatButton("Login");

		private final LoadingLabel loadingLabel;
		private final CardLayout cardLayout = new CardLayout(0, 5);
		final JPanel cardPanel = new JPanel(cardLayout);

		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		private XMPPConnection connection = null;

		public LoginPanel()
		{
			setLayout(new GridBagLayout());
			setOpaque(false);

			autoLoginBox.setOpaque(false);
			logoLabel.setOpaque(false);

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.insets = new Insets(20,0,15,0);

			logoLabel.setIcon(ImageUtilities.loadImageIcon(ICON_PATH, true));
			add(logoLabel, gbc);

			autoLoginBox.setSelected(chatPref.getBoolean("autologin", false));
			add(autoLoginBox, gbc);

			buttonPanel.add(loginButton);
			buttonPanel.setOpaque(false);

			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.insets = new Insets(5,0,5,0);
			add(buttonPanel, gbc);

			ImageIcon icon = new ImageIcon(ChatTopComponent.class.getResource("resources/loading.gif"));
			loadingLabel = new LoadingLabel(icon);
			add(loadingLabel, gbc);

			loginButton.addActionListener((ActionListener)this);
			autoLoginBox.addActionListener((ActionListener)this);

			if (preferences.getBoolean("registred", false))
				if (chatPref.getBoolean("autologin", false))
					loginButton.doClick();
		}

		private String getUsername()
		{
			return StringUtils.escapeNode(preferences.get("username", ""));
		}

		private String getPassword()
		{
			return preferences.get("password", "");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == loginButton)
				validateLogin();
			else if (e.getSource() == autoLoginBox)
				chatPref.putBoolean("autologin", autoLoginBox.isSelected());
		}

		@Override
		public void handle(Callback[] callbacks)
			throws IOException, UnsupportedCallbackException
		{
			for (Callback callback : callbacks)
			{
				if (callback instanceof NameCallback)
				{
					NameCallback ncb = (NameCallback) callback;
					ncb.setName(getUsername());
				}
				else if (callback instanceof PasswordCallback)
				{
					PasswordCallback pcb = (PasswordCallback) callback;
					pcb.setPassword(getPassword().toCharArray());
				}
				else
				{
					String message = "Unknown callback requested: "
						+ callback.getClass().getSimpleName();
					Log.error(message);
				}
			}
		}

		private void validateLogin()
		{
			final SwingWorker loginValidationThread = new SwingWorker()
			{
				@Override
				public Object construct()
				{
					boolean loginSuccessfull = login();
					if (loginSuccessfull)
					{
						chatPref.putBoolean("loggedin", true);
						startChat();
					}
					else
					{
						loadingLabel.setVisible(false);
					}
					return loginSuccessfull;
				}
			};
			loadingLabel.setVisible(true);
			loginValidationThread.start();
		}

		private boolean login()
		{
			final SessionManager sessionManager = ChatsyManager.getSessionManager();
			boolean hasErrors = false;
			String errorMessage = null;

			String serverName = "chat.mrswing.com";
			if (!hasErrors)
			{
				SmackConfiguration.setPacketReplyTimeout(10000);
				try
				{
					ConnectionConfiguration config = new ConnectionConfiguration(serverName);
					config.setReconnectionAllowed(true);
					config.setRosterLoadedAtLogin(true);
					config.setSendPresence(true);
					connection = new XMPPConnection(config, this);
					connection.connect();
					connection.login(getUsername(), getPassword());
					sessionManager.setServerAddress(connection.getServiceName());
					sessionManager.initializeSession(connection, getUsername(), getPassword());
					sessionManager.setJID(connection.getUser());
				}
				catch (XMPPException ex)
				{
					final XMPPError error = ex.getXMPPError();
					int errorCode = 0;
					if (error != null) errorCode = error.getCode();
					if (errorCode == 401)
						errorMessage = "Invalid username or password";
					else if (errorCode == 502 || errorCode == 504)
						errorMessage = "Server unavailable";
					else if (errorCode == 409)
						errorMessage = "Conflict error";
					else
						errorMessage = "Can't connect to server";

					Log.error(errorMessage, ex);
					hasErrors = true;
				}

				if (hasErrors)
				{
					final String finalErrorMessage = errorMessage;
					EventQueue.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							chatPref.putBoolean("loggedin", false);
							loadingLabel.setVisible(false);
							NotifyUtil.error("Login Error", finalErrorMessage, false);
						}
					});

					return false;
				}
			}

			connection.addConnectionListener(ChatsyManager.getSessionManager());
			return !hasErrors;
		}

		private void startChat()
		{
			try
			{
				EventQueue.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						chatPref.putBoolean("loggedin", true);
						Presence presence = new Presence(Presence.Type.available);
						connection.sendPacket(presence);

						final MainWindow mainWindow = MainWindow.getInstance();
						Workspace workspace = Workspace.getInstance();
						mainWindow.add(workspace.getCardPanel(), BorderLayout.CENTER);
						putChatsScreen();
						workspace.buildLayout();
					}
				});
			} catch (Exception e)
			{
				Log.error(e);
			}
		}

	}

	private final class LoadingLabel extends JLabel
	{

		private final Image background;

		private LoadingLabel(ImageIcon icon)
		{
			super(icon);
			setOpaque(false);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
			setVisible(false);
			background = ImageUtilities.loadImage("org/chartsy/chatsy/resources/loading-bg.png", true);
		}

		@Override protected void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.drawImage(background, 0, 0, this);
			super.paintComponent(g);
		}

		@Override public Dimension getPreferredSize()
		{
			Dimension dimension = new Dimension(46, 48);
			return dimension;
		}

	}

}
