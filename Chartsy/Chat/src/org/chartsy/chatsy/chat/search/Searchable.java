package org.chartsy.chatsy.chat.search;

import javax.swing.Icon;

public interface Searchable
{

    Icon getIcon();
    String getName();
    String getDefaultText();
    String getToolTip();
    void search(String query);
	
}
