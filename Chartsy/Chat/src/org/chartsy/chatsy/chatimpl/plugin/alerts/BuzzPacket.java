package org.chartsy.chatsy.chatimpl.plugin.alerts;

import org.jivesoftware.smack.packet.PacketExtension;

public class BuzzPacket implements PacketExtension
{

    public String getElementName()
	{
        return "buzz";
    }

    public String getNamespace()
	{
        return "http://www.jivesoftware.com/spark";
    }

    public String toXML()
	{
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\"/>");
        return buf.toString();
    }

}
