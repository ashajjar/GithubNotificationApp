package me.ahmadhajjar.GithubNotificationsApp.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public final class PROpener {
    private static final Logger logger = LogManager.getLogger(PROpener.class);

    public static void open(URI prURI) {
        try {
            Desktop.getDesktop().browse(prURI);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
}
