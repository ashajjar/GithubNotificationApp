package me.ahmadhajjar.GithubNotificationsApp.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

public class TrayAdapter {
    private static final Logger logger = LogManager.getLogger(TrayAdapter.class);
    private final Boolean traySupported = SystemTray.isSupported();
    private TrayIcon trayIcon;

    private ActionListener actionListener;
    private final PopupMenu popup = new PopupMenu();
    private static final TrayAdapter instance = new TrayAdapter();

    public static TrayAdapter getInstance() {
        return instance;
    }

    private TrayAdapter() {
        try {
            initialiseTray();
        } catch (AWTException e) {
            logger.error("Error while initialising notification service!");
            logger.error(e);
        }
    }

    private void initialiseTray() throws AWTException {
        if (!traySupported) {
            logger.error("System tray not supported!");
            return;
        }

        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/icon.png"));
        trayIcon = new TrayIcon(image, "GitHub PR Notifier", popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Github Notification Service");

        SystemTray tray = SystemTray.getSystemTray();
        tray.add(trayIcon);
    }

    public void addPopupMenuItem(String name, ActionListener actionListener) {
        MenuItem exitItem = new MenuItem(name);
        exitItem.addActionListener(actionListener);
        popup.add(exitItem);
    }

    public void sendNotification(String repoName, int newPRNumber) {
        if (!traySupported) {
            logger.error("System tray not supported!");
            return;
        }
        logger.debug("Sending notification for :" + repoName + " #" + newPRNumber);
        trayIcon.displayMessage("New Pull Request", "Repository " + repoName + " has a new open PR : #" + newPRNumber + "\n Click Here to open!", TrayIcon.MessageType.INFO);

        trayIcon.removeActionListener(actionListener);
        actionListener = e -> {
            try {
                Desktop.getDesktop().browse(URI.create("https://github.com/" + repoName + "/pull/" + newPRNumber));
            } catch (IOException ex) {
                logger.error(ex);
            }
        };
        trayIcon.addActionListener(actionListener);
    }
}
