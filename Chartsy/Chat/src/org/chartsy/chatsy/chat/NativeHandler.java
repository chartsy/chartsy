package org.chartsy.chatsy.chat;

import java.awt.Window;

public interface NativeHandler
{

    void flashWindow(Window window);
    void flashWindowStopWhenFocused(Window window);
    void stopFlashing(Window window);
    boolean handleNotification();
	
}
