package me.ahmadhajjar.GithubNotificationsApp;

import java.awt.*;

public class NotificationService {

    public void sendNotification(String repoName, int newPRNumber) throws Exception {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/icon.png"));
            TrayIcon trayIcon = new TrayIcon(image, "GitHub PR Notifier");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("There are open pull requests in " + repoName);
            tray.add(trayIcon);
            trayIcon.displayMessage("Open Pull Requests", "Repository " + repoName + " has new open PR with number: " + newPRNumber, TrayIcon.MessageType.INFO);
        } else {
            System.err.println("System tray not supported!");
        }
    }
}
