package org.chartsy.chatsy.chatimpl.plugin.gateways.transports;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.chartsy.chatsy.chat.ChatsyManager;
import org.chartsy.chatsy.chat.util.TaskEngine;
import org.chartsy.chatsy.chat.util.log.Log;
import org.chartsy.chatsy.chatimpl.plugin.gateways.GatewayPrivateData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TransportUtils
{

    private static Map<String, Transport> transports = new HashMap<String, Transport>();
    private static GatewayPrivateData gatewayPreferences;

    private TransportUtils()
	{
    }

    static
	{
        PrivateDataManager.addPrivateDataProvider(
			GatewayPrivateData.ELEMENT,
			GatewayPrivateData.NAMESPACE,
			new GatewayPrivateData.ConferencePrivateDataProvider());
        final Runnable loadGateways = new Runnable()
		{
            public void run()
			{
                try
				{
                    PrivateDataManager pdm = ChatsyManager.getSessionManager().getPersonalDataManager();
                    gatewayPreferences = (GatewayPrivateData)pdm.getPrivateData(GatewayPrivateData.ELEMENT, GatewayPrivateData.NAMESPACE);
                }
                catch (XMPPException e)
				{
                    Log.error("Unable to load private data for Gateways", e);
                }
            }
        };
        TaskEngine.getInstance().submit(loadGateways);
    }

    public static boolean autoJoinService(String serviceName)
	{
        return gatewayPreferences.autoLogin(serviceName);
    }

    public static void setAutoJoin(String serviceName, boolean autoJoin)
	{
        gatewayPreferences.addService(serviceName, autoJoin);
        PrivateDataManager pdm = ChatsyManager.getSessionManager().getPersonalDataManager();
        try
		{
            pdm.setPrivateData(gatewayPreferences);
        }
        catch (XMPPException e)
		{
            Log.error(e);
        }
    }

    public static Transport getTransport(String serviceName)
	{
        if (transports.containsKey(serviceName))
            return transports.get(serviceName);
        return null;
    }

    public static boolean isFromGateway(String jid)
	{
        jid = StringUtils.parseBareAddress(jid);
        String serviceName = StringUtils.parseServer(jid);
        return transports.containsKey(serviceName);
    }

    public static void addTransport(String serviceName, Transport transport)
	{
        transports.put(serviceName, transport);
    }

    public static Collection<Transport> getTransports()
	{
        return transports.values();
    }

    public static boolean isRegistered(XMPPConnection con, Transport transport)
	{
        if (!con.isConnected())
            return false;
        ServiceDiscoveryManager discoveryManager = ServiceDiscoveryManager.getInstanceFor(con);
        try
		{
            DiscoverInfo info = discoveryManager.discoverInfo(transport.getServiceName());
            return info.containsFeature("jabber:iq:registered");
        }
        catch (XMPPException e)
		{
            Log.error(e);
        }
        return false;
    }

    public static void registerUser(XMPPConnection con, String gatewayDomain,
		String username, String password, String nickname) throws XMPPException
	{
        Registration registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo(gatewayDomain);
        registration.addExtension(new GatewayRegisterExtension());

        Map<String, String> attributes = new HashMap<String, String>();
        if (username != null)
            attributes.put("username", username);
        if (password != null)
            attributes.put("password", password);
        if (nickname != null)
            attributes.put("nick", nickname);
        registration.setAttributes(attributes);

        PacketCollector collector = con.createPacketCollector(new PacketIDFilter(registration.getPacketID()));
        con.sendPacket(registration);

        IQ response = (IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        if (response == null)
		{
            throw new XMPPException("Server timed out");
        }
        if (response.getType() == IQ.Type.ERROR)
		{
            throw new XMPPException("Error registering user", response.getError());
        }
    }

    public static void unregister(XMPPConnection con, String gatewayDomain)
		throws XMPPException
	{
        Registration registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo(gatewayDomain);
        Map<String,String> map = new HashMap<String,String>();
        map.put("remove", "");
        registration.setAttributes(map);

        PacketCollector collector = con.createPacketCollector(new PacketIDFilter(registration.getPacketID()));
        con.sendPacket(registration);

        IQ response = (IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        if (response == null)
		{
            throw new XMPPException("Server timed out");
        }
        if (response.getType() == IQ.Type.ERROR)
		{
            throw new XMPPException("Error registering user", response.getError());
        }
    }

    static class GatewayRegisterExtension implements PacketExtension
	{

        public String getElementName()
		{
            return "x";
        }

        public String getNamespace()
		{
            return "jabber:iq:gateway:register";
        }

        public String toXML()
		{
            StringBuilder builder = new StringBuilder();
            builder.append("<").append(getElementName()).append(" xmlns=\"")
				.append(getNamespace()).append("\"/>");
            return builder.toString();
        }
		
    }

}
