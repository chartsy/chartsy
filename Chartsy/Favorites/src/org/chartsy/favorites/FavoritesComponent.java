package org.chartsy.favorites;

import java.awt.BorderLayout;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.ObjectStreamException;
import java.util.Collections;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;
import org.chartsy.favorites.nodes.FavoritesNode;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.TreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeOp;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Viorel
 */
public class FavoritesComponent extends TopComponent implements Runnable, ExplorerManager.Provider
{

    private static final String PREFERRED_ID = "FavoritesTopComponent";
    private static final Logger LOG = Logger.getLogger(FavoritesComponent.class.getPackage().getName());
    private static final DataObject[] needToSelect = new DataObject[1];
    static transient FavoritesComponent instance;

    transient protected TreeView view;
    transient private PropertyChangeListener propertyChangeListener;
    transient private NodeListener weakNodeListener;
    transient private NodeListener nodeListener;
    transient private boolean valid = true;
    private ExplorerManager manager;

    private FavoritesComponent()
    {
        setName(NbBundle.getMessage(FavoritesComponent.class, "CTL_FavoritesTopComponent"));
        setToolTipText(NbBundle.getMessage(FavoritesComponent.class, "HINT_FavoritesTopComponent"));

        this.manager = new ExplorerManager();

        ActionMap map = this.getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true));

        associateLookup(ExplorerUtils.createLookup(manager, map));
    }

    public @Override HelpCtx getHelpCtx()
    {
        return new HelpCtx(FavoritesComponent.class);
    }

    public ExplorerManager getExplorerManager()
    {
        return manager;
    }

    protected @Override String preferredID()
    {
        return PREFERRED_ID;
    }

    protected @Override void componentShowing()
    {
        super.componentShowing();

        if (view == null)
        {
            view = initGui();

            view.getAccessibleContext().setAccessibleName(NbBundle.getMessage(FavoritesComponent.class, "ACSN_ExplorerBeanTree"));
            view.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(FavoritesComponent.class, "ACSD_ExplorerBeanTree"));
        }

        run();
    }

    protected TreeView initGui()
    {
        TreeView treeView = new BeanTreeView();
        treeView.setRootVisible(false);
        treeView.setDragSource(true);
        treeView.setUseSubstringInQuickSearch(true);
        setLayout(new BorderLayout());
        add(treeView);
        return treeView;
    }

    protected @Override void componentActivated()
    {
        ExplorerUtils.activateActions(manager, true);
    }

    protected @Override void componentDeactivated()
    {
        ExplorerUtils.activateActions(manager, false);
    }

    @SuppressWarnings("deprecation")
    public @Override void requestFocus()
    {
        super.requestFocus();
        if (view != null)
        {
            view.requestFocus();
        }
    }

    @SuppressWarnings("deprecation")
    public @Override boolean requestFocusInWindow()
    {
        super.requestFocusInWindow();
        if (view != null)
        {
            return view.requestFocusInWindow();
        }
        else
        {
            return false;
        }
    }

    public void setRootContext(Node rootContext)
    {
        Node oldRootContext = getExplorerManager().getRootContext();
        if (propertyChangeListener != null)
        {
            oldRootContext.removePropertyChangeListener(propertyChangeListener);
        }
        if (weakNodeListener != null)
        {
            oldRootContext.removeNodeListener(weakNodeListener);
        }
        getExplorerManager().setRootContext(rootContext);
        initializeWithRootContext(rootContext);
    }

    public Node getRootContext()
    {
        return getExplorerManager().getRootContext();
    }

    public void run()
    {
        if (!valid)
        {
            valid = true;
            validateRootContext();
        }
    }

    protected void updateTitle()
    {
        //setName(getExplorerManager().getRootContext().getDisplayName());
    }

    private NodeListener rcListener()
    {
        if (nodeListener == null)
            nodeListener = new RootContextListener();
        return nodeListener;
    }

    private void initializeWithRootContext(Node rootContext)
    {
        setIcon(rootContext.getIcon(BeanInfo.ICON_COLOR_16x16));
        //setToolTipText(rootContext.getDisplayName());
        //setName(rootContext.getDisplayName());
        //updateTitle();

        if (propertyChangeListener == null)
        {
            propertyChangeListener = WeakListeners.propertyChange(rcListener(), rootContext);
        }
        rootContext.addPropertyChangeListener(propertyChangeListener);

        if (weakNodeListener == null)
        {
            weakNodeListener = NodeOp.weakNodeListener(rcListener(), rootContext);
        }
        rootContext.addNodeListener(weakNodeListener);
    }

    protected final void scheduleValidation()
    {
        valid = false;
        SwingUtilities.invokeLater(this);
    }

    public @Override void setName(String name)
    {
        super.setName(name);
        if (view != null)
        {
            view.getAccessibleContext().setAccessibleName(name);
        }
    }

    public @Override void setToolTipText(String text)
    {
        super.setToolTipText(text);
        if (view != null)
        {
            view.getAccessibleContext().setAccessibleDescription(text);
        }
    }

    private final class RootContextListener implements NodeListener
    {

        public void propertyChange(PropertyChangeEvent evt)
        {
            String propName = evt.getPropertyName();
            Object source = evt.getSource();

            Node node = (Node) source;
            if (Node.PROP_DISPLAY_NAME.equals(propName) || Node.PROP_NAME.equals(propName))
            {
                setName(node.getDisplayName());
            }
            else if (Node.PROP_ICON.equals(propName))
            {
                setIcon(node.getIcon(BeanInfo.ICON_COLOR_16x16));
            }
            else if (Node.PROP_SHORT_DESCRIPTION.equals(propName))
            {
                setToolTipText(node.getShortDescription());
            }
        }

        public void nodeDestroyed(NodeEvent nodeEvent)
        {
            FavoritesComponent.this.close();
        }

        public void childrenRemoved(NodeMemberEvent e) {}
        public void childrenReordered(NodeReorderEvent e) {}
        public void childrenAdded(NodeMemberEvent e) {}

    }

    public static synchronized FavoritesComponent getDefault()
    {
        if (instance == null)
        {
            instance = new FavoritesComponent();
            instance.scheduleValidation();
        }
        return instance;
    }

    public static synchronized FavoritesComponent findDefault()
    {
        if (instance == null)
        {
            TopComponent topComponent = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
            if (instance == null)
            {
                LOG.log(Level.WARNING, null,
                        new IllegalStateException("Can not find project component for its ID. Returned " +
                        topComponent));
                instance = new FavoritesComponent();
                instance.scheduleValidation();
            }
        }
        return instance;
    }
    
    public @Override int getPersistenceType()
    {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    private static Node findClosestNode(DataObject obj, Node start, boolean useLogicalViews)
    {
        DataObject original = obj;

        Stack<DataObject> stack = new Stack<DataObject>();
        while (obj != null)
        {
            stack.push(obj);
            DataObject tmp = obj.getFolder();
            if (tmp == null)
            {
                FileObject fo = FileUtil.getArchiveFile(obj.getPrimaryFile());
                if (fo != null)
                {
                    try
                    {
                        obj = DataObject.find(fo);
                        stack.pop();
                    }
                    catch (DataObjectNotFoundException ex)
                    {
                        obj = null;
                    }
                }
                else
                {
                    obj = null;
                }
            }
            else
            {
                obj = tmp;
            }
        }

        Node current = start;
        int topIdx = stack.size();
        int i = 0;
        while (i < topIdx)
        {
            Node n = findDataObject(current, stack.get(i));
            if (n != null)
            {
                current = n;
                topIdx = i;
                i = 0;
            }
            else
            {
                i++;
            }
        }

        if (!check(current, original) && useLogicalViews)
        {
            Node[] children = current.getChildren().getNodes();
            for (int j = 0; j < children.length; j++)
            {
                Node child = children[j];
                Node n = selectInLogicalViews(original, child);
                if (check(n, original))
                {
                    current = n;
                    break;
                }
            }
        }
        return current;
    }

    private static Node selectInLogicalViews(DataObject original, Node start)
    {
        return start;
    }

    private boolean selectNode(DataObject obj, Node root)
    {
        Node node = findClosestNode(obj, root, true);
        try
        {
            getExplorerManager().setSelectedNodes(new Node[] { node });
        }
        catch (PropertyVetoException ex)
        {
            throw new IllegalStateException();
        }
        return check(node, obj);
    }

    @SuppressWarnings("cast")
    private static boolean check(Node node, DataObject obj)
    {
        DataObject dObj = (DataObject)node.getLookup().lookup(DataObject.class);
        if (obj == dObj)
        {
            return true;
        }
        if (dObj instanceof DataShadow && obj == ((DataShadow)dObj).getOriginal())
        {
            return true;
        }
        return false;
    }

    @SuppressWarnings("cast")
    private static Node findDataObject(Node node, DataObject obj)
    {
        Node[] arr = node.getChildren().getNodes(true);
        for (int i = 0; i < arr.length; i++)
        {
            DataShadow ds = (DataShadow) arr[i].getCookie(DataShadow.class);
            if ((ds != null) && (obj == ds.getOriginal()))
            {
                return arr[i];
            }
            else
            {
                DataObject o = (DataObject) arr[i].getCookie(DataObject.class);
                if ((o != null) && (obj == o))
                {
                    return arr[i];
                }
            }
        }
        return null;
    }

    protected void validateRootContext()
    {
        Node projectsRc = FavoritesNode.getNode();
        setRootContext(projectsRc);
    }

    protected boolean containsNode(DataObject obj)
    {
        Node node = findClosestNode(obj, getExplorerManager().getRootContext(), true);
        return check(node, obj);
    }

    protected void doSelectNode(DataObject obj)
    {
        if (obj == null)
            return;

        Node root = getExplorerManager().getRootContext();
        if (selectNode (obj, root))
        {
            requestActive();
            StatusDisplayer.getDefault().setStatusText("");
        }
        else
        {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(FavoritesComponent.class,"MSG_NodeNotFound"));
            FileObject file = chooseFileObject(obj.getPrimaryFile());
            if (file == null)
            {
                return;
            }

            try {
                Actions.Add.addToFavorites(Collections.singletonList(DataObject.find(file)));
            } catch (DataObjectNotFoundException e) {
                LOG.log(Level.WARNING, null, e);
            }
        }
    }

    private static FileObject chooseFileObject(FileObject file)
    {
        FileObject retVal = null;
        File chooserSelection = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle(NbBundle.getBundle(Actions.class).getString("CTL_DialogTitle"));
        chooser.setApproveButtonText(NbBundle.getBundle(Actions.class).getString("CTL_ApproveButtonText"));
        chooser.setSelectedFile(FileUtil.toFile(file));
        int option = chooser.showOpenDialog(WindowManager.getDefault().getMainWindow()); // Show the chooser
        if (option == JFileChooser.APPROVE_OPTION)
        {
            chooserSelection = chooser.getSelectedFile();
            File selectedFile = FileUtil.normalizeFile(chooserSelection);
            
            if (!selectedFile.exists())
            {
                if ((selectedFile.getParentFile() != null) && selectedFile.getParentFile().exists())
                {
                    if (selectedFile.getName().equals(selectedFile.getParentFile().getName()))
                    {
                        selectedFile = selectedFile.getParentFile();
                    }
                }
            }
            
            if (!selectedFile.exists())
            {
                String message = NbBundle.getMessage(Actions.class,"ERR_FileDoesNotExist",selectedFile.getPath());
                String title = NbBundle.getMessage(Actions.class,"ERR_FileDoesNotExistDlgTitle");
                DialogDisplayer.getDefault().notify
                (new NotifyDescriptor(message,title,NotifyDescriptor.DEFAULT_OPTION,
                NotifyDescriptor.INFORMATION_MESSAGE, new Object[] { NotifyDescriptor.CLOSED_OPTION },
                NotifyDescriptor.OK_OPTION));
            }
            else
            {
                retVal = FileUtil.toFileObject(selectedFile);
                assert retVal != null;
            }
        }
        return retVal;
    }

    public Object readResolve() throws ObjectStreamException
    {
        getDefault().scheduleValidation();
        return getDefault();
    }

}
