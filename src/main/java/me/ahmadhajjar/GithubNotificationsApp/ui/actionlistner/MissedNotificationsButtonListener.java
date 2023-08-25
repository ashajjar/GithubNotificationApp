package me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner;

import me.ahmadhajjar.GithubNotificationsApp.ui.MissedNotifications;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MissedNotificationsButtonListener implements ActionListener {

    final private MissedNotifications missedNotifications;

    public MissedNotificationsButtonListener(JFrame mainWindow) {
        missedNotifications = new MissedNotifications(mainWindow);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        missedNotifications.populateTree();
        missedNotifications.setVisible(true);
    }
}
