package org.chartsy.main.managers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Indicator;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.templates.Template;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.main.utils.XMLUtil;
import org.openide.filesystems.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Viorel
 */
public class TemplateManager 
{

    private static TemplateManager instance;
    private HashMap<String, Template> templates;
    private String defTemp;
    private File defaultTemplate;
    private File templatesXML;

    public static TemplateManager getDefault()
    {
        if (instance == null)
            instance = new TemplateManager();
        return instance;
    }

    private TemplateManager()
    {
        defaultTemplate = FileUtils.templatesFile("default.xml");
        templatesXML = FileUtils.templatesFile("templates.xml");
        templates = new HashMap<String, Template>();
        if (!templatesXML.exists())
        {
            try
            {
                FileUtil.copy(
                    Template.class.getResourceAsStream("default.xml"),
                    FileUtil.createData(defaultTemplate).getOutputStream());
                FileUtil.copy(
                    Template.class.getResourceAsStream("templates.xml"),
                    FileUtil.createData(templatesXML).getOutputStream());
            }
            catch (Exception ex)
            {
                XMLUtil.createXMLDocument(defaultTemplate);
                XMLUtil.createXMLDocument(templatesXML, XMLUtil.TEMPLATES_NODE);
            }
            initTemplates();
        }
        else
            initTemplates();
    }

    public String getDefaultTemplate()
    {
        return defTemp;
    }

    public void setDefaultTemplate(String name)
    {
        defTemp = name;
        Document document = XMLUtil.loadXMLDocument(defaultTemplate);
        Element root = XMLUtil.getRoot(document);
        root.setTextContent(name);
        XMLUtil.saveXMLDocument(document, defaultTemplate);
    }

    public Object[] getTemplateNames()
    {
        return templates.keySet().toArray();
    }

    public Template getTemplate(Object key)
    {
        return templates.get(key);
    }

    public boolean templateExists(String name)
    {
        return templates.containsKey(name);
    }

