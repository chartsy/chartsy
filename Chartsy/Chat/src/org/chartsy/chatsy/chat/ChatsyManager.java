package org.chartsy.chatsy.chat;

import org.chartsy.chatsy.MainWindow;
import org.chartsy.chatsy.Chatsy;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.MessageEventManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.chartsy.chatsy.chat.search.SearchManager;
import org.chartsy.chatsy.chat.ui.ContactList;
import org.chartsy.chatsy.chatimpl.profile.VCardManager;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;
import org.chartsy.chatsy.chat.util.log.Log;
import org.openide.windows.WindowManager;

public final class ChatsyManager
{

    private static final String dateFormat = ((SimpleDateFormat)SimpleDateFormat
		.getDateTimeInstance(SimpleDateFormat.FULL,SimpleDateFormat.MEDIUM)).toPattern();
    public static final SimpleDateFormat DATE_SECOND_FORMATTER = new SimpleDateFormat(dateFormat);

    private static SessionManager sessionManager;
    private static MessageEventManager messageEventManager;
    private static UserManager userManager;
    private static ChatManager chatManager;
    private static VCardManager vcardManager;
    private static NativeManager nativeManager;
    private static Component focusedComponent;

    private ChatsyManager()
	{
    }

    static
	{
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addPropertyChangeListener(new PropertyChangeListener() 
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				String prop = e.getPropertyName();
				if (("focusOwner".equals(prop)) && (e.getNewValue() != null))
					focusedComponent = (Component)e.getNewValue();
			}
		});
    }

	public static XMPPConnection getConnection()
	{
        return sessionManager.getConnection();
    }

    public static MainWindow getMainWindow()
	{
        return MainWindow.getInstance();
    }

	public static Workspace getWorkspace()
	{
        return Workspace.getInstance();
    }

    public static SessionManager getSessionManager()
	{
        if (sessionManager == null) 
            sessionManager = new SessionManager();
        return sessionManager;
    }

    public static UserManager getUserManager()
	{
        if (userManager == null) 
            userManager = new UserManager();
        return userManager;
    }

    public static ChatManager getChatManager()
	{
        if (chatManager == null) 
            chatManager = ChatManager.getInstance();
        return chatManager;
    }

    public static MessageEventManager getMessageEventManager()
	{
        if (messageEventManager == null) 
            messageEventManager = new MessageEventManager(getConnection());
        return messageEventManager;
    }

    public static VCardManager getVCardManager()
	{
        if (vcardManager == null) 
            vcardManager = new VCardManager();
        return vcardManager;
    }

    public static NativeManager getNativeManager()
	{
        if (nativeManager == null) 
            nativeManager = new NativeManager();
        return nativeManager;
    }

    public static String getClipboard()
	{
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try
		{
            if (transferable != null
				&& transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
                return (String)transferable.getTransferData(DataFlavor.stringFlavor);
        }
        catch (Exception e)
		{
            Log.error(e);
        }
        return null;
    }

    public static void setClipboard(String str)
	{
        StringSelection ss = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    public static SearchManager getSearchManager()
	{
        return SearchManager.getInstance();
    }

    public static ContactList getContactList()
	{
        return getWorkspace().getContactList();
    }

    public static File getUserDirectory()
	{
        final String bareJID = sessionManager.getBareAddress();
        File userDirectory = new File(Chatsy.getChatUserHome(), bareJID);
        if (!userDirectory.exists()) 
            userDirectory.mkdirs();
        return userDirectory;
    }

    public static Component getFocusedComponent()
	{
        return focusedComponent;
    }

    public static void addFeature(String namespace)
	{
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(getConnection());
        discoManager.addFeature(namespace);
    }

    public static void removeFeature(String namespace)
	{
        ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(getConnection());
        discoManager.removeFeature(namespace);
    }

    public static ImageIcon getApplicationImage()
	{
        return new ImageIcon(WindowManager.getDefault().getMainWindow().getIconImage());
    }

}
