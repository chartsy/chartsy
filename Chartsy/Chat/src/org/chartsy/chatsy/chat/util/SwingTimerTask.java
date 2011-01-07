package org.chartsy.chatsy.chat.util;

import java.awt.EventQueue;

public abstract class SwingTimerTask extends java.util.TimerTask
{

    public abstract void doRun();

    @Override public void run()
	{
        if (!EventQueue.isDispatchThread())
		{
            EventQueue.invokeLater(this);
        }
        else
		{
            doRun();
        }
    }
	
}
