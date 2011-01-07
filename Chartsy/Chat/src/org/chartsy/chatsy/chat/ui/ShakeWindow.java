package org.chartsy.chatsy.chat.ui;

import org.chartsy.chatsy.chat.ChatsyManager;

import javax.swing.Timer;
import javax.swing.JFrame;

import java.awt.Point;
import java.awt.Window;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShakeWindow
{

    public static final int SHAKE_DISTANCE = 10;
    public static final double SHAKE_CYCLE = 50;
    public static final int SHAKE_DURATION = 1000;
    public static final int SHAKE_UPDATE = 5;

    private Window window;
    private Point naturalLocation;
    private long startTime;
    private Timer shakeTimer;
    private final double TWO_PI = Math.PI * 2.0;
    private boolean added = false;

    public ShakeWindow(Window d)
	{
        window = d;
    }

    public void startShake()
	{
        if (window instanceof JFrame)
		{
            JFrame f = (JFrame)window;
            f.setState(Frame.NORMAL);
            f.setVisible(true);
        }
        ChatsyManager.getNativeManager().flashWindow(window);

        naturalLocation = window.getLocation();
        startTime = System.currentTimeMillis();
        shakeTimer = new Timer(SHAKE_UPDATE, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				long elapsed = System.currentTimeMillis() - startTime;
				double waveOffset = (elapsed % SHAKE_CYCLE) / SHAKE_CYCLE;
				double angle = waveOffset * TWO_PI;
				int shakenX = (int)((Math.sin(angle) * SHAKE_DISTANCE) + naturalLocation.x);
				int shakenY;
				if (added)
				{
					shakenY = naturalLocation.y - 10;
					added = false;
				}
				else
				{
					shakenY = naturalLocation.y + 10;
					added = true;
				}

				window.setLocation(shakenX, shakenY);
				window.repaint();
				if (elapsed >= SHAKE_DURATION)
					stopShake();
			}
		});
        shakeTimer.start();
    }

    public void stopShake()
	{
        shakeTimer.stop();
        window.setLocation(naturalLocation);
        window.repaint();
        ChatsyManager.getNativeManager().stopFlashing(window);
    }

}
