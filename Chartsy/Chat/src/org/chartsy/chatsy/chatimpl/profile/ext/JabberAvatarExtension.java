package org.chartsy.chatsy.chatimpl.profile.ext;

import org.jivesoftware.smack.packet.PacketExtension;

public class JabberAvatarExtension implements PacketExtension
{

    private String photoHash;

    public void setPhotoHash(String hash)
	{
        photoHash = hash;
    }

    public String getElementName()
	{
        return "x";
    }

    public String getNamespace()
	{
        return "jabber:x:avatar";
    }

    public String toXML()
	{
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        buf.append("<hash>");
        buf.append(photoHash);
        buf.append("</hash>");
        buf.append("</").append(getElementName()).append(">");
        return buf.toString();
    }
	
}