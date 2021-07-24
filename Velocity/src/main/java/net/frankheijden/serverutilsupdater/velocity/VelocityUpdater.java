package net.frankheijden.serverutilsupdater.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;
import net.frankheijden.serverutils.common.managers.AbstractPluginManager;
import net.frankheijden.serverutils.velocity.managers.VelocityPluginCommandManager;
import net.frankheijden.serverutils.velocity.managers.VelocityPluginManager;
import net.frankheijden.serverutilsupdater.common.Updater;

@Plugin(
        id = "serverutilsupdater",
        name = "ServerUtilsUpdater",
        version = "${version}",
        description = "A server utility updater",
        url = "https://github.com/FrankHeijden/ServerUtilsUpdater",
        authors = "FrankHeijden",
        dependencies = @Dependency(id = "serverutils")
)
public class VelocityUpdater implements Updater<PluginContainer> {

    private final ProxyServer proxy;
    private final PluginContainer pluginContainer;
    private final org.slf4j.Logger logger;
    private final VelocityPluginManager pluginManager;

    /**
     * Constructs the VelocityUpdater.
     */
    @Inject
    public VelocityUpdater(
            ProxyServer proxy,
            PluginContainer pluginContainer,
            org.slf4j.Logger logger,
            @DataDirectory Path dataDirectory
    ) throws IOException {
        this.proxy = proxy;
        this.pluginContainer = pluginContainer;
        this.logger = logger;
        this.pluginManager = new VelocityPluginManager(
                proxy,
                logger,
                VelocityPluginCommandManager.load(
                        dataDirectory.resolve("serverutils").resolve(".pluginCommandsCache.json")
                )
        );
    }

    @Override
    public PluginContainer getPlugin() {
        return pluginContainer;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(logger.getName());
    }

    @Override
    public void runTask(Runnable runnable) {
        proxy.getScheduler().buildTask(this, runnable).schedule();
    }

    @Override
    public AbstractPluginManager<PluginContainer> getPluginManager() {
        return pluginManager;
    }

    @Override
    public String getVersion(PluginContainer plugin) {
        return plugin.getDescription().getVersion().orElse("<UNKNOWN>");
    }

    @Override
    public String getName(PluginContainer plugin) {
        return plugin.getDescription().getName().orElse("<UNKNOWN>");
    }

    @Override
    public String getServerUtilsPluginName() {
        return "serverutils";
    }
}
