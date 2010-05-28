package org.chartsy.main.utils;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author viorel.gheba
 */
public class AlphaPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private InplaceEditor ed = null;

    public AlphaPropertyEditor() {}

    public void attachEnv(PropertyEnv env) { env.registerInplaceEditorFactory(this); }
    public InplaceEditor getInplaceEditor() {
        if (ed == null) ed = new Inplace();
        return ed;
    }

    private static class Inplace implements InplaceEditor {

        private final JSlider slider = new JSlider(0, 255, 25);
        private PropertyEditor editor = null;
        private PropertyModel model;

        public void connect(PropertyEditor propertyEditor, PropertyEnv env) { editor = propertyEditor; reset(); }
        public JComponent getComponent() { return slider; }
        public void clear() { editor = null; model = null; }
        public Object getValue() { return slider.getValue(); }
        public void setValue(Object object) { slider.setValue((Integer) object); }
        public boolean supportsTextEntry() { return false; }
        public void reset() { if (editor.getValue() != null) slider.setValue((Integer) editor.getValue()); }
        public void addActionListener(ActionListener arg0) {}
        public void removeActionListener(ActionListener arg0) {}
        public KeyStroke[] getKeyStrokes() { return new KeyStroke[0]; }
        public PropertyEditor getPropertyEditor() { return editor; }
        public PropertyModel getPropertyModel() { return model; }
        public void setPropertyModel(PropertyModel propertyModel) { this.model = propertyModel; }
        public boolean isKnownComponent(Component component) { return component == slider || slider.isAncestorOf(component); }

    }

}
