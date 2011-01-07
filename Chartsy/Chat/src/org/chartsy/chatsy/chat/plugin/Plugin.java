package org.chartsy.chatsy.chat.plugin;

public interface Plugin
{

    public void initialize();
    public void shutdown();
    public boolean canShutDown();
    public void uninstall();

}