package net.frankheijden.serverutilsupdater.bungee;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import net.frankheijden.serverutils.bungee.ServerUtils;
import net.frankheijden.serverutilsupdater.common.Updater;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;
import org.yaml.snakeyaml.Yaml;

public class ServerUtilsUpdater extends Plugin implements Updater {

    /**
     * Updates the plugin to the given resource.
     * @param file The resource file.
     */
    public void update(File file) {
        ProxyServer proxy = ProxyServer.getInstance();

        PluginDescription desc;
        try {
            desc = getPluginDescription(file);
        } catch (Throwable th) {
            proxy.getLogger().log(Level.WARNING, "Error fetching PluginDescription", th);
            return;
        }

        ServerUtils plugin;
        try {
            URL url = desc.getFile().toURI().toURL();
            URLClassLoader loader = (URLClassLoader) newPluginClassLoader(proxy, desc, url);

            Class<?> main = loader.loadClass(desc.getMain());
            plugin = (ServerUtils) main.getDeclaredConstructor().newInstance();

            getPlugins(proxy.getPluginManager()).put(desc.getName(), plugin);
            plugin.onLoad();
            proxy.getLogger().log(Level.INFO, "Loaded plugin {0} version {1} by {2}", new Object[] {
                    desc.getName(), desc.getVersion(), desc.getAuthor()
            });
        } catch (Throwable th) {
            proxy.getLogger().log(Level.WARNING, "Error loading plugin " + desc.getName(), th);
            return;
        }

        plugin.onEnable();
        Object[] args = new Object[] { desc.getName(), desc.getVersion(), desc.getAuthor() };
        proxy.getLogger().log(Level.INFO, "Enabled plugin {0} version {1} by {2}", args);

        plugin.getPlugin().getPluginManager().unloadPlugin(this);
    }

    /**
     * Retrieves the plugin map.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Plugin> getPlugins(PluginManager instance) throws ReflectiveOperationException {
        Field pluginsField = PluginManager.class.getDeclaredField("plugins");
        pluginsField.setAccessible(true);
        return (Map<String, Plugin>) pluginsField.get(instance);
    }

    /**
     * Retrieves a new instance of a PluginClassLoader.
     */
    public static Object newPluginClassLoader(ProxyServer proxy, PluginDescription desc, URL... urls) throws Exception {
        Constructor<?> constructor = Class.forName("net.md_5.bungee.api.plugin.PluginClassloader")
                .getDeclaredConstructor(ProxyServer.class, PluginDescription.class, URL[].class);
        constructor.setAccessible(true);
        return constructor.newInstance(proxy, desc, urls);
    }

    /**
     * Retrieves the PluginDescription of a (plugin's) File.
     * @param file The file.
     * @return The PluginDescription.
     * @throws Exception Iff and I/O exception occurred, or notNullChecks failed.
     */
    public PluginDescription getPluginDescription(File file) throws Exception {
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = getPluginDescriptionEntry(jar);
            Preconditions.checkNotNull(entry, "Plugin must have a plugin.yml or bungee.yml");

            try (InputStream in = jar.getInputStream(entry)) {
                Field yamlField = PluginManager.class.getDeclaredField("yaml");
                yamlField.setAccessible(true);

                Yaml yaml = (Yaml) yamlField.get(ProxyServer.getInstance().getPluginManager());
                PluginDescription desc = yaml.loadAs(in, PluginDescription.class);
                Preconditions.checkNotNull(desc.getName(), "Plugin from %s has no name", file);
                Preconditions.checkNotNull(desc.getMain(), "Plugin from %s has no main", file);

                desc.setFile(file);
                return desc;
            }
        }
    }

    /**
     * Retrieves the JarEntry which contains the Description file of the JarFile.
     * @param jar The JarFile.
     * @return The description JarEntry.
     */
    public static JarEntry getPluginDescriptionEntry(JarFile jar) {
        JarEntry entry = jar.getJarEntry("bungee.yml");
        if (entry == null) return jar.getJarEntry("plugin.yml");
        return entry;
    }
}
