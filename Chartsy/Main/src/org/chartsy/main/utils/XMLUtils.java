package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.chartsy.main.chartsy.chart.Annotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author viorel.gheba
 */
public final class XMLUtils {

    private XMLUtils() {}

    public static void createXMLDocument(String path) { createXMLDocument(new File(path)); }
    public static void createXMLDocument(File file) {
        if (!file.exists()) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.newDocument();
                Element root = document.createElement("root");
                document.appendChild(root);
                saveXMLDocument(document, file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void emptyXMLDocument(String path) { emptyXMLDocument(new File(path)); }
    public static void emptyXMLDocument(File file) {
        if (file.exists()) {
            Document document = loadXMLDocument(file);
            if (document != null) {
                Element root = getRoot(document);
                NodeList nodeList = root.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) root.removeChild(nodeList.item(i));
                saveXMLDocument(document, file);
            }
        }
    }

    public static void saveXMLDocument(Document document, String path) { saveXMLDocument(document, new File(path)); }
    public static void saveXMLDocument(Document document, File file) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            Source source = new DOMSource(document);
            Result result = new StreamResult(output);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            ex.printStackTrace();
            try { output.close(); }
            catch (IOException io) { io.printStackTrace(); }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            try { output.close(); }
            catch (IOException io) { io.printStackTrace(); }
        } finally {
            try { output.close(); }
            catch (IOException io) { io.printStackTrace(); }
        }
    }

    public static Document loadXMLDocument(String path) { return loadXMLDocument(new File(path)); }
    public static Document loadXMLDocument(File file) {
        try {
            if (file.exists()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);
                return document;
            } else {
                return null;
            }
        } catch (SAXException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Element getRoot(String path) { return getRoot(new File(path)); }
    public static Element getRoot(File file) { return getRoot(loadXMLDocument(file)); }
    public static Element getRoot(Document document) {
        if (document == null) throw new IllegalArgumentException("Null document");
        NodeList nodeList = document.getElementsByTagName("root");
        return nodeList.item(0) != null ? (Element) nodeList.item(0) : null;
    }

    public static void removeElement(Element element, String path) { removeElement(loadXMLDocument(path), element, new File(path)); }
    public static void removeElement(Document document, Element element, String path) { removeElement(document, element, new File(path)); }
    public static void removeElement(Document document, Element element, File file) {
        if (document == null) throw new IllegalArgumentException("Null document");
        if (element == null) throw new IllegalArgumentException("Null element");
        if (file == null) throw new IllegalArgumentException("Null file");
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            element.removeChild(node);
        }
        Node parent = element.getParentNode();
        parent.removeChild((Node) element);
        saveXMLDocument(document, file);
    }

    public static Element setStringParam(Element element, String value) { element.setAttribute("stringvalue", value); return element; }
    public static Element setIntegerParam(Element element, Integer value) { element.setAttribute("integervalue", Integer.toString(value)); return element; }
    public static Element setDoubleParam(Element element, Double value) { element.setAttribute("doublevalue", Double.toString(value)); return element; }
    public static Element setLongParam(Element element, Long value) { element.setAttribute("longvalue", Long.toString(value)); return element; }
    public static Element setBooleanParam(Element element, Boolean value) { element.setAttribute("booleanvalue", Boolean.toString(value)); return element; }
    public static Element setStrokeParam(Element element, Stroke stroke) { element.setAttribute("stringvalue", Integer.toString(StrokeGenerator.getStrokeIndex(stroke))); return element; }
    public static Element setColorParam(Element element, Color value) {
        element.setAttribute("redvalue", Integer.toString(value.getRed()));
        element.setAttribute("greenvalue", Integer.toString(value.getGreen()));
        element.setAttribute("bluevalue", Integer.toString(value.getBlue()));
        element.setAttribute("alphavalue", Integer.toString(value.getAlpha()));
        return element;
    }
    public static Element setFontParam(Element element, Font value) {
        element.setAttribute("stringvalue", value.getName());
        element.setAttribute("stylevalue", Integer.toString(value.getStyle()));
        element.setAttribute("sizevalue", Integer.toString(value.getSize()));
        return element;
    }
    public static Element setRectangleParam(Element element, Rectangle value) {
        element.setAttribute("x", Integer.toString((int) value.getMinX()));
        element.setAttribute("y", Integer.toString((int) value.getMinY()));
        element.setAttribute("width", Integer.toString((int) value.getWidth()));
        element.setAttribute("height", Integer.toString((int) value.getHeight()));
        return element;
    }
    public static Element setBoundsParam(Element element, Rectangle2D.Double value) {
        element.setAttribute("x", Double.toString(value.getMinX()));
        element.setAttribute("y", Double.toString(value.getMinY()));
        element.setAttribute("width", Double.toString(value.getWidth()));
        element.setAttribute("height", Double.toString(value.getHeight()));
        return element;
    }
    public static Element setAnnotationParam(Element element, Annotation value) {
        element.setAttribute("t1", Long.toString(value.getT1()));
        element.setAttribute("t2", Long.toString(value.getT2()));
        element.setAttribute("v1", Double.toString(value.getV1()));
        element.setAttribute("v2", Double.toString(value.getV2()));
        return element;
    }
    public static Element setPropertiesParam(Element element, PropertyItem value) {
        element.setAttribute("name", value.getName());
        element.setAttribute("component", value.getComponent());
        element.setAttribute("list", value.getList() != null ? listToString(value.getList()) : "null");
        Object obj = value.getValue();
        if (obj instanceof String) {
            String string = (String) obj;
            element.setAttribute("stringvalue", string);
        } else if (obj instanceof Boolean) {
            Boolean bool = (Boolean) obj;
            element.setAttribute("booleanvalue", Boolean.toString(bool));
        } else if (obj instanceof Color) {
            Color color = (Color) obj;
            element.setAttribute("redvalue", Integer.toString(color.getRed()));
            element.setAttribute("greenvalue", Integer.toString(color.getGreen()));
            element.setAttribute("bluevalue", Integer.toString(color.getBlue()));
            element.setAttribute("alphavalue", Integer.toString(color.getAlpha()));
        }
        return element;
    }

    public static String getStringParam(Element parent, String name) {
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        return element != null ? element.getAttribute("stringvalue") : "";
    }
    public static Integer getIntegerParam(Element parent, String name) {
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        return element != null ? Integer.parseInt(element.getAttribute("integervalue")) : 0;
    }
    public static Double getDoubleParam(Element parent, String name) {
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        return element != null ? Double.parseDouble(element.getAttribute("doublevalue")) : 0;
    }
    public static Long getLongParam(Element parent, String name) {
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        return element != null ? Long.parseLong(element.getAttribute("longvalue")) : 0;
    }
    public static Boolean getBooleanParam(Element parent, String name) {
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        return element != null ? Boolean.parseBoolean(element.getAttribute("booleanvalue")) : false;
    }
    public static Stroke getStrokeParam(Element parent, String name) { 
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        return element != null ? StrokeGenerator.getStroke(Integer.parseInt(element.getAttribute("stringvalue"))) : StrokeGenerator.DEFAULT_STROKE;
    }
    public static Color getColorParam(Element parent, String name) {
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        return element == null ? Color.BLACK :
            new Color(Integer.parseInt(element.getAttribute("redvalue")),
            Integer.parseInt(element.getAttribute("greenvalue")),
            Integer.parseInt(element.getAttribute("bluevalue")),
            Integer.parseInt(element.getAttribute("alphavalue")));
    }
    public static Font getFontParam(Element parent, String name) {
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        return element == null ? DefaultTheme.FONT :
            new Font(element.getAttribute("stringvalue"),
            Integer.parseInt(element.getAttribute("stylevalue")),
            Integer.parseInt(element.getAttribute("sizevalue")));
    }
    public static Rectangle getRectangleParam(Element parent, String name) {
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        return new Rectangle(Integer.parseInt(element.getAttribute("x")),
                Integer.parseInt(element.getAttribute("y")),
                Integer.parseInt(element.getAttribute("width")),
                Integer.parseInt(element.getAttribute("height")));
    }
    public static Rectangle2D.Double getBoundsParam(Element parent, String name) {
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        return new Rectangle2D.Double(Double.parseDouble(element.getAttribute("x")),
                Double.parseDouble(element.getAttribute("y")),
                Double.parseDouble(element.getAttribute("width")),
                Double.parseDouble(element.getAttribute("height")));
    }
    public static Element getAnnotationsParam(Element parent, String name) { return (Element) parent.getElementsByTagName(name).item(0); }
    public static Properties getPropertiesParam(Element parent, String name) {
        Element element = (Element) parent.getElementsByTagName(name).item(0);
        NodeList nodeList = element.getElementsByTagName("propertyItem");
        PropertyItem[] data = new PropertyItem[nodeList.getLength()];
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element e = (Element) nodeList.item(i);
            String n = e.getAttribute("name");
            String c = e.getAttribute("component");
            String[] l = e.getAttribute("list").equals("null") ? null : stringToList(e.getAttribute("list"));
            Object v = null;
            if (c.equals(ComponentGenerator.JTEXTFIELD) || c.equals(ComponentGenerator.JCOMBOBOX) || c.equals(ComponentGenerator.JSLIDER) || c.equals(ComponentGenerator.JSTROKECOMBOBOX)) {
                String string = e.getAttribute("stringvalue");
                data[i] = new PropertyItem(n, c, l, string);
            } else if (c.equals(ComponentGenerator.JLABEL)) {
                Color color = new Color(Integer.parseInt(e.getAttribute("redvalue")),
                        Integer.parseInt(e.getAttribute("greenvalue")),
                        Integer.parseInt(e.getAttribute("bluevalue")),
                        Integer.parseInt(e.getAttribute("alphavalue")));
                data[i] = new PropertyItem(n, c, l, color);
            } else if (c.equals(ComponentGenerator.JCHECKBOX)) {
                boolean bool = Boolean.parseBoolean(e.getAttribute("booleanvalue"));
                data[i] = new PropertyItem(n, c, l, bool);
            }
        }
        return new Properties(data);
    }

    public static void setActiveDataProvider(String name) {
        String path = FileUtils.DataProvider();
        Document document = loadXMLDocument(path);

        if (document != null) {
            Element root = getRoot(document);
            if (root.getChildNodes().getLength() == 0) {
                Element element = document.createElement("active");
                element.setAttribute("stringvalue", name);
                root.appendChild(element);
            } else {
                Element element = (Element) root.getElementsByTagName("active").item(0);
                element.setAttribute("stringvalue", name);
            }
        }

        saveXMLDocument(document, path);
    }

    public static String getActiveDataProvider() {
        String path = FileUtils.DataProvider();
        Document document = loadXMLDocument(path);

        if (document != null) {
            Element root = getRoot(document);
            return root.getChildNodes().getLength() == 0 ? null : ((Element) root.getElementsByTagName("active").item(0)).getAttribute("stringvalue");
        }

        return null;
    }

    public static String listToString(String[] list) {
        String s = "";
        for (int i = 0; i < list.length; i++) s += list[i] + ",";
        s = s.substring(0, s.length() - 1);
        return s;
    }

    public static String[] stringToList(String string) {
        string = string.replace(" ", "");
        return string.split(",");
    }

    public interface ToXML {
        public void readXMLDocument(Element parent);
        public void writeXMLDocument(Document document, Element parent);
    }

}
