package org.chartsy.annotation.text;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import org.chartsy.main.AnnotationPanel;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.chart.Annotation;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.utils.SerialVersion;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author Viorel
 */
public class TextAnnotation
        extends Annotation
{

    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private AnnotationProperties properties;

    public TextAnnotation()
    {
        super();
    }

    public TextAnnotation(ChartFrame frame)
    {
        super(frame);
        actionSet.set(TOP);
        actionSet.set(TOP_LEFT);
        actionSet.set(TOP_RIGHT);
        actionSet.set(LEFT);
        actionSet.set(RIGHT);
        actionSet.set(BOTTOM);
        actionSet.set(BOTTOM_LEFT);
        actionSet.set(BOTTOM_RIGHT);
        properties = new AnnotationProperties();
    }

    public @Override String getName()
    { return "Text"; }

    public Annotation newInstance(ChartFrame frame)
    { return new TextAnnotation(frame); }

    public boolean pointIntersects(double x, double y)
    {
        if (getActionPoint(x, y) != NONE)
            return true;

        double X1 = getXFromTime(getT1()), X2 = getXFromTime(getT2());
        double Y1 = getYFromValue(getV1()), Y2 = getYFromValue(getV2());

        java.awt.Rectangle r = new java.awt.Rectangle();
        r.setFrameFromDiagonal(X1, Y1, X2, Y2);

        return r.contains(x, y);
    }

    public void paint(Graphics2D g)
    {
        // get g properties
        java.awt.Rectangle oldClip = g.getClipBounds();
        Font oldFont = g.getFont();
        Stroke oldStroke = g.getStroke();

        String text = properties.getText();
        Font textFont = properties.getFont();
        Color textColor = properties.getTextColor();
        Color fillColor = properties.getFillColor();
        Color borderColor = properties.getColor();
        BasicStroke borderStroke = (BasicStroke) properties.getStroke();

        double border = borderStroke.getLineWidth();
        double X1 = getXFromTime(getT1()), X2 = getXFromTime(getT2());
        double Y1 = getYFromValue(getV1()), Y2 = getYFromValue(getV2());

        // initialize shape
        java.awt.Rectangle rectangle = new java.awt.Rectangle();
        rectangle.setFrameFromDiagonal(X1, Y1, X2, Y2);

        // paint rectangle fill if visible
        if (properties.getInsideVisibility())
        {
            g.setColor(fillColor);
            g.fill(rectangle);
        }

        // paint rectangle border
        g.setColor(borderColor);
        g.setStroke(borderStroke);
        g.draw(rectangle);

        // paint string inside the rectangle
        if (text != null && text.length() > 0)
        {
            g.setColor(textColor);
            g.setStroke(oldStroke);
            g.setFont(textFont);

            AttributedString attributedString = new AttributedString(text);
            attributedString.addAttribute(TextAttribute.FONT, textFont);
            AttributedCharacterIterator iterator = attributedString.getIterator();
            LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, g.getFontRenderContext());

            float width = (float) ((X2 - X1) - (2 * border));
            float Y = (float)(Y1 + border);

            List<TextLayout> layouts = new ArrayList<TextLayout>();
            List<Point2D> points = new ArrayList<Point2D>();

            TextLayout textLayout = null;
            while (measurer.getPosition() < iterator.getEndIndex())
            {
                textLayout = measurer.nextLayout(width);
                Y += (textLayout.getAscent());

                points.add(new Point2D.Double(X1 + borderStroke.getLineWidth(), Y));
                layouts.add(textLayout);

                Y += textLayout.getDescent() + textLayout.getLeading();
            }

            java.awt.Rectangle newClip = new java.awt.Rectangle();
            newClip.setFrameFromDiagonal(X1 + border, Y1 + border, X2 - border, Y2 - border);
            g.setClip(newClip);

            for (int i = 0; i < layouts.size(); i++)
            {
                layouts.get(i).draw(g, (float)points.get(i).getX(), (float)points.get(i).getY());
            }
        }

        // restore g properties
        g.setStroke(oldStroke);
        g.setFont(oldFont);
        g.setClip(oldClip);

        // paint action points
        if (isSelected())
            paintActionPoints(g);
    }

    public @Override void mouseReleased(MouseEvent e)
    {
        if (isNew())
        {
            NotifyDescriptor.InputLine notifyDescriptor = new NotifyDescriptor.InputLine(
                        "Text:",
                        "Type text for annotation",
                        NotifyDescriptor.OK_CANCEL_OPTION,
                        NotifyDescriptor.PLAIN_MESSAGE);
            Object retval = DialogDisplayer.getDefault().notify(notifyDescriptor);
            if (retval.equals(NotifyDescriptor.OK_OPTION))
            {
                String text = notifyDescriptor.getInputText();
                if (text == null)
                    text = "";
                properties.setText(text);
            }
        }

        Annotation current = getAnnotationPanel().getCurrent();
        if (current != null && current.isNew())
            current.setNew(false);

        AnnotationManager.getDefault().clearNewAnnotation();
        getAnnotationPanel().setState(AnnotationPanel.NONE);
        getAnnotationPanel().repaint();
    }

    

    public AbstractNode getNode()
    { return new AnnotationNode(properties); }

}