    private void initTemplates()
    {
        Document document = XMLUtil.loadXMLDocument(defaultTemplate);
        Element temp = XMLUtil.getRoot(document);
        defTemp = temp.getTextContent();

        document = XMLUtil.loadXMLDocument(templatesXML);
        Element root = XMLUtil.getRoot(document, XMLUtil.TEMPLATES_NODE);

        NodeList nodeList = root.getElementsByTagName(XMLUtil.TEMPLATE_NODE);
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element element = (Element) nodeList.item(i);
            Template template = new Template(XMLUtil.getNameAttr(element));

            Element chart = XMLUtil.getChartNode(element);
            String chartName = XMLUtil.getNameAttr(chart);
            template.setChart(ChartManager.getDefault().getChart(chartName));
            Element chartProperties = XMLUtil.getPropertiesNode(chart);
            template.getChartProperties().loadFromTemplate(chartProperties);

            Element overlays = XMLUtil.getOverlaysNode(element);
            NodeList overlaysList = overlays.getElementsByTagName(XMLUtil.OVERLAY_NODE);
            for (int j = 0; j < overlaysList.getLength(); j++)
            {
                Element overlayNode = (Element) overlaysList.item(j);
                String overlayName = XMLUtil.getNameAttr(overlayNode);
                Overlay overlay = OverlayManager.getDefault().getOverlay(overlayName).newInstance();
                if (overlay != null)
                {
                    Element overlayProperties = XMLUtil.getPropertiesNode(overlayNode);
                    overlay.loadFromTemplate(overlayProperties);
                    template.addOverlay(overlay);
                }
            }

            Element indicators = XMLUtil.getIndicatorsNode(element);
            NodeList indicatorsList = indicators.getElementsByTagName(XMLUtil.INDICATOR_NODE);
            for (int j = 0; j < indicatorsList.getLength(); j++)
            {
                Element indicatorNode = (Element) indicatorsList.item(j);
                String indicatorName = XMLUtil.getNameAttr(indicatorNode);
                Indicator indicator = IndicatorManager.getDefault().getIndicator(indicatorName).newInstance();
                if (indicator != null)
                {
                    Element indicatorProperties = XMLUtil.getPropertiesNode(indicatorNode);
                    indicator.loadFromTemplate(indicatorProperties);
                    template.addIndicator(indicator);
                }
            }

            templates.put(template.getName(), template);
        }
    }

    public void saveToTemplate(String name, ChartFrame chartFrame)
    {
        Document document = XMLUtil.loadXMLDocument(templatesXML);
        Element root = XMLUtil.getRoot(document, XMLUtil.TEMPLATES_NODE);

        // create the template node
        Element template = XMLUtil.addTemplateNode(document, root, name);
        // save template details
        Element chart = XMLUtil.addChartNode(document, template, chartFrame.getChartData().getChart());
        Element chartProperties = XMLUtil.addPropertiesNode(document, chart);
        chartFrame.getChartProperties().saveToTemplate(document, chartProperties);

        List<Overlay> overlays = chartFrame.getMainPanel().getSplitPanel().getChartPanel().getOverlays();
        if (!overlays.isEmpty())
        {
            Element overlaysNode = XMLUtil.addOverlaysNode(document, template);
            for (Overlay overlay : overlays)
            {
                Element overlayNode = XMLUtil.addOverlayNode(document, overlaysNode, overlay);
                Element overlayProperties = XMLUtil.addPropertiesNode(document, overlayNode);
                overlay.saveToTemplate(document, overlayProperties);
            }
        }

        List<Indicator> indicators = chartFrame.getMainPanel().getSplitPanel().getIndicatorsPanel().getIndicatorsList();
        if (!indicators.isEmpty())
        {
            Element indicatorsNode = XMLUtil.addIndicatorsNode(document, template);
            for (Indicator indicator : indicators)
            {
                Element indicatorNode = XMLUtil.addIndicatorNode(document, indicatorsNode, indicator);
                Element indicatorProperties = XMLUtil.addPropertiesNode(document, indicatorNode);
                indicator.saveToTemplate(document, indicatorProperties);
            }
        }

        // save changes
        XMLUtil.saveXMLDocument(document, templatesXML);

        templates.clear();
        initTemplates();
    }

    public void removeTemplate(String name)
    {
        Document document = XMLUtil.loadXMLDocument(templatesXML);
        Element root = XMLUtil.getRoot(document, XMLUtil.TEMPLATES_NODE);
        NodeList nodeList = root.getElementsByTagName(XMLUtil.TEMPLATE_NODE);
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);
            node.getAttributes().getNamedItem(XMLUtil.NAME_ATTR).getNodeValue();
            if (node.getAttributes().getNamedItem(XMLUtil.NAME_ATTR).getNodeValue().equals(name))
            {
                root.removeChild(node);
                break;
            }
        }
        XMLUtil.saveXMLDocument(document, templatesXML);

        templates.clear();
        initTemplates();
    }

    public Overlay getOverlay(int index)
    {
        Overlay overlay = null;
        Document document = XMLUtil.loadXMLDocument(templatesXML);
        Element root = XMLUtil.getRoot(document, XMLUtil.TEMPLATES_NODE);

        NodeList nodeList = root.getElementsByTagName(XMLUtil.TEMPLATE_NODE);
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element element = (Element) nodeList.item(i);
            if (element.getAttribute(XMLUtil.NAME_ATTR).equals(defTemp))
            {
                Element overlays = XMLUtil.getOverlaysNode(element);
                NodeList overlaysList = overlays.getElementsByTagName(XMLUtil.OVERLAY_NODE);
                Element overlayNode = (Element) overlaysList.item(index);
                Element overlayProperties = XMLUtil.getPropertiesNode(overlayNode);
                overlay = OverlayManager.getDefault().getOverlay(
                    XMLUtil.getNameAttr(overlayNode)).newInstance();
                overlay.loadFromTemplate(overlayProperties);
            }
        }

        return overlay;
    }

    public Indicator getIndicator(int index)
    {
        Indicator indicator = null;
        Document document = XMLUtil.loadXMLDocument(templatesXML);
        Element root = XMLUtil.getRoot(document, XMLUtil.TEMPLATES_NODE);

        NodeList nodeList = root.getElementsByTagName(XMLUtil.TEMPLATE_NODE);
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element element = (Element) nodeList.item(i);
            if (element.getAttribute(XMLUtil.NAME_ATTR).equals(defTemp))
            {
                Element indicators = XMLUtil.getIndicatorsNode(element);
                NodeList indicatorsList = indicators.getElementsByTagName(XMLUtil.INDICATOR_NODE);
                Element indicatorNode = (Element) indicatorsList.item(index);
                Element indicatorProperties = XMLUtil.getPropertiesNode(indicatorNode);
                indicator = IndicatorManager.getDefault().getIndicator(
                    XMLUtil.getNameAttr(indicatorNode)).newInstance();
                indicator.loadFromTemplate(indicatorProperties);
            }
        }

        return indicator;
    }

}
