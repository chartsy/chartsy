package org.chartsy.main.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.chartsy.main.data.DataItem;
import org.chartsy.main.data.Dataset;
import org.chartsy.main.data.Stock;
import org.chartsy.main.utils.FileUtils;

/**
 *
 * @author Viorel
 */
public class CacheManager
{

	private static CacheManager instance;

	public static CacheManager getInstance()
	{
		if (instance == null)
			instance = new CacheManager();
		return instance;
	}

	private CacheManager()
	{
	}

	public int getLastChartFrameId()
		throws IOException
	{
		String folder = FileUtils.cacheFolder();
		File file = FileUtils.hashedCacheFile(folder, "charts");
		if (file.exists())
		{
			Properties properties = new Properties();
			FileInputStream fileInputStream = new FileInputStream(file);
			properties.load(fileInputStream);
			int id = Integer.parseInt(properties.getProperty("last_id", "0"));
			return id;
		} else
			return 0;
	}

	public void cacheLastChartFrameId(int id)
		throws IOException
	{
		String folder = FileUtils.cacheFolder();
		File file = FileUtils.hashedCacheFile(folder, "charts");
		if (!file.exists())
			file.createNewFile();

		Properties properties = new Properties();
		properties.setProperty("last_id", Integer.toString(id));

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		properties.store(fileOutputStream, "");
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	public boolean stockCacheExists(String fileName)
	{
		String folder = FileUtils.cacheStocksFolder();
		File file = FileUtils.hashedCacheFile(folder, fileName);
		return file.exists();
	}

	public void cacheStock(Stock stock, String fileName)
		throws IOException
	{
		String folder = FileUtils.cacheStocksFolder();
		File file = FileUtils.hashedCacheFile(folder, fileName);
		if (!file.exists())
		{
			file.createNewFile();
			file.deleteOnExit();
		}

		Properties properties = new Properties();
		properties.setProperty("symbol", stock.getSymbol());
		properties.setProperty("exchange", stock.getExchange());
		properties.setProperty("companyName", stock.getCompanyName());

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		properties.store(fileOutputStream, "");
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	public Stock fetchStockFromCache(String fileName)
		throws IOException
	{
		String folder = FileUtils.cacheStocksFolder();
		File file = FileUtils.hashedCacheFile(folder, fileName);

		FileInputStream fileInputStream = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(fileInputStream);

		Stock stock = new Stock();
		stock.setSymbol(properties.getProperty("symbol"));
		stock.setExchange(properties.getProperty("exchange", ""));
		stock.setCompanyName(properties.getProperty("companyName", ""));

		fileInputStream.close();
		return stock;
	}

	public boolean datasetCacheExists(String fileName)
	{
		String folder = FileUtils.cacheDatasetsFolder();
		File file = FileUtils.hashedCacheFile(folder, fileName);
		return file.exists();
	}

	public void cacheDataset(Dataset dataset, String fileName)
		throws IOException
	{
		String folder = FileUtils.cacheDatasetsFolder();
		File file = FileUtils.hashedCacheFile(folder, fileName);
		if (!file.exists())
		{
			file.createNewFile();
			file.deleteOnExit();
		}

		Properties properties = new Properties();
		int size = dataset.getItemsCount();
		for (int i = 0; i < size; i++)
		{
			DataItem item = dataset.getDataItem(i);
			if (item != null)
				properties.setProperty(Integer.toString(i), dataset.getDataItem(i).toString());
			else
				properties.setProperty(Integer.toString(i), "null");
		}

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		properties.store(fileOutputStream, "");
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	public void fetchDatasetFromCache(String fileName)
		throws IOException
	{
		String folder = FileUtils.cacheDatasetsFolder();
		File file = FileUtils.hashedCacheFile(folder, fileName);

		Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(file);
		properties.load(fileInputStream);

		int size = properties.size();
		List<DataItem> items = new ArrayList<DataItem>();
		
		for (int i = 0; i < size; i++)
		{
			String key = Integer.toString(i);
			String property = properties.getProperty(key);
			if (property.equals("null"))
			{
				items.add(null);
			} else
			{
				String[] values = property.split(",");
				DataItem item = new DataItem(
					Long.parseLong(values[0]),
					Double.parseDouble(values[1]),
					Double.parseDouble(values[2]),
					Double.parseDouble(values[3]),
					Double.parseDouble(values[4]),
					Double.parseDouble(values[5]));
				items.add(item);
			}
		}

		Dataset dataset = new Dataset(items);
		DatasetUsage.getInstance().addDataset(fileName, dataset);
		fileInputStream.close();
	}

	public Dataset getDatasetFromCache(String fileName)
		throws IOException
	{
		String folder = FileUtils.cacheDatasetsFolder();
		File file = FileUtils.hashedCacheFile(folder, fileName);

		Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(file);
		properties.load(fileInputStream);

		int size = properties.size();
		List<DataItem> items = new ArrayList<DataItem>();

		for (int i = 0; i < size; i++)
		{
			String key = Integer.toString(i);
			String property = properties.getProperty(key);
			if (property.equals("null"))
			{
				items.add(null);
			} else
			{
				String[] values = property.split(",");
				DataItem item = new DataItem(
					Long.parseLong(values[0]),
					Double.parseDouble(values[1]),
					Double.parseDouble(values[2]),
					Double.parseDouble(values[3]),
					Double.parseDouble(values[4]),
					Double.parseDouble(values[5]));
				items.add(item);
			}
		}

		Dataset dataset = new Dataset(items);
		fileInputStream.close();
		return dataset;
	}

	public int fetchDatasetSize(String fileName)
		throws IOException
	{
		String folder = FileUtils.cacheDatasetsFolder();
		File file = FileUtils.hashedCacheFile(folder, fileName);

		Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(file);
		properties.load(fileInputStream);

		int size = properties.size();
		fileInputStream.close();
		return size;
	}

	public Dataset fetchVisibleDatasetFromCache(String fileName, int period, int end)
		throws IOException
	{
		String folder = FileUtils.cacheDatasetsFolder();
		File file = FileUtils.hashedCacheFile(folder, fileName);

		Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(file);
		properties.load(fileInputStream);

		List<DataItem> items = new ArrayList<DataItem>();
		int size = properties.size();
		for (int i = 0; i < period; i++)
		{
			int j = end - period + i;
			if (j < size && j >= 0)
			{
				String key = Integer.toString(j);
				String property = properties.getProperty(key);
				if (property.equals("null"))
				{
					items.add(null);
				} else
				{
					String[] values = property.split(",");
					DataItem item = new DataItem(
						Long.parseLong(values[0]),
						Double.parseDouble(values[1]),
						Double.parseDouble(values[2]),
						Double.parseDouble(values[3]),
						Double.parseDouble(values[4]),
						Double.parseDouble(values[5]));
					items.add(item);
				}
			}
		}

		fileInputStream.close();
		Dataset dataset = new Dataset(items);
		return dataset;
	}

}
