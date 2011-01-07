package org.chartsy.chatsy.chat.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.chartsy.chatsy.MainWindow;
import org.chartsy.chatsy.chat.ChatsyManager;

public final class MessageDialog
{

    private MessageDialog()
	{
    }

    public static void showErrorDialog(final Throwable throwable)
	{
     	 EventQueue.invokeLater(new Runnable()
		 {
			 public void run()
			 {
		        JTextPane textPane;
		        final JOptionPane pane;
		        final JDialog dlg;
		
		        TitlePanel titlePanel;
		
		        textPane = new JTextPane();
		        textPane.setFont(new Font("Dialog", Font.PLAIN, 12));
		        textPane.setEditable(false);
		
		        String message = getStackTrace(throwable);
		        textPane.setText(message);
		        titlePanel = new TitlePanel("Error", null, null, true);
		
		        final JPanel mainPanel = new JPanel();
		        mainPanel.setLayout(new BorderLayout());
		        mainPanel.add(titlePanel, BorderLayout.NORTH);
		
		        Object[] options = {"Close"};
		        pane = new JOptionPane(
					new JScrollPane(textPane),
					JOptionPane.PLAIN_MESSAGE,
					JOptionPane.OK_CANCEL_OPTION,
					ChatsyManager.getApplicationImage(),
					options,
					options[0]);
		        mainPanel.add(pane, BorderLayout.CENTER);
		
		        MainWindow mainWindow = ChatsyManager.getMainWindow();
		        dlg = new JDialog(new JFrame(), "Error", false);
		        dlg.pack();
		        dlg.setSize(600, 400);
		        dlg.setContentPane(mainPanel);
		        dlg.setLocationRelativeTo(mainWindow);
		
		        PropertyChangeListener changeListener = new PropertyChangeListener()
				{
		            public void propertyChange(PropertyChangeEvent e)
					{
		                String value = (String)pane.getValue();
		                if ("Close".equals(value))
		                    dlg.setVisible(false);
		            }
		        };		
		        pane.addPropertyChangeListener(changeListener);
		
		        dlg.setVisible(true);
		        dlg.toFront();
		        dlg.requestFocus();
			}
     	 });
    }

    public static void showAlert(final String message,final String header, final String title, final Icon icon) 
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				JTextPane textPane;
				final JOptionPane pane;
				final JDialog dlg;

				TitlePanel titlePanel;

				textPane = new JTextPane();
				textPane.setFont(new Font("Dialog", Font.PLAIN, 12));
				textPane.setEditable(false);
				textPane.setText(message);
				textPane.setBackground(Color.white);

				titlePanel = new TitlePanel(header, null, icon, true);

				final JPanel mainPanel = new JPanel();
				mainPanel.setLayout(new BorderLayout());
				mainPanel.add(titlePanel, BorderLayout.NORTH);

				Object[] options = {"Close"};
				pane = new JOptionPane(
					new JScrollPane(textPane),
					JOptionPane.PLAIN_MESSAGE, 
					JOptionPane.OK_CANCEL_OPTION,
					ChatsyManager.getApplicationImage(),
					options, options[0]);

				mainPanel.add(pane, BorderLayout.CENTER);

				dlg = new JDialog(new JFrame(), title, false);
				dlg.pack();
				dlg.setSize(300, 300);
				dlg.setContentPane(mainPanel);
				dlg.setLocationRelativeTo(ChatsyManager.getMainWindow());

				PropertyChangeListener changeListener = new PropertyChangeListener()
				{
					public void propertyChange(PropertyChangeEvent e)
					{
						String value = (String)pane.getValue();
						if ("Close".equals(value))
							dlg.setVisible(false);
					}
				};
				pane.addPropertyChangeListener(changeListener);
				
				dlg.setVisible(true);
				dlg.toFront();
				dlg.requestFocus();
			}
		});
    }

    public static JDialog showComponent(String title, String description, Icon icon, 
		JComponent comp, Component parent, int width, int height, boolean modal)
	{
        final JOptionPane pane;
        final JDialog dlg;

        TitlePanel titlePanel;

        titlePanel = new TitlePanel(title, description, icon, true);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        Object[] options = {"Close"};
        pane = new JOptionPane(
			comp,
			JOptionPane.PLAIN_MESSAGE,
			JOptionPane.OK_CANCEL_OPTION,
			ChatsyManager.getApplicationImage(),
			options,
			options[0]);

        mainPanel.add(pane, BorderLayout.CENTER);

        JOptionPane p = new JOptionPane();
        dlg = p.createDialog(parent, title);
        dlg.setModal(modal);

        dlg.pack();
        dlg.setSize(width, height);
        dlg.setResizable(true);
        dlg.setContentPane(mainPanel);
        dlg.setLocationRelativeTo(parent);

        PropertyChangeListener changeListener = new PropertyChangeListener()
		{
            public void propertyChange(PropertyChangeEvent e) {
                String value;
                try
				{
                    value= (String)pane.getValue();
                    if ("Close".equals(value))
                        dlg.setVisible(false);
                } 
				catch (Exception ex)
				{
                }                
            }
        };
        pane.addPropertyChangeListener(changeListener);

        dlg.setVisible(true);
        dlg.toFront();
        dlg.requestFocus();
        return dlg;
    }

    public static String getStackTrace(Throwable aThrowable)
	{
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    public static String getCustomStackTrace(String heading, Throwable aThrowable)
	{
        final StringBuilder result = new StringBuilder(heading);
        result.append(aThrowable.toString());
        final String lineSeperator = System.getProperty("line.separator");
        result.append(lineSeperator);

        StackTraceElement[] stackTrace = aThrowable.getStackTrace();
        final List<StackTraceElement> traceElements = Arrays.asList(stackTrace);
        for (StackTraceElement traceElement : traceElements)
		{
            result.append(traceElement);
            result.append(lineSeperator);
        }
        return result.toString();
    }

}