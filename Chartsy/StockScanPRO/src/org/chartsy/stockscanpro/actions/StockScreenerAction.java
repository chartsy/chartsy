package org.chartsy.stockscanpro.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.chartsy.main.RegisterForm;
import org.chartsy.main.features.FeaturesPanel;
import org.chartsy.main.managers.ProxyManager;
import org.chartsy.main.utils.DesktopUtil;
import org.chartsy.stockscanpro.StockScreenerComponent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

public final class StockScreenerAction implements ActionListener
{

	private final static RequestProcessor RP = new RequestProcessor("interruptible tasks", 1, true);
	private Preferences preferences;
	private String username = "";
	private String password = "";
	private boolean isStockScanUser = false;
	private int userId = 0;

    public void actionPerformed(ActionEvent e)
    {
		if (!StockScreenerComponent.findInstance().isOpened())
		{
			if (!checkRegistration())
			{
				RegisterForm register = new RegisterForm(new JFrame(), true);
				register.setLocationRelativeTo(WindowManager.getDefault().getMainWindow());
				register.setVisible(true);
			}
			else
			{
				username = preferences.get("username", "");
				password = preferences.get("password", "");
				checkStockScanUser();
			}
		} 
		else
		{
			StockScreenerComponent.findInstance().requestActive();
		}
    }

	private boolean checkRegistration()
	{
		preferences = NbPreferences.root().node("/org/chartsy/register");
		boolean registred = preferences.getBoolean("registred", false);
		return registred;
	}

	private void checkStockScanUser()
	{
		final Preferences prefs = NbPreferences.root().node("/org/chartsy/stockscanpro");
		isStockScanUser = prefs.getBoolean("stockscanproregistred", false);
		
		final RequestProcessor.Task task = RP.create(new Runnable()
		{
			public void run()
			{
				userId = getUserId();
				isStockScanUser = userId != 0;
				if (isStockScanUser)
				{
					prefs.putBoolean("stockscanproregistred", isStockScanUser);
					prefs.putInt("stockscanprouser", userId);
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							FeaturesPanel.getDefault().hideBanners();
						}
					});
				}
			}
		});

		final ProgressHandle handle = ProgressHandleFactory.createHandle("Cheching registration", task);
		task.addTaskListener(new TaskListener()
		{
			public void taskFinished(Task task)
			{
				handle.finish();
				if (isStockScanUser)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							openComponent();
						}
					});
				} 
				else
				{
					JLabel label = new JLabel(NbBundle.getMessage(StockScreenerAction.class, "Registration_MSG"));
					label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					label.addMouseListener(new MouseAdapter()
					{
						public @Override void mouseClicked(MouseEvent e)
						{
							try { DesktopUtil.browse(NbBundle.getMessage(StockScreenerAction.class, "MrSwing_URL")); }
							catch (Exception ex) { Exceptions.printStackTrace(ex); }
						}
					});

					DialogDescriptor descriptor = new DialogDescriptor(label, "Register");
					descriptor.setMessageType(DialogDescriptor.INFORMATION_MESSAGE);
					descriptor.setOptions(new Object[] 
					{
						DialogDescriptor.OK_OPTION,
						DialogDescriptor.CANCEL_OPTION
					});

					Object ret = DialogDisplayer.getDefault().notify(descriptor);
					if (ret.equals(DialogDescriptor.OK_OPTION))
					{
						try { DesktopUtil.browse(NbBundle.getMessage(StockScreenerAction.class, "MrSwing_URL")); }
						catch (Exception ex) { Exceptions.printStackTrace(ex); }
					}
				}
			}
		});

		if (!isStockScanUser)
		{
			handle.start();
			task.schedule(0);
		} 
		else
		{
			openComponent();
		}
	}

	private void openComponent()
	{
		StockScreenerComponent component = new StockScreenerComponent();
		component.open();
		component.requestActive();
	}

	private int getUserId()
	{
		HttpClient client = ProxyManager.getDefault().httpClient();
		GetMethod method = new GetMethod(
			NbBundle.getMessage(StockScreenerAction.class, "StockScanPRO_URL"));
		int id = 0;

		try
		{
			method.setQueryString(new NameValuePair[]
			{
				new NameValuePair("option", "com_chartsy"),
				new NameValuePair("view", "checkregistration"),
				new NameValuePair("format", "raw"),
				new NameValuePair("username", username),
				new NameValuePair("passwd", password)
			});

			client.executeMethod(method);
			id = Integer.parseInt(method.getResponseBodyAsString());
		}
		catch (IOException ex)
		{
			Exceptions.printStackTrace(ex);
		}
		finally
		{
			method.releaseConnection();
		}
		
		return id;
	}

}
