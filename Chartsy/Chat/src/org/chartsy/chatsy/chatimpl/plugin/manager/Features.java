package org.chartsy.chatsy.chatimpl.plugin.manager;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;
import java.util.List;

public class Features implements PacketExtension
{

    private List<String> availableFeatures = new ArrayList<String>();

    public List<String> getAvailableFeatures()
	{
        return availableFeatures;
    }

    public void addFeature(String feature)
	{
        availableFeatures.add(feature);
    }

    public static final String ELEMENT_NAME = "event";
    public static final String NAMESPACE = "http://jabber.org/protocol/disco#info";

    public String getElementName()
	{
        return ELEMENT_NAME;
    }

    public String getNamespace()
	{
        return NAMESPACE;
    }

    public String toXML()
	{
        StringBuilder buf = new StringBuilder();
        buf.append("<event xmlns=\"" + NAMESPACE + "\"").append("</event>");
        return buf.toString();
    }

    public static class Provider implements PacketExtensionProvider
	{

        public PacketExtension parseExtension(XmlPullParser parser) throws Exception
		{
            Features features = new Features();
            boolean done = false;
            while (!done)
			{
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG 
					&& "event".equals(parser.getName()))
				{
                    parser.nextText();
                }
                if (eventType == XmlPullParser.START_TAG 
					&& "feature".equals(parser.getName()))
				{
                    String feature = parser.getAttributeValue("", "var");
                    features.addFeature(feature);
                }
                else if (eventType == XmlPullParser.END_TAG)
				{
                    if ("event".equals(parser.getName()))
                        done = true;
                }
            }

            return features;
        }
		
    }
	
}
