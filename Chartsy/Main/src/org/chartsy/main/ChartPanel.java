package org.chartsy.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.chartsy.main.chart.Overlay;
import org.chartsy.main.data.Stock;
import org.chartsy.main.utils.Range;
import org.chartsy.main.utils.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class ChartPanel 
        extends JPanel
        implements Serializable
{
    
    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private ChartFrame chartFrame;
    private AnnotationPanel annotationPanel;
    private JPanel top;
    private JLabel stockInfo;
    private JLabel overlayLabels;
    private List<Overlay> overlays;

    public ChartPanel(ChartFrame frame)
    {
        chartFrame = frame;
        overlays = new ArrayList<Overlay>();

        Font f = chartFrame.getChartProperties().getFont();

        annotationPanel = new AnnotationPanel(chartFrame);
        top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);

        stockInfo = new JLabel();
        stockInfo.setOpaque(false);
        stockInfo.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));

        overlayLabels = new JLabel(" ");
        overlayLabels.setOpaque(false);
        overlayLabels.setFont(f);

        if (!chartFrame.getChartData().isStockNull())
        {
            Stock stock = chartFrame.getChartData().getStock();
            StringBuffer sb = new StringBuffer();
            sb.append(stock.getKey());
            if (!stock.getCompanyName().equals(""))
                sb.append(" - ");
            sb.append(stock.getCompanyName());
            stockInfo.setText(sb.toString());
        }
        else
        {
            stockInfo.setText("No data for this symbol");
        }

        top.add(stockInfo);
        top.add(overlayLabels);

        setOpaque(false);
        setLayout(new LayoutManager() {
            public void addLayoutComponent(String name, Component comp)
            {}
            public void removeLayoutComponent(Component comp)
            {}
            public Dimension preferredLayoutSize(Container parent)
            {return new Dimension(0, 0);}
            public Dimension minimumLayoutSize(Container parent)
            {return new Dimension(0, 0);}
            public void layoutContainer(Container parent)
            {
                int width = parent.getWidth();
                int height = parent.getHeight();
                top.setBounds(0, 0, width, 32);
                annotationPanel.setBounds(0, 2, width - 4, height - 4);
            }
        });

        add(top);
        add(annotationPanel);
    }

    public ChartFrame getChartFrame()
    { return chartFrame; }

    public void setChartFrame(ChartFrame frame)
    { chartFrame = frame; }

    public AnnotationPanel getAnnotationPanel()
    { return annotationPanel; }

    public void setAnnotationPanel(AnnotationPanel panel)
    { annotationPanel = panel; }

    public Range getRange()
    { return chartFrame.getChartData().getVisibleRange(); }

    public void paint(Graphics g)
    {
        Font font = chartFrame.getChartProperties().getFont();
        Color fontColor = chartFrame.getChartProperties().getFontColor();
        
        stockInfo.setFont(font.deriveFont(font.getStyle() ^ Font.BOLD));
        stockInfo.setForeground(fontColor);
        
        overlayLabels.setFont(font);
        overlayLabels.setForeground(fontColor);

        setOverlaysLabel();

        super.paint(g);

        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setPaintMode();

        chartFrame.getChartData().calculateRange(chartFrame, overlays);
        if (!chartFrame.getChartData().isChartNull())
        {
            chartFrame.getChartData().getChart().paint(g2, chartFrame);
        }

        if (!overlays.isEmpty())
        {
            Rectangle bounds = getBounds();
            bounds.grow(-2, -2);
            for (Overlay overlay : overlays)
            {
                overlay.paint(g2, chartFrame, bounds);
            }
        }

        g2.dispose();
    }

    public void updateStock()
    {
        if (!chartFrame.getChartData().isStockNull())
        {
            Stock stock = chartFrame.getChartData().getStock();
            StringBuffer sb = new StringBuffer();
            sb.append(stock.getKey());
            if (!stock.getCompanyName().equals(""))
                sb.append(" - ");
            sb.append(stock.getCompanyName());
            stockInfo.setText(sb.toString());
        }
        else
        {
            stockInfo.setText("No data for this symbol");
        }
    }

    private void setOverlaysLabel() 
    {
        if (!overlays.isEmpty())
        {
            StringBuffer sb = new StringBuffer();
            String d = ", ";
            int count = overlays.size() - 1;
            for (int i = 0; i < overlays.size(); i++)
            {
                Overlay o = overlays.get(i);
                sb.append(o.getLabel());
                if (i < count)
                    sb.append(d);
            }
            overlayLabels.setText(sb.toString());
        }
        else
        {
            overlayLabels.setText(" ");
        }
    }

    public synchronized void setOverlays(List<Overlay> list)
    {
        clearOverlays();
        chartFrame.getChartData().removeAllOverlaysDatasetListeners();
        for (Overlay o : list)
        {
            o.setDataset(chartFrame.getChartData().getDataset());
            o.calculate();
            chartFrame.getChartData().addOverlaysDatasetListeners(o);
            addOverlay(o);
        }
    }

    public List<Overlay> getOverlays()
    {
        List<Overlay> list = new ArrayList<Overlay>();
        for (Overlay overlay : overlays)
            list.add(overlay);
        return list;
    }

    public Overlay getOverlay(int index)
    {
        if (index < 0 || index > overlays.size())
            return null;
        return overlays.get(index);
    }

    public int getOverlaysCount()
    { return overlays.size(); }

    public void addOverlay(Overlay overlay)
    {
        chartFrame.getChartProperties().addLogListener(overlay);
        chartFrame.getChartData().addOverlaysDatasetListeners(overlay);
        overlays.add(overlay);
    }

    public void clearOverlays()
    {
        overlays.clear();
        overlays = new ArrayList<Overlay>();
    }

    @Override
    public Rectangle getBounds()
    { return new Rectangle(0, 0, getWidth(), getHeight()); }

    /*public class OverlayLabel
            extends JLabel
            implements Serializable
    {

        private static final long serialVersionUID = SerialVersion.APPVERSION;
        private boolean rollOver = false;

    }*/

}
