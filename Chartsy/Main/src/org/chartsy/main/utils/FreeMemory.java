package org.chartsy.main.utils;

/**
 *
 * @author Viorel
 */
public final class FreeMemory
{

	private static FreeMemory instance;

	public static FreeMemory getInstance()
	{
		if (instance == null)
			instance = new FreeMemory();
		return instance;
	}

	private FreeMemory()
	{
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				freeMemory();
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	public void freeMemory()
	{
		long minRunningMemory = (1024 * 1024 * 10);
		Runtime runtime = Runtime.getRuntime();
		while (runtime.freeMemory() > minRunningMemory)
		{
			
		}
		runtime.gc();
		freeMemory();
	}

}
