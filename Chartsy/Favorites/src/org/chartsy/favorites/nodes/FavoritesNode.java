package org.chartsy.favorites.nodes;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.api.queries.VisibilityQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.ChangeableDataFilter;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Viorel
 */
public final class FavoritesNode extends FilterNode implements Index
{

    private static Node node;

    private FavoritesNode(Node node)
    {
        super(node, new FavoritesChildren(node));
    }

    @Override
    public <T extends Node.Cookie> T getCookie(Class<T> cl) {
        if (cl == Index.class) {
            return cl.cast(this);
        } else {
            return super.getCookie(cl);
        }
    }

    public int getNodesCount() {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            return ind.getNodesCount();
        } else {
            return 0;
        }
    }

    public Node[] getNodes() {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            return ind.getNodes();
        } else {
            return new Node [] {};
        }
    }

    public int indexOf(final Node node) {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            if (node instanceof FavoritesNode.ProjectFilterNode) {
                FavoritesNode.ProjectFilterNode fn = (FavoritesNode.ProjectFilterNode) node;
                int i = ind.indexOf(fn.getOriginal());
                return i;
            } else {
                int i = ind.indexOf(node);
                return i;
            }
        } else {
            return -1;
        }
    }

    public void reorder() {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            ind.reorder();
        }
    }

    public void reorder(int[] perm) {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            ind.reorder(perm);
        }
    }

    public void move(int x, int y) {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            ind.move(x,y);
        }
    }

    public void exchange(int x, int y) {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            ind.exchange(x,y);
        }
    }

    public void moveUp(int x) {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            ind.moveUp(x);
        }
    }

    public void moveDown(int x) {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            ind.moveDown(x);
        }
    }

    public void addChangeListener(final ChangeListener chl) {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            ind.addChangeListener(chl);
        }
    }

    public void removeChangeListener(final ChangeListener chl) {
        Index ind = getOriginal().getCookie(Index.class);
        if (ind != null) {
            ind.removeChangeListener(chl);
        }
    }

    @Override
    public Image getIcon (int type) {
        return ImageUtilities.loadImage("org/chartsy/favorites/resources/actionSelect.png"); // NOI18N
    }

    @Override
    public Image getOpenedIcon (int type) {
        return ImageUtilities.loadImage("org/chartsy/favorites/resources/actionSelect.png"); // NOI18N
    }

    @Override
    public boolean canCopy () {
        return false;
    }

    @Override
    public boolean canCut () {
        return false;
    }

    @Override
    public boolean canRename () {
        return false;
    }

    @SuppressWarnings("deprecation")
    public static DataFolder getFolder () {
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("FavoritesDefaults");
        DataFolder folder = DataFolder.findFolder(fo);
        return folder;
    }

    /** Getter for default filter node.
     */
    public static synchronized Node getNode() {
        if (node == null) {
            node = new FavoritesNode(getFolder().getNodeDelegate());
        }
        return node;
    }

    /** Get name of home directory. Used from layer.
     */
    public static URL getHome ()
    throws FileStateInvalidException, MalformedURLException {
        String s = System.getProperty("user.home"); // NOI18N

        File home = new File (s);
        home = FileUtil.normalizeFile (home);

        return home.toURI ().toURL ();
    }

    /** Finds file for a given node
     */
    static File fileForNode (Node n) {
        DataObject obj = n.getCookie (DataObject.class);
        if (obj == null) return null;

        return FileUtil.toFile (
            obj.getPrimaryFile()
        );
    }

    @Override
    public Handle getHandle () {
        return new RootHandle ();
    }

    private static class RootHandle implements Node.Handle
    {

        /** Return a node for the current project.
        */
        public Node getNode () {
            return FavoritesNode.getNode ();
        }
    }
    
    static class VisQ 
    implements DataFilter.FileBased, ChangeableDataFilter, ChangeListener {
        public static final VisQ DEFAULT = new VisQ();

        private ChangeListener weak;
        private ChangeSupport support = new ChangeSupport(this);

        VisQ() {
            weak = org.openide.util.WeakListeners.change(this, VisibilityQuery.getDefault());
            VisibilityQuery.getDefault().addChangeListener(weak);
        }
        
        public boolean acceptFileObject(FileObject fo) 
        {
            return VisibilityQuery.getDefault().isVisible(fo);
        }

        public boolean acceptDataObject(DataObject obj) {
            return acceptFileObject(obj.getPrimaryFile());
        }

        public void addChangeListener(ChangeListener listener) {
            support.addChangeListener(listener);
        }

        public void removeChangeListener(ChangeListener listener) {
            support.removeChangeListener(listener);
        }

        public void stateChanged(ChangeEvent e) {
            support.fireChange();
        }
        
    }

    private static class FavoritesChildren extends FilterNode.Children
    {

        public FavoritesChildren(Node node)
        {
            super(node);
        }

        @Override
        protected Node[] createNodes(Node node)
        {
            org.openide.nodes.Children children = Children.LEAF;
            DataObject object = node.getLookup().lookup(DataObject.class);
            
            DataFolder folder = node.getLookup().lookup(DataFolder.class);
            if (folder != null)
            {
                children = new FavoritesChildren(new FilterNode(node, folder.createNodeChildren(new VisQ())));
            }
            else
            {
                if (node.isLeaf())
                {
                    children = org.openide.nodes.Children.LEAF;
                }
                else
                {
                    children = new FavoritesChildren(node);
                }
            }

            return new Node[] { new ProjectFilterNode(node, children) };
        }

    }

    private static class ProjectFilterNode extends FilterNode
    {

        public ProjectFilterNode(Node node, org.openide.nodes.Children children)
        {
            super(node, children);
        }

        @Override
        public void setName(String name)
        {
            final DataFolder favoritesFolder = FavoritesNode.getFolder();
            final DataObject[] children = favoritesFolder.getChildren();
            super.setName(name);
            try
            {
                favoritesFolder.setOrder(children);
            }
            catch (IOException ex)
            {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public String getDisplayName ()
        {
            if (FavoritesNode.getNode().equals(this.getParentNode()))
            {
                DataShadow ds = getCookie(DataShadow.class);
                if (ds != null)
                {
                    String name = ds.getName();
                    //String path = FileUtil.getFileDisplayName(ds.getOriginal().getPrimaryFile());
                    return name;
                }
                else
                {
                    return super.getDisplayName();
                }
            }
            else
            {
                return super.getDisplayName();
            }
        }

        @Override
        public String getHtmlDisplayName()
        {
            return getOriginal().getHtmlDisplayName();
        }

        @Override
        protected Node getOriginal()
        {
            return super.getOriginal();
        }

        @Override
        public boolean canDestroy ()
        {
            boolean canDestroy = super.canDestroy ();
            DataShadow link = getCookie (DataShadow.class);

            // if the DO of this node can be destroyed and the original DO should be destroyed too
            // ask the original if it's allowed to delete it
            if (canDestroy && isDeleteOriginal (link)) {
                canDestroy = link.getOriginal ().isDeleteAllowed ();
            }

            return canDestroy;
        }

        @Override
        public void destroy () throws IOException
        {
            if (canDestroy ())
            {
                DataShadow link = getCookie (DataShadow.class);
                DataObject original = isDeleteOriginal (link) ? link.getOriginal () : null;

                super.destroy ();

                if (original != null)
                {
                    original.delete ();
                }
            }
        }

        private boolean isDeleteOriginal (DataShadow link)
        {
            return false;
        }

    }

}
