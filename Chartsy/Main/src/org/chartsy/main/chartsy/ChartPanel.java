package org.chartsy.main.chartsy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import org.chartsy.main.chartsy.axis.DateAxis;
import org.chartsy.main.chartsy.axis.HorizontalGrid;
import org.chartsy.main.chartsy.axis.PriceAxis;
import org.chartsy.main.chartsy.axis.PriceAxisMarker;
import org.chartsy.main.chartsy.axis.VerticalGrid;
import org.chartsy.main.chartsy.chart.Annotation;
import org.chartsy.main.managers.AnnotationManager;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author viorel.gheba
 */
public class ChartPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, Printable {

    public static final int NONE = 0;
    public static final int NEWANNOTATION = 1;
    public static final int RESIZE = 2;
    public static final int MOVE = 3;
    private int state;

    protected ChartFrame chartFrame;
    protected Annotation current;
    protected Annotation[] annotations;
    protected Annotation[] intraDayAnnotations;

    public static ChartPanel newInstance(ChartFrame cf) { return new ChartPanel(cf); }

    private ChartPanel(ChartFrame cf) {
        state = NONE;
        chartFrame = cf;
        annotations = new Annotation[0];
        intraDayAnnotations = new Annotation[0];
        current = null;

        setFocusable(true);
        requestFocusInWindow();

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        setOpaque(true);
        setLayout(new BorderLayout());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        g2.setPaint(chartFrame.getChartProperties().getBackgroundColor());
        g2.fillRect(0, 0, getWidth(), getHeight());

        chartFrame.getChartRenderer().setWidth(getWidth());
        chartFrame.getChartRenderer().setHeight(getHeight());

        if (chartFrame.getChartRenderer().getMainDataset() != null)
            chartFrame.getChartRenderer().calculate();

        if (chartFrame.getChartRenderer().getVisibleDataset() != null) {
            g2.setPaintMode();
            HorizontalGrid.paint(g2, chartFrame); // paint horizontal grid
            VerticalGrid.paint(g2, chartFrame); // paint vertical grid
            DateAxis.paint(g2, chartFrame); // date axis
            PriceAxis.paint(g2, chartFrame); // paint price axis
            PriceAxisMarker.paint(g2, chartFrame); // paint chart axis marker
            chartFrame.paintChart(g2); // paint chart
            chartFrame.getChartRenderer().paintOverlays(g2);// paint overlays
            chartFrame.getChartRenderer().paintIndicators(g2); // paint indicators
            chartFrame.getChartRenderer().paintLabels(g2); // paint labels
            chartFrame.getMarker().paint(g2); // paint marker
            paintAnnotations(g2); // paint annotations
            setFocusable(true);
            requestFocusInWindow();
        }
    }

    public ChartFrame getChartFrame() { return chartFrame; }
    public void setState(int s) { state = s; }
    public int getState() { return state; }

    public Annotation getCurrent() { return current; }
    public void setCurrent(Annotation a) { current = a; }
    public boolean hasCurrent() { return (current != null); }

    public void moveDown() {
        if (current != null && current.isSelected()) {
            current.moveDown();
            repaint();
        }
    }

    public void moveUp() {
        if (current != null && current.isSelected()) {
            current.moveUp();
            repaint();
        }
    }

    public void moveLeft() {
        if (current != null && current.isSelected()) {
            current.moveLeft();
            repaint();
        }
    }

    public void moveRight() {
        if (current != null && current.isSelected()) {
            current.moveRight();
            repaint();
        }
    }

    public void removeAllAnnotations() {
        if (!chartFrame.getTime().contains("Min")) {
            Annotation[] list = new Annotation[0];
            annotations = list;
            current = null;
        } else {
            Annotation[] list = new Annotation[0];
            intraDayAnnotations = list;
            current = null;
        }
        repaint();
    }

    public void removeAnnotation() {
        if (current != null && current.isSelected()) {
            if (!chartFrame.getTime().contains("Min")) {
                if (annotations.length == 1) {
                    Annotation[] list = new Annotation[0];
                    annotations = list;
                } else {
                    Annotation[] list = new Annotation[annotations.length - 1];
                    int k = 0;
                    for (int i = 0; i < annotations.length; i++) {
                        if (!current.equals(annotations[i])) {
                            list[k] = annotations[i];
                            k++;
                        }
                    }
                    annotations = list;
                }
            } else {
                if (intraDayAnnotations.length == 1) {
                    Annotation[] list = new Annotation[0];
                    intraDayAnnotations = list;
                } else {
                    Annotation[] list = new Annotation[intraDayAnnotations.length - 1];
                    int k = 0;
                    for (int i = 0; i < intraDayAnnotations.length; i++) {
                        if (!current.equals(intraDayAnnotations[i])) {
                            list[k] = intraDayAnnotations[i];
                            k++;
                        }
                    }
                    intraDayAnnotations = list;
                }
            }
        }
        repaint();
    }

