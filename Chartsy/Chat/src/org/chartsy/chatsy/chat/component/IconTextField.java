package org.chartsy.chatsy.chat.component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class IconTextField extends JPanel
{
	
    private JTextField textField;
    private JLabel imageComponent;
    private JLabel downOption;

    public IconTextField(Icon icon)
	{
        setLayout(new GridBagLayout());
        setBackground((Color)UIManager.get("TextField.background"));

        textField = new JTextField();
        textField.setBorder(null);
        setBorder(new JTextField().getBorder());

        imageComponent = new JLabel(icon);
        downOption = new JLabel("icon");

        add(downOption, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(imageComponent, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(textField, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 0), 0, 0));

        downOption.setVisible(false);
    }

    public void enableDropdown(boolean enable)
	{
        downOption.setVisible(enable);
    }

    public void setText(String text)
	{
        textField.setText(text);
    }

    public String getText()
	{
        return textField.getText();
    }

    public void setIcon(Icon icon)
	{
        imageComponent.setIcon(icon);
    }

    public Icon getIcon()
	{
        return imageComponent.getIcon();
    }

    public JComponent getImageComponent()
	{
        return imageComponent;
    }

    public JTextField getTextComponent()
	{
        return textField;
    }
	
}






