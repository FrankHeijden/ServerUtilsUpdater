package net.frankheijden.serverutilsupdater.common;

import java.io.File;

public interface Updater {

    /**
     * Updates the plugin to the given resource.
     * @param file The resource file.
     */
    void update(File file);
}