    public void removeAnnotation(int index) {
        if (!chartFrame.getTime().contains("Min")) {
            Annotation[] list = new Annotation[annotations.length - 1];
            int k = 0;
            for (int i = 0; i < annotations.length; i++) {
                if (index != i) {
                    list[k] = annotations[i];
                    k++;
                }
            }
            annotations = list;
        } else {
            Annotation[] list = new Annotation[intraDayAnnotations.length - 1];
            int k = 0;
            for (int i = 0; i < intraDayAnnotations.length; i++) {
                if (index != i) {
                    list[k] = intraDayAnnotations[i];
                    k++;
                }
            }
            intraDayAnnotations = list;
        }
    }

    public void addExtraDayAnnotation(Annotation a) {
        if (a != null) {
            Annotation[] list = new Annotation[annotations.length + 1];
            for (int i = 0; i < annotations.length; i++)
                list[i] = annotations[i];
            list[annotations.length] = a;
            a.setIntraDay(Annotation.NO);
            annotations = list;
        }
    }

    public void addIntraDayAnnotation(Annotation a) {
        if (a != null) {
            Annotation[] list = new Annotation[intraDayAnnotations.length + 1];
            for (int i = 0; i < intraDayAnnotations.length; i++)
                list[i] = intraDayAnnotations[i];
            list[intraDayAnnotations.length] = a;
            a.setIntraDay(Annotation.YES);
            intraDayAnnotations = list;
        }
    }

    public void addAnnotation(Annotation a) {
        if (a != null) {
            a.setSelected(true);
            initializeAnnotations();
            if (!chartFrame.getTime().contains("Min")) {
                Annotation[] list = new Annotation[annotations.length + 1];
                for (int i = 0; i < annotations.length; i++)
                    list[i] = annotations[i];
                list[annotations.length] = a;
                a.setIntraDay(Annotation.NO);
                annotations = list;
            } else {
                Annotation[] list = new Annotation[intraDayAnnotations.length + 1];
                for (int i = 0; i < intraDayAnnotations.length; i++)
                    list[i] = intraDayAnnotations[i];
                list[intraDayAnnotations.length] = a;
                a.setIntraDay(Annotation.YES);
                intraDayAnnotations = list;
            }
            current = a;
        }
    }

    public void deselectAll() {
        if (!chartFrame.getTime().contains("Min")) {
            for (int i = 0; i < annotations.length; i++) annotations[i].setSelected(false);
        } else {
            for (int i = 0; i < intraDayAnnotations.length; i++) intraDayAnnotations[i].setSelected(false);
        }
        current = null;
    }

    public Annotation getSelected() {
        if (!chartFrame.getTime().contains("Min")) {
            for (int i = 0; i < annotations.length; i++) if (annotations[i].isSelected()) return annotations[i];
        } else {
            for (int i = 0; i < intraDayAnnotations.length; i++) if (intraDayAnnotations[i].isSelected()) return intraDayAnnotations[i];
        }
        return null;
    }

    protected void initializeAnnotations() {
        if (!chartFrame.getTime().contains("Min")) {
            for (int i = 0; i < annotations.length; i++) annotations[i].setJustAdded(false);
        } else {
            for (int i = 0; i < intraDayAnnotations.length; i++) intraDayAnnotations[i].setJustAdded(false);
        }
    }

    protected boolean isAnnotation(double x, double y) {
        if (!chartFrame.getTime().contains("Min")) {
            for (int i = 0; i < annotations.length; i++) {
                boolean b = annotations[i].pointIntersects(x, y);
                if (b) {
                    current = annotations[i];
                    current.setActive(true);
                    current.setSelected(true);
                    return b;
                }
            }
        } else {
            for (int i = 0; i < intraDayAnnotations.length; i++) {
                boolean b = intraDayAnnotations[i].pointIntersects(x, y);
                if (b) {
                    current = intraDayAnnotations[i];
                    current.setActive(true);
                    current.setSelected(true);
                    return b;
                }
            }
        }
        return false;
    }

    protected void paintAnnotations(Graphics2D g) {
        Rectangle oldR = g.getClipBounds();
        if (!chartFrame.getTime().contains("Min")) {
            for (int i = 0; i < annotations.length; i++) {
                Rectangle newR = new Rectangle();
                Rectangle2D.Double b = chartFrame.getChartRenderer().getClickedBounds(annotations[i].getAreaIndex());
                if (b != null) {
                    newR.setFrameFromDiagonal(b.getMinX(), b.getMinY(), b.getMaxX(), b.getMaxY());
                    g.setClip(newR);
                    annotations[i].paintAnnotation(g);
                } else {
                    removeAnnotation(i);
                    repaint();
                }
            }
        } else {
            for (int i = 0; i < intraDayAnnotations.length; i++) {
                Rectangle newR = new Rectangle();
                Rectangle2D.Double b = chartFrame.getChartRenderer().getClickedBounds(intraDayAnnotations[i].getAreaIndex());
                if (b != null) {
                    newR.setFrameFromDiagonal(b.getMinX(), b.getMinY(), b.getMaxX(), b.getMaxY());
                    g.setClip(newR);
                    intraDayAnnotations[i].paintAnnotation(g);
                } else {
                    removeAnnotation(i);
                    repaint();
                }
            }
        }
        g.setClip(oldR);
    }

