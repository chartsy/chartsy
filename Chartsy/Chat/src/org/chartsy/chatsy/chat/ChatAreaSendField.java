package org.chartsy.chatsy.chat;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.chartsy.chatsy.Chatsy;
import org.chartsy.chatsy.chat.ui.ChatInputEditor;

public class ChatAreaSendField extends JPanel
{
	
	private ChatInputEditor textField;
    private JButton button;

    public ChatAreaSendField(String text)
	{
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.lightGray));
		
        textField = new ChatInputEditor();
        textField.setBorder(null);
		
        button = new JButton();
        if (Chatsy.isMac())
            button.setContentAreaFilled(false);
		button.setText(text);
        add(button, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
			new Insets(2, 2, 2, 2), 0, 0));
        button.setVisible(false);
        
        final JScrollPane pane = new JScrollPane(textField);
        pane.setBorder(null);
        add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(2, 2, 2, 2), 0, 0));
        button.setEnabled(false);
    }

    public JButton getButton()
	{
        return button;
    }

    public void showSendButton(boolean show)
	{
        button.setVisible(show);
    }

    public void enableSendButton(boolean enabled)
	{
        button.setEnabled(enabled);
    }

    public void setText(String text)
	{
        textField.setText(text);
    }

    public String getText()
	{
        return textField.getText();
    }

    public ChatInputEditor getChatInputArea()
	{
        return textField;
    }

}








