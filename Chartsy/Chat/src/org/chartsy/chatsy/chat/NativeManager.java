package org.chartsy.chatsy.chat;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.chartsy.chatsy.chat.util.ModelUtil;

public class NativeManager
{

    private List<NativeHandler> nativeHandlers = new ArrayList<NativeHandler>();

    public NativeManager()
	{
    }

    public void addNativeHandler(NativeHandler nativeHandler)
	{
        nativeHandlers.add(nativeHandler);
    }

    public void removeNativeHandler(NativeHandler nativeHandler)
	{
        nativeHandlers.remove(nativeHandler);
    }

    public void flashWindow(Window window)
	{
        final Iterator alertNotifier = ModelUtil.reverseListIterator(nativeHandlers.listIterator());
        while (alertNotifier.hasNext())
		{
            final NativeHandler alert = (NativeHandler)alertNotifier.next();
            boolean handle = alert.handleNotification();
            if (handle)
			{
                alert.flashWindow(window);
                break;
            }
        }
    }

    public void flashWindowStopOnFocus(Window window)
	{
        final Iterator alertNotifiers = ModelUtil.reverseListIterator(nativeHandlers.listIterator());
        while (alertNotifiers.hasNext())
		{
            final NativeHandler alert = (NativeHandler)alertNotifiers.next();
            boolean handle = alert.handleNotification();
            if (handle)
			{
                alert.flashWindowStopWhenFocused(window);
                break;
            }
        }
    }

    public void stopFlashing(Window window)
	{
        final Iterator alertNotifiers = ModelUtil.reverseListIterator(nativeHandlers.listIterator());
        while (alertNotifiers.hasNext())
		{
            final NativeHandler alert = (NativeHandler)alertNotifiers.next();
            boolean handle = alert.handleNotification();
            if (handle)
			{
                alert.stopFlashing(window);
                break;
            }
        }
    }

}
