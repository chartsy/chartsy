package org.chartsy.chatsy.chat.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

import org.chartsy.chatsy.chat.ChatsyManager;
import org.openide.windows.WindowManager;

public class ChatFrame extends JFrame implements WindowFocusListener
{

    private long inactiveTime;
    private boolean focused;

	public ChatFrame()
	{
    	this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        setIconImage(ChatsyManager.getApplicationImage().getImage());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(ChatsyManager.getChatManager().getChatContainer(), BorderLayout.CENTER);

		setSize(500, 400);
		setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
		
        addWindowFocusListener(this);
        addWindowListener(new WindowAdapter()
		{
            public void windowActivated(WindowEvent e)
			{
                inactiveTime = 0;
            }
            public void windowDeactivated(WindowEvent e)
			{
                inactiveTime = System.currentTimeMillis();
            }
            public void windowIconified(WindowEvent e)
			{
            }
            public void windowDeiconified(WindowEvent e)
			{
                setFocusableWindowState(true);
            }
        });

        addComponentListener(new ComponentAdapter()
		{
            public void componentResized(ComponentEvent e)
			{
				try
				{
					ChatRoom chatRoom = ChatsyManager.getChatManager().getChatContainer().getActiveChatRoom();
					chatRoom.getVerticalSlipPane().setDividerLocation(-1);
				}
				catch (ChatRoomNotFoundException ex)
				{
				}
            }
        });
    }

    public void windowGainedFocus(WindowEvent e)
	{
        focused = true;
        ChatsyManager.getChatManager().getChatContainer().focusChat();
    }

    public void windowLostFocus(WindowEvent e)
	{
        focused = false;
    }

    public boolean isInFocus()
	{
        return focused;
    }

    public long getInactiveTime()
	{
        if (inactiveTime == 0) 
            return 0;
        return System.currentTimeMillis() - inactiveTime;
    }

    public void bringFrameIntoFocus()
	{
        if (!isVisible()) 
            setVisible(true);
        if (getState() == Frame.ICONIFIED) 
            setState(Frame.NORMAL);
        toFront();
        requestFocus();
    }

    public void buzz()
	{
        ShakeWindow shakeWindow = new ShakeWindow(this);
        shakeWindow.startShake();
    }
	
}
