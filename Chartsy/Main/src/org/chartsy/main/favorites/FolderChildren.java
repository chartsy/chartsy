package org.chartsy.main.favorites;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Viorel
 */
public class FolderChildren extends Children.Keys
{

    private String[] folders = new String[]
    {
        "Stocks",
        "Market Internals",
        "E-Minis",
        "Index",
        "Scans"
    };

    public FolderChildren()
    {}

    protected Node[] createNodes(Object key) {
        Folder obj = (Folder) key;
        return new Node[] { new FolderNode(obj) };
    }

    protected void addNotify() {
        super.addNotify();
        Folder[] objs = new Folder[folders.length];
        for (int i = 0; i < objs.length; i++) {
            Folder cat = new Folder();
            cat.setFolderName(folders[i]);
            objs[i] = cat;
        }
        setKeys(objs);
    }


}
