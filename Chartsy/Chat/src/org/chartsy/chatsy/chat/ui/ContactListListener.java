package org.chartsy.chatsy.chat.ui;

public interface ContactListListener
{

    void contactItemAdded(ContactItem item);
    void contactItemRemoved(ContactItem item);
    void contactGroupAdded(ContactGroup group);
    void contactGroupRemoved(ContactGroup group);
    void contactItemClicked(ContactItem item);
    void contactItemDoubleClicked(ContactItem item);
	
}
