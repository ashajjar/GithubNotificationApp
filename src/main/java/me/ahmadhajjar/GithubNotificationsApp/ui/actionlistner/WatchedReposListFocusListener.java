package me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class WatchedReposListFocusListener implements FocusListener {

    @Override
    public void focusGained(FocusEvent e) {
        @SuppressWarnings("unchecked")
        JList<String> watchedReposList = (JList<String>) e.getComponent();
        if (watchedReposList.getSelectedIndex() == -1) {
            watchedReposList.setSelectedIndex(watchedReposList.getModel().getSize() - 1);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
