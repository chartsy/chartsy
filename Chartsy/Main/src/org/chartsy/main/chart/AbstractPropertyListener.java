package org.chartsy.main.chart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author Viorel
 */
public abstract class AbstractPropertyListener
        implements Serializable
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private final List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList<PropertyChangeListener>());

    public void addPropertyChangeListener(PropertyChangeListener pcl)
    {
		listeners.add(pcl);
	}

    public void removePropertyChangeListener(PropertyChangeListener pcl)
    {
		listeners.remove(pcl);
	}

	public void clearPropertyChangeListenerList()
	{
		listeners.clear();
	}

    protected void fire(String propertyName, Object old, Object nue)
    {
		if (!old.equals(nue))
			for (PropertyChangeListener listener : listeners)
				listener.propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
    }

}
