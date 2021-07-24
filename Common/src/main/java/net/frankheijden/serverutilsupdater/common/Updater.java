package net.frankheijden.serverutilsupdater.common;

import java.io.File;
import java.util.logging.Logger;
import net.frankheijden.serverutils.common.entities.LoadResult;
import net.frankheijden.serverutils.common.entities.Result;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;

public interface Updater<T> {

    T getPlugin();

    Logger getLogger();

    void runTask(Runnable runnable);

    AbstractPluginManager<T> getPluginManager();

    String getVersion(T plugin);

    String getName(T plugin);

    /**
     * Updates the plugin to the given resource.
     * @param file The resource file.
     */
    default void update(File file) {
        runTask(() -> {
            AbstractPluginManager<T> pluginManager = getPluginManager();
            Logger logger = getLogger();

            T oldPlugin = pluginManager.getPlugin("ServerUtils");
            if (oldPlugin != null) {
                pluginManager.disablePlugin(oldPlugin);
                pluginManager.unloadPlugin(oldPlugin).tryClose();
            }

            LoadResult<T> loadResult = pluginManager.loadPlugin(file);
            if (!loadResult.isSuccess()) {
                logger.severe("Unable to load plugin \"" + file.getName() + "\": " + loadResult.getResult().name());
                return;
            }

            T plugin = loadResult.get();
            Result result = pluginManager.enablePlugin(plugin);
            if (result == Result.SUCCESS) {
                logger.info("Successfully updated " + getName(plugin) + " to v" + getVersion(plugin));
            } else {
                logger.severe("Unable to enable plugin " + getName(plugin) + ": " + result.name());
            }
        });
    }
}
