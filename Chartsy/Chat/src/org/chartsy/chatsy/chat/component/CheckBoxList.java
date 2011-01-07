package org.chartsy.chatsy.chat.component;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CheckBoxList extends JPanel
{

    private Map<JCheckBox, String> valueMap = new HashMap<JCheckBox, String>();
    private JPanel internalPanel = new JPanel();

    public CheckBoxList()
	{
        setLayout(new BorderLayout());
        internalPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 5, 5, true, false));
        add(new JScrollPane(internalPanel), BorderLayout.CENTER);
    }

    public void addCheckBox(JCheckBox box, String value)
	{
        internalPanel.add(box);
        valueMap.put(box, value);
    }

    public List getSelectedValues()
	{
        List<String> list = new ArrayList<String>();
        for (JCheckBox checkbox : valueMap.keySet())
		{
            if (checkbox.isSelected())
			{
                String value = valueMap.get(checkbox);
                list.add(value);
            }
        }
        return list;
    }
}
