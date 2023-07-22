package me.ahmadhajjar.GithubNotificationsApp.ui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

public class TrayAdapter {
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
            System.err.println("Error while initialising notification service!");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void initialiseTray() throws AWTException {
        if (!traySupported) {
            System.err.println("System tray not supported!");
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
            System.err.println("System tray not supported!");
            return;
        }
        trayIcon.displayMessage("New Pull Request", "Repository " + repoName + " has a new open PR : #" + newPRNumber + "\n Click Here to open!", TrayIcon.MessageType.INFO);

        trayIcon.removeActionListener(actionListener);
        actionListener = e -> {
            try {
                Desktop.getDesktop().browse(URI.create("https://github.com/" + repoName + "/pull/" + newPRNumber));
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        };
        trayIcon.addActionListener(actionListener);
    }
}
