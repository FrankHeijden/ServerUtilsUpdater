package net.frankheijden.serverutilsupdater.bukkit;

import net.frankheijden.serverutils.bukkit.managers.BukkitPluginManager;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutilsupdater.common.Updater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitUpdater extends JavaPlugin implements Updater<Plugin> {

    private final BukkitPluginManager pluginManager = new BukkitPluginManager();

    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    public void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    @Override
    public AbstractPluginManager<Plugin> getPluginManager() {
        return pluginManager;
    }

    @Override
    public String getVersion(Plugin plugin) {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String getName(Plugin plugin) {
        return plugin.getName();
    }
}
