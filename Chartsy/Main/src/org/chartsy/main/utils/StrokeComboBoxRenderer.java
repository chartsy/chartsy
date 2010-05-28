package org.chartsy.main.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author viorel.gheba
 */
public class StrokeComboBoxRenderer extends JComponent implements ListCellRenderer {

    private Stroke stroke;

    public StrokeComboBoxRenderer(int width, int height) {
        setOpaque(true);
        setPreferredSize(new Dimension(width, height));
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        stroke = (Stroke) value;
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Stroke oldStroke = g2.getStroke();

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        g2.setStroke(stroke);
        g2.setColor(getForeground());

        int x = 5;
        while (getWidth() <= 2 * x) {
            x--;
        }

        g2.drawLine(x, getHeight() / 2, getWidth() - x, getHeight() / 2);

        g2.setStroke(oldStroke);
    }

}
