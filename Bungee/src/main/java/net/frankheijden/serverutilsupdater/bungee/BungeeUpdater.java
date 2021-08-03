package net.frankheijden.serverutilsupdater.bungee;

import net.frankheijden.serverutils.bungee.managers.BungeePluginManager;
import net.frankheijden.serverutilsupdater.common.Updater;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeUpdater extends Plugin implements Updater<Plugin> {

    private final BungeePluginManager pluginManager = new BungeePluginManager();

    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    public void runTask(Runnable runnable) {
        ProxyServer.getInstance().getScheduler().runAsync(this, runnable);
    }

    @Override
    public BungeePluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public String getVersion(Plugin plugin) {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String getServerUtilsPluginName() {
        return "ServerUtils";
    }
}
