package org.chartsy.chatsy;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import org.jivesoftware.smack.XMPPConnection;
import org.chartsy.chatsy.chat.ChatsyManager;

public final class MainWindow extends JPanel
{

    private boolean focused;
    private JToolBar topToolbar = new JToolBar();
    private JSplitPane splitPane;

    private static MainWindow singleton;
    private static final Object LOCK = new Object();

    public static MainWindow getInstance()
	{
        synchronized (LOCK)
		{
            if (null == singleton)
			{
            	MainWindow controller = new MainWindow();
            	singleton = controller;         		
            }
        }
        return singleton;   	 
    }

    private MainWindow()
	{
        setLayout(new BorderLayout());
		setOpaque(false);
        add(topToolbar, BorderLayout.NORTH);
    }

    public void shutdown()
	{
        final XMPPConnection connection = ChatsyManager.getConnection();
        if (connection.isConnected())
            connection.disconnect();
    }

    public void logout()
	{
        final XMPPConnection connection = ChatsyManager.getConnection();
		if (connection.isConnected())
			connection.disconnect();
    }

    public boolean isInFocus()
	{
        return focused;
    }

    public JToolBar getTopToolBar()
	{
        return topToolbar;
    }

    public JSplitPane getSplitPane()
	{
        if (splitPane == null) 
            splitPane = new JSplitPane();
        return this.splitPane;
    }
	
}
