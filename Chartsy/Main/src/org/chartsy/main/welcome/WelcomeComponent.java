package org.chartsy.main.welcome;

import java.awt.BorderLayout;
import java.lang.ref.WeakReference;
import javax.swing.JComponent;
import org.chartsy.main.welcome.ui.StartPageContent;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public class WelcomeComponent extends TopComponent
{

	static final long serialVersionUID = 6021472310161712674L;
	private static WeakReference<WelcomeComponent> component =
		new WeakReference<WelcomeComponent>(null);

	private JComponent content;
	private boolean initialized = false;

	private WelcomeComponent()
	{
		setLayout(new BorderLayout());

		setName("Welcome");
        setToolTipText("Welcome");
        setDisplayName("Welcome");
        setHtmlDisplayName("Welcome");

		content = null;
		initialized = false;
		
		putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
	}

	private void doInitialize()
	{
		if (null == content)
		{
			content = new StartPageContent();
			add(content, BorderLayout.CENTER);
			setFocusable(false);
		}
	}

	@Override protected String preferredID()
	{
		return "WelcomeComponent";
	}

	@Override public int getPersistenceType()
	{
		return TopComponent.PERSISTENCE_ONLY_OPENED;
	}

	public static WelcomeComponent createComp()
	{
		WelcomeComponent wc = component.get();
		if (wc == null)
		{
			wc = new WelcomeComponent();
			component = new WeakReference<WelcomeComponent>(wc);
		}
		return wc;
	}

	public static WelcomeComponent findComp()
	{
		WelcomeComponent wc = component.get();
		if (wc == null)
		{
			TopComponent tc = WindowManager.getDefault().findTopComponent("Welcome");
			if (tc != null)
			{
				if (tc instanceof WelcomeComponent)
				{
					wc = (WelcomeComponent) tc;
					component = new WeakReference<WelcomeComponent>(wc);
				}
				else
				{
					IllegalStateException exc = new IllegalStateException
                    ("Incorrect settings file. Unexpected class returned."
                    + " Expected:" + WelcomeComponent.class.getName()
                    + " Returned:" + tc.getClass().getName());
					ErrorManager.getDefault().notify(
						ErrorManager.INFORMATIONAL, exc);
					wc = WelcomeComponent.createComp();
				}
			}
			else
			{
				wc = WelcomeComponent.createComp();
			}
		}
		return wc;
	}

	@Override protected void componentShowing()
	{
		if (!initialized)
		{
			initialized = true;
			doInitialize();
			Feeds.getDefault().start();
		}
		if (null != content && getComponentCount() == 0)
		{
			add(content, BorderLayout.CENTER);
		}
		super.componentShowing();
		setActivatedNodes(new Node[] {});
	}

	@Override protected void componentOpened()
	{
		super.componentOpened();
		if (!initialized)
		{
			initialized = true;
			doInitialize();
			Feeds.getDefault().start();
		}
		if (null != content && getComponentCount() == 0)
		{
			add(content, BorderLayout.CENTER);
		}
	}

	@Override protected void componentHidden()
	{
		super.componentHidden();
		if (null != content)
		{
			remove(content);
		}
	}

	@Override public void requestFocus()
	{
		if (null != content)
			content.requestFocus();
	}

	@Override public boolean requestFocusInWindow()
	{
		if (null != content)
			return content.requestFocusInWindow();
		return super.requestFocusInWindow();
	}

}
