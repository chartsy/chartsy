package org.chartsy.chatsy.chatimpl.plugin.gateways.transports;

import javax.swing.Icon;

public class SimpleTransport implements Transport
{

    private String serviceName;

    public SimpleTransport(String serviceName)
	{
        this.serviceName = serviceName;
    }

    public String getTitle()
	{
        return "SIMPLE account credentials";
    }

    public String getInstructions()
	{
        return "Enter your SIMPLE username and password below";
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
        return "SIMPLE";
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
