package org.chartsy.chatsy.chatimpl.plugin.gateways.transports;

import javax.swing.Icon;

public class XMPPTransport implements Transport
{

    private String serviceName;

    public XMPPTransport(String serviceName)
	{
        this.serviceName = serviceName;
    }

    public String getTitle() {
        return "XMPP account credentials";
    }

    public String getInstructions()
	{
        return "Enter your XMPP username and password below";
    }

    public Icon getIcon()
	{
        return null;
    }

    public Icon getInactiveIcon()
	{
        return null;
    }

    public String getServiceName()
	{
        return serviceName;
    }

    public String getName()
	{
        return "XMPP";
    }

    public void setServiceName(String serviceName)
	{
        this.serviceName = serviceName;
    }

    public Boolean requiresUsername()
	{
        return true;
    }

    public Boolean requiresPassword()
	{
        return true;
    }

    public Boolean requiresNickname()
	{
        return false;
    }
    
}
