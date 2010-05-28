package org.chartsy.main.utils;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.chartsy.main.data.Dataset;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author viorel.gheba
 */
public class PricePropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private InplaceEditor ed = null;

    public PricePropertyEditor() {}

    public String getAsText() { return (String) getValue(); }
    public void setAsText(String s) { setValue(s); }
    public void attachEnv(PropertyEnv env) { env.registerInplaceEditorFactory(this); }
    public InplaceEditor getInplaceEditor() {
        if (ed == null) ed = new Inplace();
        return ed;
    }

    private static class Inplace implements InplaceEditor {

        private final JComboBox comboBox = new JComboBox(Dataset.LIST);
        private PropertyEditor editor = null;
        private PropertyModel model;

        public void connect(PropertyEditor propertyEditor, PropertyEnv env) { editor = propertyEditor; reset(); }
        public JComponent getComponent() { return comboBox; }
        public void clear() { editor = null; model = null; }
        public Object getValue() { return comboBox.getSelectedItem(); }
        public void setValue(Object object) { comboBox.setSelectedItem(object); }
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
