package org.chartsy.chatsy.chat.ui;

import java.awt.event.MouseEvent;
import java.util.Collection;

public interface ContactGroupListener {

    public void contactItemAdded(ContactItem item);
    public void contactItemRemoved(ContactItem item);
    public void contactItemDoubleClicked(ContactItem item);
    public void contactItemClicked(ContactItem item);
    public void showPopup(MouseEvent e, ContactItem item);
    public void showPopup(MouseEvent e, Collection<ContactItem> items);
    public void contactGroupPopup(MouseEvent e, ContactGroup group);
	
}
