package org.chartsy.chatsy.chat.component;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;

public class ConfirmDialog extends JPanel
{

    private JLabel message;
    private JLabel iconLabel;
    private JButton yesButton;
    private JButton noButton;
    private ConfirmListener listener = null;
    private JDialog dialog;

    public ConfirmDialog()
	{
        setLayout(new GridBagLayout());

        message = new JLabel();
        iconLabel = new JLabel();
        yesButton = new JButton();
        noButton = new JButton();

        add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(message, new GridBagConstraints(1, 0, 4, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        add(yesButton, new GridBagConstraints(3, 1, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(noButton, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        yesButton.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                if (listener != null)
				{
                    listener.yesOption();
                    listener = null;
                }
                dialog.dispose();
            }
        });

        noButton.addActionListener(new ActionListener()
		{
            public void actionPerformed(ActionEvent actionEvent)
			{
                dialog.dispose();
            }
        });


    }

    public void showConfirmDialog(JFrame parent, String title, String text, 
		String yesText, String noText, Icon icon)
	{
        message.setText("<html><body>" + text + "</body></html>");
        iconLabel.setIcon(icon);

		yesButton.setText(yesText);
		noButton.setText(noText);

        dialog = new JDialog(parent, title, false);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        dialog.addWindowListener(new WindowAdapter()
		{
            public void windowClosed(WindowEvent windowEvent)
			{
                if (listener != null)
                    listener.noOption();
            }
        });
    }

    public void setDialogSize(int width, int height)
	{
        dialog.setSize(width, height);
        dialog.pack();
        dialog.validate();
    }

    public void setConfirmListener(ConfirmListener listener)
	{
        this.listener = listener;
    }

    public interface ConfirmListener
	{
        void yesOption();
        void noOption();
    }
	
}
