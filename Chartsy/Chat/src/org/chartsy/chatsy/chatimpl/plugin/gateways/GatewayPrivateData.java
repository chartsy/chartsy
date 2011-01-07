package org.chartsy.chatsy.chatimpl.plugin.gateways;

import org.jivesoftware.smackx.packet.PrivateData;
import org.jivesoftware.smackx.provider.PrivateDataProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;

public class GatewayPrivateData implements PrivateData
{

    private final Map<String, String> loginSettingsMap = new HashMap<String, String>();
    public static final String ELEMENT = "gateway-settings";
    public static final String NAMESPACE = "http://www.jivesoftware.org/spark";

    public void addService(String serviceName, boolean autoLogin)
	{
        loginSettingsMap.put(serviceName, Boolean.toString(autoLogin));
    }

    public boolean autoLogin(String serviceName)
	{
        String str = loginSettingsMap.get(serviceName);
        if (str == null)
            return true;
        return Boolean.parseBoolean(str);
    }

    public String getElementName()
	{
        return ELEMENT;
    }

    public String getNamespace()
	{
        return NAMESPACE;
    }

    public String toXML()
	{
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
        buf.append("<gateways>");
        for (String serviceName : loginSettingsMap.keySet())
		{
            buf.append("<gateway>");
            String autoLogin = loginSettingsMap.get(serviceName);
            buf.append("<serviceName>").append(serviceName).append("</serviceName>");
            buf.append("<autoLogin>").append(autoLogin).append("</autoLogin>");
            buf.append("</gateway>");
        }
        buf.append("</gateways>");
        buf.append("</").append(getElementName()).append(">");
        return buf.toString();
    }

    public static class ConferencePrivateDataProvider implements PrivateDataProvider
	{

        public ConferencePrivateDataProvider()
		{
        }

        public PrivateData parsePrivateData(XmlPullParser parser) throws Exception
		{
            GatewayPrivateData data = new GatewayPrivateData();
            boolean done = false;
            boolean isInstalled = false;

            while (!done)
			{
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG 
					&& parser.getName().equals("gateways"))
                    isInstalled = true;

                if (eventType == XmlPullParser.START_TAG 
					&& parser.getName().equals("gateway"))
				{
                    boolean gatewayDone = false;
                    String serviceName = null;
                    String autoLogin = null;
                    while (!gatewayDone)
					{
                        int eType = parser.next();
                        if (eType == XmlPullParser.START_TAG 
							&& parser.getName().equals("serviceName"))
						{
                            serviceName = parser.nextText();
                        }
                        else if (eType == XmlPullParser.START_TAG 
							&& parser.getName().equals("autoLogin"))
						{
                            autoLogin = parser.nextText();
                        }
                        else if (eType == XmlPullParser.END_TAG 
							&& parser.getName().equals("gateway"))
						{
                            data.addService(serviceName, Boolean.parseBoolean(autoLogin));
                            gatewayDone = true;
                        }
                    }
                }
                else if (eventType == XmlPullParser.END_TAG 
					&& parser.getName().equals("gateways"))
				{
                    done = true;
                }
                else if (!isInstalled)
				{
                    done = true;
                }
            }
            return data;
        }
		
    }

}
