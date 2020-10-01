package net.frankheijden.serverutilsupdater.bukkit;

import java.io.File;

import net.frankheijden.serverutils.bukkit.ServerUtils;
import net.frankheijden.serverutils.bukkit.managers.BukkitPluginManager;
import net.frankheijden.serverutilsupdater.common.Updater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerUtilsUpdater extends JavaPlugin implements Updater {

    /**
     * Updates the plugin to the given resource.
     * @param file The resource file.
     */
    public void update(File file) {
        Bukkit.getScheduler().runTask(this, () -> {
            PluginManager serverManager = Bukkit.getPluginManager();
            ServerUtils plugin = null;
            try {
                plugin = (ServerUtils) serverManager.loadPlugin(file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (plugin == null) {
                getLogger().severe("Unable to load plugin ServerUtils, please restart the server manually.");
                return;
            }

            serverManager.enablePlugin(plugin);
            BukkitPluginManager manager = plugin.getPlugin().getPluginManager();
            manager.disablePlugin(this);
            manager.unloadPlugin(this).tryClose();
        });
    }
}
