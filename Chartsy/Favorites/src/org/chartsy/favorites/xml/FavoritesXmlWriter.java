package org.chartsy.favorites.xml;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.chartsy.main.favorites.nodes.FolderAPI;
import org.chartsy.main.favorites.nodes.FolderAPINode;
import org.chartsy.main.favorites.nodes.RootAPINode;
import org.chartsy.main.favorites.nodes.StockAPI;
import org.chartsy.main.favorites.nodes.StockAPINode;
import org.chartsy.main.utils.FileUtils;
import org.openide.nodes.Node;

/**
 *
 * @author Viorel
 */
public final class FavoritesXmlWriter
{

	private FavoritesXmlWriter()
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
			Logger.getLogger(FavoritesXmlWriter.class.getName()).log(
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
			Logger.getLogger(FavoritesXmlWriter.class.getName()).log(
				Level.SEVERE, "", ex);
		}
		return text;
	}

}
