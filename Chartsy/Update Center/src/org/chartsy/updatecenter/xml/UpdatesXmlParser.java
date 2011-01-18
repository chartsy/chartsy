package org.chartsy.updatecenter.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Viorel
 */
public class UpdatesXmlParser
{

    private UpdatesXmlParser()
    {}

    public static Downloads getDownloads(String stream)
    {
        Downloads downloads = null;

        try
        {
            System.out.println(stream);
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(stream));
            
            UpdatesXmlHandler handler = new UpdatesXmlHandler();

            XMLReader reader = XMLReaderFactory.createXMLReader(
                    "com.sun.org.apache.xerces.internal.parsers.SAXParser");
            if (reader != null)
            {
                reader.setContentHandler(handler);
                reader.parse(src);
                downloads = handler.getDownloads();
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(UpdatesXmlParser.class.getName()).log(
                    Level.SEVERE, "", ex);
        }
        catch (SAXException ex)
        {
            Logger.getLogger(UpdatesXmlParser.class.getName()).log(
                    Level.SEVERE, "", ex);
        }

        return downloads;
    }

}
