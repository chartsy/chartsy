package org.chartsy.chatsy.chat.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class CheckTree extends JPanel
{

	private JTree tree;

    public CheckTree(CheckNode rootNode)
	{
        tree = new JTree(rootNode);
        tree.setCellRenderer(new CheckRenderer());
        tree.setRowHeight(18);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setToggleClickCount(1000);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.addMouseListener(new NodeSelectionListener(tree));
        setLayout(new BorderLayout());
        add(tree, BorderLayout.CENTER);
    }

    class NodeSelectionListener extends MouseAdapter
	{
        JTree tree;

        NodeSelectionListener(JTree tree)
		{
            this.tree = tree;
        }

        public void mouseClicked(MouseEvent e)
		{
            int x = e.getX();
            int y = e.getY();
            int row = tree.getRowForLocation(x, y);
            TreePath path = tree.getPathForRow(row);
            if (path != null)
			{
                CheckNode node = (CheckNode)path.getLastPathComponent();
                boolean isSelected = !node.isSelected();
                node.setSelected(isSelected);
                if (node.getSelectionMode() == CheckNode.DIG_IN_SELECTION)
				{
                    if (isSelected)
						tree.expandPath(path);
                    else
						tree.collapsePath(path);
                }
                ((DefaultTreeModel)tree.getModel()).nodeChanged(node);
                tree.revalidate();
                tree.repaint();
            }
        }
    }

    public void close()
	{
    }

    class ButtonActionListener implements ActionListener 
	{

        CheckNode root;
        JTextArea textArea;

        ButtonActionListener(CheckNode root, JTextArea textArea)
		{
            this.root = root;
            this.textArea = textArea;
        }

        public void actionPerformed(ActionEvent e)
		{
            Enumeration nodeEnum = root.breadthFirstEnumeration();
            while (nodeEnum.hasMoreElements())
			{
                CheckNode node = (CheckNode)nodeEnum.nextElement();
                if (node.isSelected())
				{
                    TreeNode[] nodes = node.getPath();
                    textArea.append("\n" + nodes[0].toString());
                    for (int i = 1; i < nodes.length; i++)
                        textArea.append("/" + nodes[i].toString());
                }
            }
        }
    }

    public JTree getTree()
	{
        return tree;
    }

    public void expandTree()
	{
        for (int i = 0; i <= tree.getRowCount(); i++)
            tree.expandPath(tree.getPathForRow(i));
    }

}

