package org.chartsy.chatsy.chatimpl.plugin.gateways.transports;

import javax.swing.Icon;

public interface Transport
{

    String getTitle();
    String getInstructions();
    Icon getIcon();
    Icon getInactiveIcon();
    String getServiceName();
    String getName();
    Boolean requiresUsername();
    Boolean requiresPassword();
    Boolean requiresNickname();

}
