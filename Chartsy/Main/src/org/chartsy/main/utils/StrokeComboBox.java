package org.chartsy.main.utils;

import java.awt.Dimension;
import java.awt.Stroke;
import javax.swing.JComboBox;

/**
 *
 * @author viorel.gheba
 */
public class StrokeComboBox extends JComboBox {

    public StrokeComboBox() { this(StrokeGenerator.getStrokes(), 100, 30); }

    public StrokeComboBox(Stroke[] strokes, int width, int height) {
        super(strokes);
        setRenderer(new StrokeComboBoxRenderer(width, height));
        Dimension prefSize = getPreferredSize();
        prefSize.height = height + getInsets().top + getInsets().bottom;
        setPreferredSize(prefSize);
        setMaximumRowCount(10);
    }

}
