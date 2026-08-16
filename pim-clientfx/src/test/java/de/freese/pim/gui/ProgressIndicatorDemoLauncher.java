package de.freese.pim.gui;

import com.sun.javafx.application.LauncherImpl;

/**
 * @author Thomas Freese
 * @since 16.08.26
 */
public final class ProgressIndicatorDemoLauncher {
    private ProgressIndicatorDemoLauncher() {
        super();
    }

    static void main(final String[] args) {
        LauncherImpl.launchApplication(ProgressIndicatorDemo.class, args);
    }
}
