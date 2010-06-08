package org.chartsy.annotation.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.beans.PropertyEditorSupport;
import java.util.logging.Level;
import org.chartsy.main.chart.AbstractPropertiesNode;
import org.chartsy.main.utils.AlphaPropertyEditor;
import org.chartsy.main.utils.SerialVersion;
import org.chartsy.main.utils.StrokeGenerator;
import org.chartsy.main.utils.StrokePropertyEditor;
import org.openide.nodes.Sheet;

/**
 *
 * @author Viorel
 */
public class AnnotationNode
        extends AbstractPropertiesNode
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    public AnnotationNode()
    {
        super("Text Properties");
    }

    public AnnotationNode(AnnotationProperties annotationProperties)
    {
        super("Text Properties", annotationProperties);
    }

    @SuppressWarnings("unchecked")
    protected @Override Sheet createSheet()
    {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = getPropertiesSet();
        sheet.put(set);

        try
        {
            // Text
            set.put(getProperty(
                    "Text", // property name
                    "Sets the text string", // property description
                    AnnotationProperties.class, // properties class
                    String.class, // property class
                    PropertyEditorSupport.class, // property editor class (null if none)
                    "getText", // get method name
                    "setText", // set method name
                    AnnotationProperties.TEXT // default property value
                    ));
            // Text Color
            set.put(getProperty(
                    "Text Color", // property name
                    "Sets the text color", // property description
                    AnnotationProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getTextColor", // get method name
                    "setTextColor", // set method name
                    AnnotationProperties.TEXT_COLOR // default property value
                    ));
            // Font
            set.put(getProperty(
                    "Font", // property name
                    "Sets the text font", // property description
                    AnnotationProperties.class, // properties class
                    Font.class, // property class
                    null, // property editor class (null if none)
                    "getFont", // get method name
                    "setFont", // set method name
                    AnnotationProperties.FONT // default property value
                    ));
            // Color
            set.put(getProperty(
                    "Color", // property name
                    "Sets the color", // property description
                    AnnotationProperties.class, // properties class
                    Color.class, // property class
                    null, // property editor class (null if none)
                    "getColor", // get method name
                    "setColor", // set method name
                    AnnotationProperties.COLOR // default property value
                    ));
            // Border Line Style
            set.put(getProperty(
                    "Border Line Style", // property name
                    "Sets the border line style", // property description
                    AnnotationProperties.class, // properties class
                    Stroke.class, // property class
                    StrokePropertyEditor.class, // property editor class (null if none)
                    "getStroke", // get method name
                    "setStroke", // set method name
                    StrokeGenerator.getStroke(AnnotationProperties.STROKE_INDEX) // default property value
                    ));
            // Inside Alpha
            set.put(getProperty(
                    "Inside Alpha", // property name
                    "Sets the inside alpha value", // property description
                    AnnotationProperties.class, // properties class
                    int.class, // property class
                    AlphaPropertyEditor.class, // property editor class (null if none)
                    "getInsideAlpha", // get method name
                    "setInsideAlpha", // set method name
                    AnnotationProperties.INSIDE_ALPHA // default property value
                    ));
            // Inside Visibility
            set.put(getProperty(
                    "Inside Visibility", // property name
                    "Sets the inside visibility flag", // property description
                    AnnotationProperties.class, // properties class
                    boolean.class, // property class
                    null, // property editor class (null if none)
                    "getInsideVisibility", // get method name
                    "setInsideVisibility", // set method name
                    AnnotationProperties.INSIDE_VISIBILITY // default property value
                    ));
        }
        catch (NoSuchMethodException ex)
        {
            LOG.log(Level.SEVERE, "[RectangleNode] : Method does not exist.", ex);
        }

        return sheet;
    }

}
