package org.chartsy.main.utils;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 *
 * @author viorel.gheba
 */
public final class ComponentGenerator {

    public static final String JTEXTFIELD = "JTextField";
    public static final String JCOMBOBOX = "JComboBox";
    public static final String JLABEL = "JLabel";
    public static final String JCHECKBOX = "JCheckBox";
    public static final String JSLIDER = "JSlider";
    public static final String JSTROKECOMBOBOX = "JStrokeComboBox";

    private ComponentGenerator() {
        // does nothing
    }

    public static JComponent getComponent(final String component) {
        if (component.equals(JTEXTFIELD)) return new JTextField();
        else if (component.equals(JCOMBOBOX)) return new JComboBox();
        else if (component.equals(JLABEL)) return new JLabel();
        else if (component.equals(JCHECKBOX)) return new JCheckBox();
        else if (component.equals(JSLIDER)) return new JSlider();
        else if (component.equals(JSTROKECOMBOBOX)) return new StrokeComboBox();
        return null;
    }

}
