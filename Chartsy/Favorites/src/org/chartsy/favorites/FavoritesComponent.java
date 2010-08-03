package org.chartsy.favorites;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.text.DefaultEditorKit;
import org.chartsy.main.events.DataProviderEvent;
import org.chartsy.main.events.DataProviderListener;
import org.chartsy.main.favorites.FavoritesTreeView;
import org.chartsy.main.favorites.nodes.FolderAPI;
import org.chartsy.main.favorites.nodes.FolderAPINode;
import org.chartsy.main.favorites.nodes.RootAPI;
import org.chartsy.main.favorites.nodes.RootAPINode;
import org.chartsy.main.favorites.nodes.StockAPI;
import org.chartsy.main.favorites.nodes.StockAPINode;
import org.chartsy.main.managers.DataProviderManager;
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
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

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

	private RootAPI root;

	transient private PropertyChangeListener weakRcL;
    transient private NodeListener weakNRcL;
	transient private NodeListener rcListener;

    private FavoritesComponent()
    {
        setName(NbBundle.getMessage(FavoritesComponent.class, "CTL_FavoritesComponent"));
        setToolTipText(NbBundle.getMessage(FavoritesComponent.class, "HINT_FavoritesComponent"));
		setIcon(ImageUtilities.loadImage(NbBundle.getMessage(FavoritesComponent.class, "ICON_FavoritesComponent"), true));
		
		initComponents();

		manager = new ExplorerManager();

		ActionMap map = getActionMap();
		map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
		map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
		map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
		map.put("delete", ExplorerUtils.actionDelete(manager, true));
		associateLookup(ExplorerUtils.createLookup(manager, map));

		List<String> providers = DataProviderManager.getDefault().getDataProviders();
		for (String provider : providers)
			DataProviderManager.getDefault().getDataProvider(provider).addDatasetListener((DataProviderListener) this);
    }

	private void initComponents()
	{
		setLayout(new BorderLayout());
		add(FavoritesTreeView.getDefault(), BorderLayout.CENTER);
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

	public @Override void open()
	{
		Mode mode = WindowManager.getDefault().findMode("explorer");
		if (mode != null)
		{
			mode.setBounds(new Rectangle(168, 242, 386, 373));
			mode.dockInto(this);
			super.open();
		}
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

	public static class FavoritesXmlParser
	{

		public static RootAPI getRoot()
		{
			RootAPI root = null;

			try
			{
				InputSource src
					= new InputSource(
					new FileInputStream(FileUtils.favoritesFile()));

				FavoritesXmlHandler handler = new FavoritesXmlHandler();

				XMLReader reader = XMLReaderFactory.createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");
				if (reader != null)
				{
					reader.setContentHandler(handler);
					reader.parse(src);
					root = handler.getRootAPI();
				}
			}
			catch (IOException ex)
			{
				Logger.getLogger(FavoritesComponent.class.getName()).log(
					Level.SEVERE, "", ex);
			}
			catch (SAXException ex)
			{
				Logger.getLogger(FavoritesComponent.class.getName()).log(
					Level.SEVERE, "", ex);
			}

			return root;
		}

	}

	public static class FavoritesXmlHandler extends DefaultHandler
	{

		private static final int FAVORITES		= "favorites".hashCode();

		private static final int FOLDER			= "folder".hashCode();
		private static final int NAME			= "name".hashCode();

		private static final int STOCK			= "stock".hashCode();
		private static final int COMPANY		= "companyName".hashCode();
		private static final int SYMBOL			= "symbol".hashCode();
		private static final int EXCHANGE		= "exchange".hashCode();
		private static final int DATAPROVIDER	= "dataProvider".hashCode();

		private RootAPI root;
		private Stack stack;
		private boolean isStackReadyForText;
		private boolean isInsideFolder;

		public FavoritesXmlHandler()
		{
			stack = new Stack();
			isStackReadyForText = false;
			isInsideFolder = false;
		}

		public RootAPI getRootAPI()
		{
			return root;
		}

		public @Override void startElement
			(String uri, String localName, String qName, Attributes attribs)
		{
			isStackReadyForText = false;
			int identifier = localName.hashCode();

			if (identifier == FAVORITES)
			{
				stack.push(new RootAPI());
				isInsideFolder = false;
			}
			else if(identifier == FOLDER)
			{
				stack.push(new FolderAPI());
				isInsideFolder = true;
			}
			else if(identifier == STOCK)
				stack.push(new StockAPI());
			else if (identifier == NAME
				|| identifier == COMPANY
				|| identifier == SYMBOL
				|| identifier == EXCHANGE
				|| identifier == DATAPROVIDER)
			{
				stack.push(new StringBuilder());
				isStackReadyForText = true;
			}
			else
			{
				// do nothing
			}
		}

		public @Override void endElement
			(String uri, String localName, String qName)
		{
			isStackReadyForText = false;

			Object tmp = stack.pop();
			int identifier = localName.hashCode();

			if (identifier == FAVORITES)
				root = (RootAPI) tmp;
			else if(identifier == FOLDER)
			{
				((RootAPI) stack.peek()).addFolder((FolderAPI) tmp);
				isInsideFolder = false;
			}
			else if(identifier == STOCK)
			{
				if (isInsideFolder)
					((FolderAPI)stack.peek()).addStock((StockAPI) tmp);
				else
					((RootAPI)stack.peek()).addStock((StockAPI) tmp);
			}
			else if (identifier == NAME)
				((FolderAPI)stack.peek()).setDisplayName(decode(tmp.toString()));
			else if (identifier == COMPANY)
				((StockAPI)stack.peek()).setCompanyName(decode(tmp.toString()));
			else if (identifier == SYMBOL)
				((StockAPI)stack.peek()).setSymbol(decode(tmp.toString()));
			else if (identifier == EXCHANGE)
				((StockAPI)stack.peek()).setExchange(decode(tmp.toString()));
			else if (identifier == DATAPROVIDER)
				((StockAPI)stack.peek()).setDataProviderName(decode(tmp.toString()));
			else
				stack.push(tmp);
		}

		public @Override void characters
			(char[] data, int start, int length)
		{
			if (isStackReadyForText == true)
			{
				((StringBuilder)stack.peek()).append(data, start, length);
			}
		}

		private String decode(String text)
		{
			try
			{
				return URLDecoder.decode(text, "UTF-8");
			}
			catch (UnsupportedEncodingException ex)
			{ 
				Logger.getLogger(FavoritesComponent.class.getName()).log(Level.SEVERE, "", ex);
			}
			return text;
		}

	}

	public static class FavoritesXmlWriter
	{

		public FavoritesXmlWriter()
		{}

		public static boolean saveFavoritesNodes(Node root)
		{
			if (!(root instanceof RootAPINode))
				return false;

			boolean saved = false;

			try
			{
				FileUtils.removeFile(FileUtils.favoritesFile().getAbsolutePath());
				FileUtils.createFile(FileUtils.favoritesFile().getAbsolutePath());

				FileOutputStream fileOutputStream 
					= new FileOutputStream(
					FileUtils.favoritesFile().getAbsolutePath());
				BufferedOutputStream bufferedOutputStream
					= new BufferedOutputStream(fileOutputStream);
				OutputStreamWriter out
					= new OutputStreamWriter(bufferedOutputStream, "UTF-8");

				out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");

				out.write("<favorites>\r\n");

				// write root folders
				for (Node node : root.getChildren().getNodes())
				{
					if (node instanceof FolderAPINode)
					{
						FolderAPI folder = ((FolderAPINode) node).getFolder();
						// open folder node
						out.write("\t<folder>\r\n");

						// folder name
						out.write("\t\t<name>");
						out.write(encode(folder.getDisplayName()));
						out.write("</name>\r\n");

						// write stocks in folder
						for (Node child : node.getChildren().getNodes())
						{
							if (child instanceof StockAPINode)
							{
								StockAPI stock = ((StockAPINode) child).getStock();
								// open stock node
								out.write("\t\t<stock>\r\n");

								// company name
								out.write("\t\t\t<companyName>");
								out.write(encode(stock.getCompanyName()));
								out.write("</companyName>\r\n");

								// symbol
								out.write("\t\t\t<symbol>");
								out.write(encode(stock.getSymbol()));
								out.write("</symbol>\r\n");

								// exchange
								out.write("\t\t\t<exchange>");
								out.write(encode(stock.getExchange()));
								out.write("</exchange>\r\n");

								// data provider
								out.write("\t\t\t<dataProvider>");
								out.write(encode(stock.getDataProviderName()));
								out.write("</dataProvider>\r\n");

								// close stock node
								out.write("\t\t</stock>\r\n");
							}
						}

						// close folder node
						out.write("\t</folder>\r\n");
					}
				}

				// write root stocks
				for (Node node : root.getChildren().getNodes())
				{
					if (node instanceof StockAPINode)
					{
						StockAPI stock = ((StockAPINode) node).getStock();
						// open stock node
						out.write("\t<stock>\r\n");

						// company name
						out.write("\t\t<companyName>");
						out.write(encode(stock.getCompanyName()));
						out.write("</companyName>\r\n");

						// symbol
						out.write("\t\t<symbol>");
						out.write(encode(stock.getSymbol()));
						out.write("</symbol>\r\n");

						// exchange
						out.write("\t\t<exchange>");
						out.write(encode(stock.getExchange()));
						out.write("</exchange>\r\n");

						// data provider
						out.write("\t\t<dataProvider>");
						out.write(encode(stock.getDataProviderName()));
						out.write("</dataProvider>\r\n");

						// close stock node
						out.write("\t</stock>\r\n");
					}
				}

				out.write("</favorites>\r\n");

				out.flush();
				out.close();

				saved = true;
			}
			catch (IOException ex)
			{
				Logger.getLogger(FavoritesComponent.class.getName()).log(
					Level.SEVERE, "", ex);
				saved = false;
			}

			if (!saved)
				FileUtils.removeFile(FileUtils.favoritesFile().getAbsolutePath());

			return saved;
		}

		private static String encode(String text)
		{
			try
			{
				return URLEncoder.encode(text, "UTF-8");
			}
			catch (UnsupportedEncodingException ex)
			{
				Logger.getLogger(FavoritesComponent.class.getName()).log(
					Level.SEVERE, "", ex);
			}
			return text;
		}
		
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
