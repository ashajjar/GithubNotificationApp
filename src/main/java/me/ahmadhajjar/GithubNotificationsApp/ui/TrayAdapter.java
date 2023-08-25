package me.ahmadhajjar.GithubNotificationsApp.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionListener;

public class TrayAdapter {
    private static final Logger logger = LogManager.getLogger(TrayAdapter.class);
    private final Boolean traySupported = SystemTray.isSupported();
    private final PopupMenu popup = new PopupMenu();
    private static final TrayAdapter instance = new TrayAdapter();

    public static TrayAdapter getInstance() {
        return instance;
    }

    private TrayAdapter() {
        try {
            initialiseTray();
        } catch (AWTException e) {
            logger.error("Error while initialising tray!");
            logger.error(e);
        }
    }

    private void initialiseTray() throws AWTException {
        if (!traySupported) {
            logger.error("System tray not supported!");
            return;
        }

        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, "GitHub PR Notifier", popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Github Notification Service");

        SystemTray tray = SystemTray.getSystemTray();
        tray.add(trayIcon);
    }

    public void addPopupMenuItem(String name, ActionListener actionListener) {
        MenuItem item = new MenuItem(name);
        item.addActionListener(actionListener);
        popup.add(item);
    }
}
