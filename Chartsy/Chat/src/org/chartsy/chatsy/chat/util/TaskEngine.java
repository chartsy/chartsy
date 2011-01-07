package org.chartsy.chatsy.chat.util;

import org.chartsy.chatsy.chat.util.log.Log;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskEngine
{

    private static TaskEngine instance = new TaskEngine();

    public static TaskEngine getInstance()
	{
        return instance;
    }

    private Timer timer;
    private ExecutorService executor;
    private Map<TimerTask, TimerTaskWrapper> wrappedTasks = new ConcurrentHashMap<TimerTask, TimerTaskWrapper>();

    private TaskEngine()
	{
        timer = new Timer("timer-chatsy", true);
        executor = Executors.newCachedThreadPool(new ThreadFactory()
		{
            final AtomicInteger threadNumber = new AtomicInteger(1);

            public Thread newThread(Runnable runnable)
			{
                Thread thread = new Thread(Thread.currentThread().getThreadGroup(), runnable,
					"pool-chatsy" + threadNumber.getAndIncrement(), 0);
                thread.setDaemon(true);
                if (thread.getPriority() != Thread.NORM_PRIORITY)
                    thread.setPriority(Thread.NORM_PRIORITY);
                return thread;
            }
        });
    }

    public Future<?> submit(Runnable task)
	{
        return executor.submit(task);
    }

    public void schedule(TimerTask task, long delay)
	{
        timer.schedule(new TimerTaskWrapper(task), delay);
    }

    public void schedule(TimerTask task, Date time)
	{
        timer.schedule(new TimerTaskWrapper(task), time);
    }

    public void schedule(TimerTask task, long delay, long period)
	{
        TimerTaskWrapper taskWrapper = new TimerTaskWrapper(task);
        wrappedTasks.put(task, taskWrapper);
        timer.schedule(taskWrapper, delay, period);
    }

    public void schedule(TimerTask task, Date firstTime, long period)
	{
        TimerTaskWrapper taskWrapper = new TimerTaskWrapper(task);
        wrappedTasks.put(task, taskWrapper);
        timer.schedule(taskWrapper, firstTime, period);
    }

    public void scheduleAtFixedRate(TimerTask task, long delay, long period)
	{
        TimerTaskWrapper taskWrapper = new TimerTaskWrapper(task);
        wrappedTasks.put(task, taskWrapper);
        try
		{
            timer.scheduleAtFixedRate(taskWrapper, delay, period);
        }
        catch (Exception e)
		{
            Log.error(e);
        }
    }

    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period)
	{
        TimerTaskWrapper taskWrapper = new TimerTaskWrapper(task);
        wrappedTasks.put(task, taskWrapper);
        try
		{
            timer.scheduleAtFixedRate(taskWrapper, firstTime, period);
        }
        catch (Exception e)
		{
            Log.error(e);
        }
    }

    public void cancelScheduledTask(TimerTask task)
	{
        TaskEngine.TimerTaskWrapper taskWrapper = wrappedTasks.remove(task);
        if (taskWrapper != null)
            taskWrapper.cancel();
    }

    public void shutdown()
	{
        if (executor != null)
		{
            executor.shutdownNow();
            executor = null;
        }
        if (timer != null)
		{
            timer.cancel();
            timer = null;
        }
    }

    private class TimerTaskWrapper extends TimerTask
	{

        private TimerTask task;

        public TimerTaskWrapper(TimerTask task)
		{
            this.task = task;
        }

        public void run()
		{
            executor.submit(task);
        }
    }
	
}