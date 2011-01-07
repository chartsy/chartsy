package org.chartsy.main.favorites.nodes;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.chartsy.main.ChartFrame;
import org.chartsy.main.data.ChartData;
import org.chartsy.main.intervals.DailyInterval;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.TemplateManager;
import org.chartsy.main.templates.Template;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.NotifyDescriptor.Message;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Viorel
 */
public class Actions
{

	private Actions()
	{}

	public static Action openStock(StockAPINode node)							{ return new OpenStock(node); }
	public static Action addFolder(RootAPINode node)							{ return AddFolder.addFolder(node); }
	public static Action renameFolder()											{ return RenameFolder.getDefault(); }

	private static class OpenStock extends AbstractAction
		implements HelpCtx.Provider
	{

		private StockAPINode node;

		public OpenStock(StockAPINode node)
		{
			this.node = node;
			putValue(NAME, "Open in New Tab");
		}

		public void actionPerformed(ActionEvent e)
		{
			if (node != null)
			{
				final StockAPI stock
					= node.getLookup().lookup(StockAPI.class);
				if (stock != null)
				{
					String defaultTemplate = TemplateManager.getDefault().getDefaultTemplate();
					Template template = TemplateManager.getDefault().getTemplate(defaultTemplate);

					ChartData chartData = new ChartData();
					chartData.setStock(stock.getStock());
					chartData.setDataProviderName(stock.getDataProviderName());
					chartData.setInterval(new DailyInterval());
					chartData.setChart(
						ChartManager.getDefault().getChart("Candle Stick"));

					ChartFrame chartFrame = ChartFrame.getInstance();
					chartFrame.setChartData(chartData);
					chartFrame.setTemplate(template);
					chartFrame.open();
					chartFrame.requestActive();
				}
			}
		}

		public HelpCtx getHelpCtx()
		{
			return new HelpCtx(OpenStock.class);
		}

	}

	private static class AddFolder extends NodeAction
	{

		private static AddFolder ADDFOLDER;

		public static Action addFolder(RootAPINode node)
		{
			if (ADDFOLDER == null)
				ADDFOLDER = new AddFolder(node);
			return ADDFOLDER;
		}

		private RootAPINode root;
		
		private AddFolder(RootAPINode root)
		{
			this.root = root;
		}

		protected @Override void performAction(Node[] nodes)
		{
			InputLine descriptor = new DialogDescriptor.InputLine("Folder Name:", "Add Folder");
			descriptor.setOptions(new Object[]
			{
				DialogDescriptor.OK_OPTION,
				DialogDescriptor.CANCEL_OPTION
			});

			Object ret = DialogDisplayer.getDefault().notify(descriptor);
			if (ret.equals(DialogDescriptor.OK_OPTION))
			{
				String folderName = descriptor.getInputText();
				if ((folderName != null)
					&& (folderName.hashCode() != "".hashCode()))
				{
					FolderAPI folder = new FolderAPI();
					folder.setDisplayName(folderName);
					
					if (!root.getRoot().folderNameExists(folderName))
					{
						root.getRoot().addFolder(folder);
						if (root.getChildren() instanceof RootAPIChildren)
						{
							RootAPIChildren children = (RootAPIChildren) root.getChildren();
							children.addNewFolder(new FolderAPINode(folder));
						}
						//root.getChildren().add(new Node[] { new FolderAPINode(folder) });
					}
					else
					{
						Message message = new DialogDescriptor.Message(
							"Folder already exists.",
							DialogDescriptor.INFORMATION_MESSAGE);
						DialogDisplayer.getDefault().notify(message);
					}
				}
				else
				{
					Message message = new DialogDescriptor.Message(
						"Invalid folder name.",
						DialogDescriptor.WARNING_MESSAGE);
					DialogDisplayer.getDefault().notify(message);
				}
			}
		}

		protected @Override boolean enable(Node[] nodes)
		{
			return true;
		}

		public @Override String getName()
		{
			return "Add Folder";
		}

		public @Override HelpCtx getHelpCtx()
		{
			return new HelpCtx(AddFolder.class);
		}

		protected @Override boolean asynchronous()
		{
            return false;
        }

	}

	private static class RenameFolder extends NodeAction
	{

		private static RenameFolder RENAME = new RenameFolder();

		public static Action getDefault()
		{
			return RENAME;
		}

		protected @Override void performAction(Node[] nodes)
		{
			if (nodes == null || nodes.length == 0)
				return;

			if (nodes.length > 1)
			{
				Message message = new DialogDescriptor.Message(
					"You can rename only one folder.",
					DialogDescriptor.INFORMATION_MESSAGE);
				DialogDisplayer.getDefault().notify(message);
				return;
			}

			FolderAPINode node = (FolderAPINode) nodes[0];
			RootAPINode root = (RootAPINode) node.getParentNode();

			InputLine descriptor = new DialogDescriptor.InputLine("Folder Name:", "Rename Folder");
			descriptor.setOptions(new Object[]
			{
				DialogDescriptor.OK_OPTION,
				DialogDescriptor.CANCEL_OPTION
			});

			Object ret = DialogDisplayer.getDefault().notify(descriptor);
			if (ret.equals(DialogDescriptor.OK_OPTION))
			{
				String folderName = descriptor.getInputText();
				if ((folderName != null)
					&& (folderName.hashCode() != "".hashCode()))
				{
					if (!root.getRoot().folderNameExists(folderName))
					{
						node.setDisplayName(folderName);
						node.getFolder().setDisplayName(folderName);
					}
					else
					{
						Message message = new DialogDescriptor.Message(
							"Folder already exists.",
							DialogDescriptor.INFORMATION_MESSAGE);
						DialogDisplayer.getDefault().notify(message);
					}
				}
				else
				{
					Message message = new DialogDescriptor.Message(
						"Invalid folder name.",
						DialogDescriptor.WARNING_MESSAGE);
					DialogDisplayer.getDefault().notify(message);
				}
			}
		}

		protected @Override boolean enable(Node[] nodes)
		{
			if (nodes == null || nodes.length == 0)
				return false;

			for (int i = 0; i < nodes.length; i++)
				if (!(nodes[i] instanceof FolderAPINode))
					return false;

			return true;
		}

		public @Override String getName()
		{
			return "Rename";
		}

		public @Override HelpCtx getHelpCtx()
		{
			return new HelpCtx(RenameFolder.class);
		}

		protected @Override boolean asynchronous()
		{
            return false;
        }

	}

}
