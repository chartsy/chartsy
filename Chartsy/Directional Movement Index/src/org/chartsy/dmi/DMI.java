package org.chartsy.dmi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import org.chartsy.main.chartsy.ChartFrame;
import org.chartsy.main.chartsy.DefaultPainter;
import org.chartsy.main.chartsy.chart.Indicator;
import org.chartsy.main.dataset.Dataset;
import org.chartsy.main.utils.Range;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author viorel.gheba
 */
public class DMI extends Indicator implements Serializable {

    private static final long serialVersionUID = 101L;
    public static final String PDI = "pdi";
    public static final String MDI = "mdi";
    public static final String ADX = "adx";

    private IndicatorProperties properties = new IndicatorProperties();

    public DMI() {
        super("DMI", "Description", "DMI");
    }

    public String getLabel() { return properties.getLabel() + "(" + properties.getPeriod() + ")"; }

    public LinkedHashMap getHTML(ChartFrame cf, int i) {
        LinkedHashMap ht = new LinkedHashMap();

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double[] values = getValues(cf, i);
        String[] labels = {"DI+:", "DI-:", "ADX:"};

        ht.put(getLabel(), " ");
        if (values.length > 0) {
            Color[] colors = getColors();
            for (int j = 0; j < values.length; j++) {
                ht.put(getFontHTML(colors[j], labels[j]),
                        getFontHTML(colors[j], df.format(values[j])));
            }
        }

        return ht;
    }

    public Range getRange(ChartFrame cf) {
        Dataset pdi = visibleDataset(cf, PDI);
        Dataset mdi = visibleDataset(cf, MDI);
        Dataset adx = visibleDataset(cf, ADX);

        if (pdi != null && mdi != null && adx != null) {
            Range range = new Range(0, adx.getMax(Dataset.CLOSE));
            range = Range.combine(range, new Range(0, pdi.getMax(Dataset.CLOSE)));
            range = Range.combine(range, new Range(0, mdi.getMax(Dataset.CLOSE)));
            return range;
        }
        return new Range(0, 100);
    }

    public void paint(Graphics2D g, ChartFrame cf) {
        Dataset pdi = visibleDataset(cf, PDI);
        Dataset mdi = visibleDataset(cf, MDI);
        Dataset adx = visibleDataset(cf, ADX);

        if (pdi != null && mdi != null && adx != null) {
            Range range = getRange(cf);
            Rectangle2D.Double bounds = getBounds();

            DefaultPainter.line(g, cf, range, bounds, pdi, properties.getPDIColor(), properties.getPDIStroke()); // paint PDI
            DefaultPainter.line(g, cf, range, bounds, mdi, properties.getMDIColor(), properties.getMDIStroke()); // paint MDI
            DefaultPainter.line(g, cf, range, bounds, adx, properties.getADXColor(), properties.getADXStroke()); // paint ADX
            DefaultPainter.label(g, cf, getLabel(), bounds); // paint label
        }
    }

    public void calculate() {
        Dataset initial = getDataset();
        if (initial != null && !initial.isEmpty()) {
            int period = properties.getPeriod();
            Dataset[] datasets = Dataset.ADX(initial, period);
            addDataset(PDI, datasets[0]); // PDI dataset
            addDataset(MDI, datasets[1]); // MDI dataset
            addDataset(ADX, datasets[2]); // ADX dataset
        }
    }

    public boolean hasZeroLine() { return false; }
    public boolean getZeroLineVisibility() { return false; }
    public Color getZeroLineColor() { return null; }
    public Stroke getZeroLineStroke() { return null; }
    public Color[] getColors() { return new Color[] {properties.getPDIColor(), properties.getMDIColor(), properties.getADXColor()}; }
    public double[] getValues(ChartFrame cf) {
        Dataset pdi = visibleDataset(cf, PDI);
        Dataset mdi = visibleDataset(cf, MDI);
        Dataset adx = visibleDataset(cf, ADX);
        if (pdi != null && mdi != null && adx != null)
            return new double[] {pdi.getLastPriceValue(Dataset.CLOSE), mdi.getLastPriceValue(Dataset.CLOSE), adx.getLastPriceValue(Dataset.CLOSE)};
        return new double[] {};
    }
    public double[] getValues(ChartFrame cf, int i) {
        Dataset pdi = visibleDataset(cf, PDI);
        Dataset mdi = visibleDataset(cf, MDI);
        Dataset adx = visibleDataset(cf, ADX);
        if (pdi != null && mdi != null && adx != null)
            return new double[] {pdi.getPriceValue(i, Dataset.CLOSE), mdi.getPriceValue(i, Dataset.CLOSE), adx.getPriceValue(i, Dataset.CLOSE)};
        return new double[] {};
    }
    public boolean getMarkerVisibility() { return properties.getMarker(); }
    public AbstractNode getNode() { return new IndicatorNode(properties); }

}
