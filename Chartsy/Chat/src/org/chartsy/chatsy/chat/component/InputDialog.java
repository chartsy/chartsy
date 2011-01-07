package org.chartsy.chatsy.chat.component;

import org.chartsy.chatsy.chat.ChatsyManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class InputDialog implements PropertyChangeListener
{

    private JTextArea textArea;
    private JOptionPane optionPane;
    private JDialog dialog;

    private String stringValue;
    private int width = 400;
    private int height = 250;

    public InputDialog()
	{
    }

    public String getInput(String title, String description, Icon icon,
		int width, int height)
	{
        this.width = width;
        this.height = height;
        return getInput(title, description, icon, ChatsyManager.getMainWindow());
    }

    public String getInput(String title, String description, Icon icon, 
		Component parent)
	{
        textArea = new JTextArea();
        textArea.setLineWrap(true);

        TitlePanel titlePanel = new TitlePanel(title, description, icon, true);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        final Object[] options = {"Ok", "Cancel"};
        optionPane = new JOptionPane(
			new JScrollPane(textArea),
			JOptionPane.PLAIN_MESSAGE, 
			JOptionPane.OK_CANCEL_OPTION,
			ChatsyManager.getApplicationImage(),
			options,
			options[0]);
        mainPanel.add(optionPane, BorderLayout.CENTER);

        JOptionPane p = new JOptionPane();
        dialog = p.createDialog(parent, title);
        dialog.setModal(true);
        dialog.pack();
        dialog.setSize(width, height);
        dialog.setContentPane(mainPanel);
        dialog.setLocationRelativeTo(parent);
        optionPane.addPropertyChangeListener(this);

        textArea.addKeyListener(new KeyAdapter()
		{
            public void keyPressed(KeyEvent e)
			{
                if (e.getKeyChar() == KeyEvent.VK_TAB)
                    optionPane.requestFocus();
                else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) 
                    dialog.dispose();
            }
        });
        textArea.requestFocus();
        textArea.setWrapStyleWord(true);
        dialog.setVisible(true);
        return stringValue;
    }

    public Action nextFocusAction = new AbstractAction("Move Focus Forwards")
	{
        public void actionPerformed(ActionEvent evt)
		{
            ((Component)evt.getSource()).transferFocus();
        }
    };

    public Action prevFocusAction = new AbstractAction("Move Focus Backwards")
	{
        public void actionPerformed(ActionEvent evt)
		{
            ((Component)evt.getSource()).transferFocusBackward();
        }
    };

    public void propertyChange(PropertyChangeEvent e)
	{
        String value = (String)optionPane.getValue();
        if ("Cancel".equals(value))
		{
            stringValue = null;
            dialog.setVisible(false);
        }
        else if ("Ok".equals(value))
		{
            stringValue = textArea.getText();
            if (stringValue.trim().length() == 0)
                stringValue = null;
            else
                stringValue = stringValue.trim();
            dialog.setVisible(false);
        }
    }
	
}