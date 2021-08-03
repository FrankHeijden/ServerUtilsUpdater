package net.frankheijden.serverutilsupdater.common;

import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;
import net.frankheijden.serverutils.common.entities.results.PluginResult;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;

public interface Updater<P> {

    P getPlugin();

    Logger getLogger();

    void runTask(Runnable runnable);

    AbstractPluginManager<P, ?> getPluginManager();

    String getVersion(P plugin);

    String getServerUtilsPluginName();

    /**
     * Updates the plugin to the given resource.
     * @param file The resource file.
     */
    default void update(File file) {
        runTask(() -> {
            AbstractPluginManager<P, ?> pluginManager = getPluginManager();
            Logger logger = getLogger();

            Optional<P> oldPluginOptional = pluginManager.getPlugin(getServerUtilsPluginName());
            oldPluginOptional.ifPresent(oldPlugin -> {
                pluginManager.disablePlugin(oldPlugin);
                pluginManager.unloadPlugin(oldPlugin).tryClose();
            });

            PluginResult<P> loadResult = pluginManager.loadPlugin(file);
            if (!loadResult.isSuccess()) {
                logger.severe("Unable to load plugin \"" + file.getName() + "\": " + loadResult.getResult().name());
                return;
            }

            P plugin = loadResult.getPlugin();
            PluginResult<P> enableResult = pluginManager.enablePlugin(plugin);
            if (enableResult.isSuccess()) {
                logger.info("Successfully updated " + enableResult.getPluginId() + " to v" + getVersion(plugin));
            } else {
                String resultName = enableResult.getResult().name();
                logger.severe("Unable to enable plugin " + enableResult.getPluginId() + ": " + resultName);
            }
        });
    }
}
