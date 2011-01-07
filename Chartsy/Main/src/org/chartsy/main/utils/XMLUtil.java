package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.chart.Overlay;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 *
 * @author Viorel
 */
public final class XMLUtil
{

	private XMLUtil() {}

	public static void createXMLDocument(String path) 
	{ createXMLDocument(path, "root"); }

	public static void createXMLDocument(String path, String root)
	{ createXMLDocument(new File(path), root); }

    public static void createXMLDocument(File file)
	{ createXMLDocument(file, "root"); }

	public static void createXMLDocument(File file, String rootName)
	{
		if (!file.exists())
		{
            try
			{
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.newDocument();
                Element root = document.createElement(rootName);
                document.appendChild(root);
                saveXMLDocument(document, file);
            }
			catch (Exception ex)
			{}
        }
	}

	public static void emptyXMLDocument(String path) 
	{ emptyXMLDocument(new File(path)); }
	
    public static void emptyXMLDocument(File file)
	{
        if (file.exists())
		{
            Document document = loadXMLDocument(file);
            if (document != null)
			{
                Element root = getRoot(document);
                NodeList nodeList = root.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++)
					root.removeChild(nodeList.item(i));
                saveXMLDocument(document, file);
            }
        }
    }

	public static void saveXMLDocument(Document document, String path) 
	{ saveXMLDocument(document, new File(path)); }
	
    public static void saveXMLDocument(Document document, File file)
	{
        FileOutputStream output = null;
        try
		{
            output = new FileOutputStream(file);
            Source source = new DOMSource(document);
            Result result = new StreamResult(output);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
        } 
		catch (Exception ex)
		{
            try
			{ output.close(); }
            catch (IOException io)
			{}
        } 
		finally
		{
            try
			{ output.close(); }
            catch (IOException io)
			{}
        }
    }

	public static Document loadXMLDocument(String path) 
	{ return loadXMLDocument(new File(path)); }
	
    public static Document loadXMLDocument(File file)
	{
        try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			return document;
        } 
		catch (Exception ex)
		{
            return null;
        }
    }

	public static Element getRoot(String path) 
	{ return getRoot(new File(path)); }
	
    public static Element getRoot(File file) 
	{ return getRoot(loadXMLDocument(file)); }

    public static Element getRoot(Document document) 
	{ return getRoot(document, "root"); }
	
	public static Element getRoot(Document document, String root)
	{
		if (document == null)
			throw new IllegalArgumentException("Null document");

        NodeList nodeList = document.getElementsByTagName(root);
        return nodeList.item(0) != null ? (Element) nodeList.item(0) : null;
	}

	public static Element addTemplateNode
		(Document document, Element parent, String name)
	{
		Element element = document.createElement(TEMPLATE_NODE);
		parent.appendChild(element);
		element.setAttribute(NAME_ATTR, name);
		return element;
	}

	public static Element addChartNode
		(Document document, Element parent, Chart chart)
	{
		Element element = document.createElement(CHART_NODE);
		parent.appendChild(element);
		element.setAttribute(NAME_ATTR, chart.getName());
		return element;
	}

	public static Element addOverlaysNode
		(Document document, Element parent)
	{
		Element element = document.createElement(OVERLAYS_NODE);
		parent.appendChild(element);
		return element;
	}

	public static Element addOverlayNode
		(Document document, Element parent, Overlay overlay)
	{
		Element element = document.createElement(OVERLAY_NODE);
		parent.appendChild(element);
		element.setAttribute(NAME_ATTR, overlay.getName());
		return element;
	}

	public static Element addIndicatorsNode
		(Document document, Element parent)
	{
		Element element = document.createElement(INDICATORS_NODE);
		parent.appendChild(element);
		return element;
	}

	public static Element addIndicatorNode
		(Document document, Element parent, Indicator indicator)
	{
		Element element = document.createElement(INDICATOR_NODE);
		parent.appendChild(element);
		element.setAttribute(NAME_ATTR, indicator.getName());
		return element;
	}

	public static Element addPropertiesNode
		(Document document, Element parent)
	{
		Element element = document.createElement(PROPERTIES_NODE);
		parent.appendChild(element);
		return element;
	}

	private static Element addPropertyNode
		(Document document, Element parent, String name)
	{
		Element element = document.createElement(PROPERTY_NODE);
		parent.appendChild(element);
		element.setAttribute(NAME_ATTR, name);
		return element;
	}

	public static void addProperty
		(Document document, Element parent, String name, Object value)
	{
		if (value instanceof String)
			addStringProperty(document, parent, name, (String)value);
		else if (value instanceof Integer)
			addIntegerProperty(document, parent, name, (Integer)value);
		else if (value instanceof Double)
			addDoubleProperty(document, parent, name, (Double)value);
		else if (value instanceof Float)
			addFloatProperty(document, parent, name, (Float)value);
		else if (value instanceof Boolean)
			addBooleanProperty(document, parent, name, (Boolean)value);
		else if (value instanceof Color)
			addColorProperty(document, parent, name, (Color)value);
		else if (value instanceof Font)
			addFontProperty(document, parent, name, (Font)value);
		else
			return;
	}

	public static Element getPropertyNode
		(Element properties, String name)
	{
		NodeList nodeList = properties.getElementsByTagName(PROPERTY_NODE);
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Element element = (Element) nodeList.item(i);
			if (name.equals(element.getAttributes().getNamedItem(NAME_ATTR)
				.getNodeValue()))
				return element;
		}
		return null;
	}

	public static boolean elementExists(Element properties, String name)
	{
		NodeList nodeList = properties.getElementsByTagName(PROPERTY_NODE);
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Element element = (Element) nodeList.item(i);
			if (name.equals(element.getAttributes().getNamedItem(NAME_ATTR)
				.getNodeValue()))
				return true;
		}
		return false;
	}

	public static void addStringProperty
		(Document document, Element parent, String name, String value)
	{
		addPropertyNode(document, parent, name)
			.setAttribute(STRING_ATTR, value);
	}

	public static String getStringProperty
		(Element properties, String name)
	{
		return getPropertyNode(properties, name).getAttributes()
			.getNamedItem(STRING_ATTR).getNodeValue();
	}

	public static void addIntegerProperty
		(Document document, Element parent, String name, int value)
	{
		addPropertyNode(document, parent, name)
			.setAttribute(INTEGER_ATTR, Integer.toString(value));
	}

	public static int getIntegerProperty
		(Element properties, String name)
	{
		return new Integer(getPropertyNode(properties, name).getAttributes()
			.getNamedItem(INTEGER_ATTR).getNodeValue());
	}

	public static void addDoubleProperty
		(Document document, Element parent, String name, double value)
	{
		addPropertyNode(document, parent, name)
			.setAttribute(DOUBLE_ATTR, Double.toString(value));
	}

	public static double getDoubleProperty
		(Element properties, String name)
	{
		return new Double(getPropertyNode(properties, name).getAttributes()
			.getNamedItem(DOUBLE_ATTR).getNodeValue());
	}

	public static void addFloatProperty
		(Document document, Element parent, String name, float value)
	{
		addPropertyNode(document, parent, name)
			.setAttribute(FLOAT_ATTR, Float.toString(value));
	}

	public static float getFloatProperty
		(Element properties, String name)
	{
		return new Float(getPropertyNode(properties, name).getAttributes()
			.getNamedItem(FLOAT_ATTR).getNodeValue());
	}

	public static void addBooleanProperty
		(Document document, Element parent, String name, boolean value)
	{
		addPropertyNode(document, parent, name)
			.setAttribute(BOOLEAN_ATTR, Boolean.toString(value));
	}

	public static boolean getBooleanProperty
		(Element properties, String name)
	{
		return Boolean.parseBoolean(getPropertyNode(properties, name).getAttributes()
			.getNamedItem(BOOLEAN_ATTR).getNodeValue());
	}

	public static void addColorProperty
		(Document document, Element parent, String name, Color value)
	{
		addPropertyNode(document, parent, name)
			.setAttribute(COLOR_ATTR, value.getRed()+","+value.getGreen()+","+value.getBlue());
	}

	public static Color getColorProperty
		(Element properties, String name)
	{
		String[] rgb = getPropertyNode(properties, name).getAttributes()
			.getNamedItem(COLOR_ATTR).getNodeValue().split(",");
		return new Color(
				new Integer(rgb[0]),
				new Integer(rgb[1]),
				new Integer(rgb[2]));
	}

	public static void addFontProperty
		(Document document, Element parent, String name, Font value)
	{
		addPropertyNode(document, parent, name)
			.setAttribute(FONT_ATTR, value.getName()+","+value.getStyle()+","+value.getSize());
	}

	public static Font getFontProperty
		(Element properties, String name)
	{
		String[] font = getPropertyNode(properties, name).getAttributes()
			.getNamedItem(FONT_ATTR).getNodeValue().split(",");
		return new Font(font[0], new Integer(font[1]), new Integer(font[2]));
	}

	public static Object getPropertyValue(Element element)
	{
		NamedNodeMap map = element.getAttributes();
		map.removeNamedItem(NAME_ATTR);

		if (map.item(0).getNodeName().equals(STRING_ATTR))
			return map.item(0).getNodeValue();
		else if (map.item(0).getNodeName().equals(INTEGER_ATTR))
			return new Integer(map.item(0).getNodeValue());
		else if (map.item(0).getNodeName().equals(DOUBLE_ATTR))
			return new Double(map.item(0).getNodeValue());
		else if (map.item(0).getNodeName().equals(FLOAT_ATTR))
			return new Float(map.item(0).getNodeValue());
		else if (map.item(0).getNodeName().equals(BOOLEAN_ATTR))
			return Boolean.valueOf(map.item(0).getNodeValue());
		else if (map.item(0).getNodeName().equals(COLOR_ATTR))
		{
			String[] rgb = map.item(0).getNodeValue().split(",");
			return new Color(
				new Integer(rgb[0]),
				new Integer(rgb[1]),
				new Integer(rgb[2]));
		}
		else if (map.item(0).getNodeName().equals(FONT_ATTR))
		{
			String[] font = map.item(0).getNodeValue().split(",");
			return new Font(font[0], new Integer(font[1]), new Integer(font[2]));
		}
		else
		{
			return null;
		}
	}

	public static String getNameAttr(Element element)
	{
		return element.getAttribute(NAME_ATTR);
	}

	public static Element getChartNode(Element parent)
	{
		return (Element)parent.getElementsByTagName(CHART_NODE).item(0);
	}

	public static Element getOverlaysNode(Element parent)
	{
		return (Element)parent.getElementsByTagName(OVERLAYS_NODE).item(0);
	}

	public static Element getIndicatorsNode(Element parent)
	{
		return (Element)parent.getElementsByTagName(INDICATORS_NODE).item(0);
	}

	public static Element getPropertiesNode(Element parent)
	{
		return (Element)parent.getElementsByTagName(PROPERTIES_NODE).item(0);
	}

	public static String getOnlyNumerics(String str)
	{
		if (str == null)
			return null;
		StringBuilder strBuff = new StringBuilder();
		char c;
		for (int i = 0; i < str.length() ; i++) {
			c = str.charAt(i);
			if (Character.isDigit(c))
				strBuff.append(c);
		}
		return strBuff.toString();
	}

	public interface XMLTemplate
	{
		public void saveToTemplate(Document document, Element element);
		public void loadFromTemplate(Element element);
	}

	public static final String TEMPLATES_NODE		= "templates";
	public static final String TEMPLATE_NODE		= "template";
	public static final String CHART_NODE			= "chart";
	public static final String PROPERTIES_NODE		= "properties";
	public static final String PROPERTY_NODE		= "property";
	public static final String OVERLAYS_NODE		= "overlays";
	public static final String OVERLAY_NODE			= "overlay";
	public static final String INDICATORS_NODE		= "indicators";
	public static final String INDICATOR_NODE		= "indicator";

	public static final String NAME_ATTR			= "name";
	public static final String STRING_ATTR			= "stringvalue";
	public static final String INTEGER_ATTR			= "intvalue";
	public static final String DOUBLE_ATTR			= "doublevalue";
	public static final String FLOAT_ATTR			= "floatvalue";
	public static final String BOOLEAN_ATTR			= "booleanvalue";
	public static final String COLOR_ATTR			= "colorvalue";
	public static final String FONT_ATTR			= "fontvalue";

}
