package org.chartsy.main.utils;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.jdesktop.swingx.JXDatePicker;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author Viorel
 */
public class DatePropertyEditor 
	extends PropertyEditorSupport
	implements ExPropertyEditor, InplaceEditor.Factory
{

	private Inplace editor;

	@Override public String getAsText()
	{
		Date d = (Date) getValue();
		if (d == null)
			return "No Date Set";
		return new SimpleDateFormat("MM/dd/yy").format(d);
	}

	@Override public void setAsText(String s)
	{
		try
		{
			setValue(new SimpleDateFormat("MM/dd/yy").parse(s));
		}
		catch (ParseException ex)
		{
			IllegalArgumentException iae
				= new IllegalArgumentException("Could not parse date");
			throw iae;
		}
	}

	public void attachEnv(PropertyEnv pe)
	{
		pe.registerInplaceEditorFactory(this);
	}

	public InplaceEditor getInplaceEditor()
	{
		if (editor == null)
			editor = new Inplace();
		return editor;
	}

	private static class Inplace implements InplaceEditor
	{

		private final JXDatePicker picker = new JXDatePicker();
		private PropertyEditor editor = null;

		public void connect(PropertyEditor propertyEditor, PropertyEnv env)
		{
			editor = propertyEditor;
			reset();
		}

		public JComponent getComponent()
		{
			return picker;
		}

		public void clear()
		{
			editor = null;
			model = null;
		}

		public Object getValue()
		{
			return picker.getDate();
		}

		public void setValue(Object object)
		{
			picker.setDate((Date) object);
		}

		public boolean supportsTextEntry()
		{
			return true;
		}

		public void reset()
		{
			Date d = (Date) editor.getValue();
            if (d != null)
                picker.setDate(d);
		}

		public KeyStroke[] getKeyStrokes()
		{
			return new KeyStroke[0];
		}

		public PropertyEditor getPropertyEditor()
		{
			return editor;
		}

		public PropertyModel getPropertyModel()
		{
			return model;
		}

		private PropertyModel model;

		public void setPropertyModel(PropertyModel pm)
		{
			this.model = pm;
		}

		public void addActionListener(ActionListener al)
		{
			// do nothing
		}

		public void removeActionListener(ActionListener al)
		{
			// do nothing
		}

		public boolean isKnownComponent(Component component)
		{
			return component == picker || picker.isAncestorOf(component);
		}

	}

}
