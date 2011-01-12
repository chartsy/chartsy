package org.chartsy.favorites;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.text.DefaultEditorKit;
import org.chartsy.favorites.xml.FavoritesXmlParser;
import org.chartsy.favorites.xml.FavoritesXmlWriter;
import org.chartsy.main.events.DataProviderEvent;
import org.chartsy.main.events.DataProviderListener;
import org.chartsy.main.favorites.FavoritesTreeView;
import org.chartsy.main.favorites.nodes.RootAPI;
import org.chartsy.main.favorites.nodes.RootAPINode;
import org.chartsy.main.managers.DatasetUsage;
import org.chartsy.main.utils.FileUtils;
import org.chartsy.main.utils.SerialVersion;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeOp;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public final class FavoritesComponent extends TopComponent 
	implements DataProviderListener, ExplorerManager.Provider
{

	private static FavoritesComponent instance;
	private static final String PREFERRED_ID = "FavoritesComponent";
	private ExplorerManager manager;
	private FavoritesTreeView treeView;

	private RootAPI root;

	transient private PropertyChangeListener weakRcL;
    transient private NodeListener weakNRcL;
	transient private NodeListener rcListener;

    private FavoritesComponent()
    {
        setName(NbBundle.getMessage(FavoritesComponent.class, "CTL_FavoritesComponent"));
        setToolTipText(NbBundle.getMessage(FavoritesComponent.class, "HINT_FavoritesComponent"));
		setIcon(ImageUtilities.loadImage(NbBundle.getMessage(FavoritesComponent.class, "ICON_FavoritesComponent"), true));

		putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
		putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
		putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
		putClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);

		manager = new ExplorerManager();

		initComponents();

		ActionMap map = getActionMap();
		map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
		map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
		map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
		map.put("delete", ExplorerUtils.actionDelete(manager, true));
		associateLookup(ExplorerUtils.createLookup(manager, map));

		DatasetUsage.getInstance().addDataProviderListener((DataProviderListener) this);
    }

	private void initComponents()
	{
		setOpaque(false);
		setLayout(new BorderLayout());
		treeView = FavoritesTreeView.getDefault();
		add(treeView, BorderLayout.CENTER);
	}

	public static synchronized FavoritesComponent getDefault()
	{
		if (instance == null)
			instance = new FavoritesComponent();
		return instance;
	}

	public static synchronized FavoritesComponent findInstance()
	{
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null)
		{
            Logger.getLogger(FavoritesComponent.class.getName()).warning(
				"Cannot find " + PREFERRED_ID + " component. It will not be "
				+ "located properly in the window system.");
            return getDefault();
        }
        if (win instanceof FavoritesComponent)
		{
            return (FavoritesComponent) win;
        }

        Logger.getLogger(FavoritesComponent.class.getName()).warning(
			"There seem to be multiple components with the '"
			+ PREFERRED_ID + "' ID. That is a potential source of errors and "
			+ "unexpected behavior.");
        return getDefault();
    }

	protected @Override void componentActivated()
    {
        ExplorerUtils.activateActions(manager, true);
    }

    protected @Override void componentDeactivated()
    {
        ExplorerUtils.activateActions(manager, false);
    }

	protected @Override void componentOpened()
	{
		if (isInitialized())
			initializeRootNode();
		else
			initialize();
	}

	public @Override int getPersistenceType()
	{
        return TopComponent.PERSISTENCE_ALWAYS;
    }

	protected @Override String preferredID()
	{
        return PREFERRED_ID;
    }

	public void triggerDataProviderListener(DataProviderEvent evt)
	{
		revalidate();
		repaint();
	}

	public ExplorerManager getExplorerManager()
	{
		return manager;
	}

	private boolean isInitialized()
	{
		return FileUtils.favoritesFile().exists();
	}

	private void initialize()
	{
		boolean ok = false;
		try
		{
			FileObject dest = FileUtil.createData(FileUtils.favoritesFile());
			FileUtil.copy(
				FavoritesComponent.class.getResourceAsStream("favoritesDefaults.xml"),
				dest.getOutputStream());
			ok = true;
		}
		catch (IOException ex)
		{
			Logger.getLogger(FavoritesComponent.class.getName()).log(
				Level.SEVERE, "Can't copy favorites defaults.", ex);
			ok = false;
		}
		
		if (ok)
			initializeRootNode();
	}

	private void initializeRootNode()
	{
		root = FavoritesXmlParser.getRoot();
		if (root != null)
		{
			RootAPINode node = new RootAPINode(root);
			setRootContext(node);
		}
	}

	public void setRootContext(Node rc)
	{
		Node oldRC = getExplorerManager().getRootContext();
		if (weakRcL != null)
			oldRC.removePropertyChangeListener(weakRcL);
		if (weakNRcL != null)
			oldRC.removeNodeListener(weakNRcL);
		getExplorerManager().setRootContext(rc);
		initializeWithRootContext(rc);
	}

	private void initializeWithRootContext(Node rc)
	{
		if (weakRcL == null)
			weakRcL = WeakListeners.propertyChange(rcListener(), rc);
		rc.addPropertyChangeListener(weakRcL);

		if (weakNRcL == null)
			weakNRcL = NodeOp.weakNodeListener(rcListener(), rc);
		rc.addNodeListener(weakNRcL);
	}

	private NodeListener rcListener()
	{
		if (rcListener == null)
			rcListener = new RootContextListener();
		return rcListener;
	}

	protected @Override Object writeReplace()
	{
		Logger.getLogger(FavoritesComponent.class.getName()).log
				(Level.INFO, "Saving favorites xml ...");
		boolean saved = FavoritesXmlWriter.saveFavoritesNodes(getExplorerManager().getRootContext());
		if (saved)
			Logger.getLogger(FavoritesComponent.class.getName()).log
				(Level.INFO, "Favorites xml saved.");
		else
			Logger.getLogger(FavoritesComponent.class.getName()).log
				(Level.INFO, "Couldn't save favorites xml.");

		return new ResolvableHelper();
	}

	private final class RootContextListener implements NodeListener
	{
		public void propertyChange (PropertyChangeEvent evt)
		{}
		public void nodeDestroyed(NodeEvent nodeEvent)
		{ FavoritesComponent.this.close(); }
		public void childrenRemoved(NodeMemberEvent e) {}
        public void childrenReordered(NodeReorderEvent e) {}
        public void childrenAdded(NodeMemberEvent e) {}
	}

	final static class ResolvableHelper implements Serializable
	{

		private static final long serialVersionUID = SerialVersion.APPVERSION;

		public ResolvableHelper()
		{}

		public Object readResolve()
		{
			return new FavoritesComponent();
		}

	}

}