    public void mouseClicked(MouseEvent e) {
        if (getState() == NONE && chartFrame.getChartProperties().getMarkerVisibility()) {
            int index = chartFrame.getMarker().getIndex(e.getX(), e.getY());
            chartFrame.getMarker().setIndex(index);
            repaint();
        }
    }
    public void mousePressed(MouseEvent e) {
        if (getState() == NONE) {
            if (!isAnnotation(e.getX(), e.getY())) {
                deselectAll();
                setState(NONE);
                repaint();
            } else {
                if (hasCurrent())
                    getCurrent().mousePressed(e);
            }
        } else if (getState() == RESIZE) {
            if (hasCurrent()) getCurrent().mousePressed(e);
        } else if (getState() == MOVE) {
            if (hasCurrent()) getCurrent().mousePressed(e);
        } else if (getState() == NEWANNOTATION) {
            int x = e.getX(), y = e.getY();
            int areaIndex = chartFrame.getChartRenderer().getAreaIndex(x, y);
            if (areaIndex != -1) {
                Annotation a = AnnotationManager.getDefault().getNewAnnotation(chartFrame);
                a.setAreaIndex(areaIndex);
                Rectangle2D.Double bounds = chartFrame.getChartRenderer().getClickedBounds(areaIndex);
                Range range = chartFrame.getChartRenderer().getClickedRange(areaIndex);
                a.setBounds(bounds);
                a.setRange(range);
                setCurrent(a);
                if (hasCurrent())
                    getCurrent().mousePressed(e);
            } else {
                setState(NONE);
                mousePressed(e);
            }
        }
    }
    public void mouseReleased(MouseEvent e) {
        if (hasCurrent())
            getCurrent().mouseReleased(e);
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
        if (hasCurrent())
            getCurrent().mouseDragged(e);
    }
    public void mouseMoved(MouseEvent e) {}
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (getState() == NONE) {
            int end = chartFrame.getChartRenderer().getEnd() - e.getWheelRotation();
            chartFrame.updateHorizontalScrollBar(end);
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DELETE:
                removeAnnotation();
                break;
            case KeyEvent.VK_UP:
                moveUp();
                break;
            case KeyEvent.VK_DOWN:
                moveDown();
                break;
            case KeyEvent.VK_LEFT:
                moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                moveRight();
                break;
        }
        repaint();
    }

    public void readAnnotationsXMLDocument(Element parent) {
        NodeList nodeList = parent.getElementsByTagName("annotation");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String name = XMLUtils.getStringParam(element, "name");
            AnnotationManager.getDefault().setNewAnnotationName(name);
            Annotation a = AnnotationManager.getDefault().getNewAnnotation(chartFrame);
            a.readXMLDocument(element);
            if (a.isIntraDay() == Annotation.NO) addExtraDayAnnotation(a);
            else addIntraDayAnnotation(a);
        }
        AnnotationManager.getDefault().setNewAnnotationName("");
    }

    public void writeAnnotationsXMLDocument(Document document, Element parent) {
        Element element;
        for (int i = 0; i < annotations.length; i++) {
            element = document.createElement("annotation");
            annotations[i].writeXMLDocument(document, element);
            parent.appendChild(element);
        }
        for (int i = 0; i < intraDayAnnotations.length; i++) {
            element = document.createElement("annotation");
            intraDayAnnotations[i].writeXMLDocument(document, element);
            parent.appendChild(element);
        }
    }

    public BufferedImage getBufferedImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        String text = "Chartsy.org \u00a9 2009 MrSwing bvba";
        Font font = new Font("Tahoma", Font.PLAIN, 24);
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(text, font, frc);

        paint(g);

        float x = (float) (width - layout.getVisibleAdvance() - chartFrame.getChartProperties().getDataOffset().right - layout.getAscent());
        float y = (float) (height - layout.getAscent() - chartFrame.getChartProperties().getDataOffset().bottom);
        g.setColor(Color.BLACK);
        layout.draw(g, x, y);

        g.dispose();

        return image;
    }

    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) return NO_SUCH_PAGE;

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pf.getImageableX(), pf.getImageableY());

        int width = getWidth();
        int height = getHeight();

        setSize((int) pf.getImageableWidth(), (int) pf.getImageableHeight());
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
        this.paint(g2);
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);
        setSize(width, height);

        g2.drawRect(0, 0, (int) pf.getImageableWidth(), (int) pf.getImageableHeight());

        return PAGE_EXISTS;
    }

}
