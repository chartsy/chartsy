package org.chartsy.main.favorites;

import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Index;
import org.openide.nodes.Node;

/**
 *
 * @author Viorel
 */
public class LeafChildren extends Index.ArrayChildren
{

    private Folder folder;

    private String[][] items = new String[][]
    {
        {"Stocks", "AAPL", "", "0.0", "0.0", "0.0"},
        {"Stocks" ,"GOOG", "", "0.0", "0.0", "0.0"},
        {"Stocks" ,"MSFT", "", "0.0", "0.0", "0.0"},
        {"Stocks" ,"GAIA", "", "0.0", "0.0", "0.0"}
    };

    public LeafChildren(Folder folder)
    {
        this.folder = folder;
    }

    protected List<Node> initCollection()
    {
        ArrayList list = new ArrayList(items.length);
        for (int i = 0; i < items.length; i++)
        {
            if (folder.getFolderName().equals(items[i][0]))
            {
                Leaf leaf = new Leaf();
                leaf.setSymbol(items[i][1]);
                leaf.setExchange(items[i][2]);
                leaf.setValue(new Double(items[i][3]));
                leaf.setChange(new Double(items[i][4]));
                leaf.setPercent(new Double(items[i][5]));
                list.add(new LeafNode(leaf));
            }
        }
        return list;
    }

}
