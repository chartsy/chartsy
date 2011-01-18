package org.chartsy.updatecenter.xml;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Viorel
 */
public class UpdatesXmlHandler extends DefaultHandler
{

    private static final int DOWNLOADS		= "downloads".hashCode();
    private static final int VERSION		= "version".hashCode();
    private static final int NBVERSION		= "nbversion".hashCode();
    private static final int FEATURES		= "features".hashCode();
    private static final int INSTALLER		= "installer".hashCode();
    private static final int FILENAME		= "filename".hashCode();
    private static final int URL                = "url".hashCode();
    private static final int OS                 = "os".hashCode();

    private Stack stack;
    private boolean isStackReadyForText;
    private Downloads downloads = null;

    public UpdatesXmlHandler()
    {
        stack = new Stack();
        isStackReadyForText = false;
    }

    public Downloads getDownloads()
    {
        return downloads;
    }

    public @Override void startElement
            (String uri, String localName, String qName, Attributes attribs)
    {
            isStackReadyForText = false;
            int identifier = localName.hashCode();

            if (identifier == DOWNLOADS)
            {
                stack.push(new Downloads());
            }
            else if(identifier == INSTALLER)
                stack.push(new Installer());
            else if(identifier == VERSION
                    || identifier == NBVERSION
                    || identifier == FEATURES
                    || identifier == FILENAME
                    || identifier == URL
                    || identifier == OS)
            {
                stack.push(new StringBuilder());
                isStackReadyForText = true;
            }
            else
            {
                // do nothing
            }
    }

    public @Override void endElement
            (String uri, String localName, String qName)
    {
            isStackReadyForText = false;

            Object tmp = stack.pop();
            int identifier = localName.hashCode();

            if (identifier == DOWNLOADS)
                 downloads = (Downloads)tmp;
            else if(identifier == INSTALLER)
            {
                ((Downloads)stack.peek()).addInstaller((Installer)tmp);
            }
            else if (identifier == VERSION)
                ((Downloads)stack.peek()).setVersion(tmp.toString());
            else if (identifier == NBVERSION)
                ((Downloads)stack.peek()).setNbVersion(tmp.toString());
            else if (identifier == FEATURES)
                ((Downloads)stack.peek()).setFeatures(tmp.toString());
            else if (identifier == FILENAME)
                ((Installer)stack.peek()).setFilename(tmp.toString());
            else if (identifier == URL)
                ((Installer)stack.peek()).setUrl(tmp.toString());
            else if (identifier == OS)
                ((Installer)stack.peek()).setOS(tmp.toString());
            else
                stack.push(tmp);
    }

    public @Override void characters
            (char[] data, int start, int length)
    {
            if (isStackReadyForText == true)
            {
                ((StringBuilder)stack.peek()).append(data, start, length);
            }
    }

}
