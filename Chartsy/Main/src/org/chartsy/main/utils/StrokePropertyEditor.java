package org.chartsy.main.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author viorel.gheba
 */
public class StrokePropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private InplaceEditor ed = null;

    public StrokePropertyEditor() {}

    public void paintValue(Graphics g, Rectangle box) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Stroke old = g2.getStroke();

        g.setColor(Color.WHITE);
        g2.fill(box);

        Stroke stroke = (Stroke) getValue();
        g2.setStroke(stroke);
        g2.setColor(Color.BLACK);

        int x = 5;
        while (box.width <= 2 * x) {
            x--;
        }

        g2.drawLine(x, box.height / 2, box.width - x, box.height / 2);

        g2.setStroke(old);
    }
    public void attachEnv(PropertyEnv env) { env.registerInplaceEditorFactory(this); }
    public InplaceEditor getInplaceEditor() {
        if (ed == null) ed = new Inplace();
        return ed;
    }
    public boolean isPaintable() { return true; }

    private static class Inplace implements InplaceEditor {

        private final StrokeComboBox comboBox = new StrokeComboBox();
        private PropertyEditor editor = null;
        private PropertyModel model;

        public void connect(PropertyEditor propertyEditor, PropertyEnv env) { editor = propertyEditor; reset(); }
        public JComponent getComponent() { return comboBox; }
        public void clear() { editor = null; model = null; }
        public Object getValue() { return comboBox.getSelectedItem(); }
        public void setValue(Object object) { int i = (Integer) object; comboBox.setSelectedItem(object); }
        public boolean supportsTextEntry() { return false; }
        public void reset() { if (editor.getValue() != null) comboBox.setSelectedItem(editor.getValue()); }
        public void addActionListener(ActionListener arg0) {}
        public void removeActionListener(ActionListener arg0) {}
        public KeyStroke[] getKeyStrokes() { return new KeyStroke[0]; }
        public PropertyEditor getPropertyEditor() { return editor; }
        public PropertyModel getPropertyModel() { return model; }
        public void setPropertyModel(PropertyModel propertyModel) { this.model = propertyModel; }
        public boolean isKnownComponent(Component component) { return component == comboBox || comboBox.isAncestorOf(component); }

    }

}
