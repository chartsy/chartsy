package org.chartsy.chatsy.chatimpl.plugin.gateways;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.xmlpull.v1.XmlPullParser;

public class Gateway extends IQ
{

    private String jid;
    private String username;


    public String getJid()
	{
        return jid;
    }

    public void setJid(String jid)
	{
        this.jid = jid;
    }

    public String getUsername()
	{
        return username;
    }

    public void setUsername(String username)
	{
        this.username = username;
    }

    public static final String ELEMENT_NAME = "query";
    public static final String NAMESPACE = "jabber:iq:gateway";

    public String getChildElementXML()
	{
        StringBuilder buf = new StringBuilder();
        buf.append("<query xmlns=\"").append(NAMESPACE).append("\">");
        buf.append("<prompt>").append(username).append("</prompt>");
        buf.append("</query>");
        return buf.toString();
    }

    public static class Provider implements IQProvider
	{

        public Provider()
		{
            super();
        }

        public IQ parseIQ(XmlPullParser parser) throws Exception
		{
            Gateway version = new Gateway();

            boolean done = false;
            while (!done)
			{
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG)
				{
                    if (parser.getName().equals("jid"))
					{
                        version.setJid(parser.nextText());
                    }
                    else if (parser.getName().equals("username"))
					{
                        version.setUsername(parser.nextText());
                    }
                }
                else if (eventType == XmlPullParser.END_TAG)
				{
                    if (parser.getName().equals(ELEMENT_NAME))
					{
                        done = true;
                    }
                }
            }

            return version;
        }
    }

    public static String getJID(String serviceName, String username) throws XMPPException
	{
        Gateway registration = new Gateway();
        registration.setType(IQ.Type.SET);
        registration.setTo(serviceName);
        registration.setUsername(username);

        XMPPConnection con = ChatsyManager.getConnection();
        PacketCollector collector = con.createPacketCollector(new PacketIDFilter(registration.getPacketID()));
        con.sendPacket(registration);

        Gateway response = (Gateway)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        if (response == null)
		{
            throw new XMPPException("Server timed out");
        }
        if (response.getType() == IQ.Type.ERROR)
		{
            throw new XMPPException("Error registering user", response.getError());
        }

        return response.getJid();
    }


}


