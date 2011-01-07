package org.chartsy.chatsy.chatimpl.profile.ext;

import org.jivesoftware.smack.packet.PacketExtension;

public class VCardUpdateExtension implements PacketExtension
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
        return "vcard-temp:x:update";
    }

    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        buf.append("<photo>");
        buf.append(photoHash);
        buf.append("</photo>");
        buf.append("</").append(getElementName()).append(">");
        return buf.toString();
    }
	
}
