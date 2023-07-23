package me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.IntStream;

public class RemoveRepoButtonListener implements ActionListener {
    private static final Logger logger = LogManager.getLogger(RemoveRepoButtonListener.class);
    private final JList<String> watchedReposList;

    public RemoveRepoButtonListener(JList<String> watchedReposList) {
        this.watchedReposList = watchedReposList;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultListModel<String> model = new DefaultListModel<>();

        if (watchedReposList.getSelectedIndex() == -1) {
            logger.debug("No repo was selected!");
            return;
        }

        ListModel<String> currentModel = watchedReposList.getModel();

        IntStream
                .range(0, currentModel.getSize())
                .mapToObj(currentModel::getElementAt)
                .filter(i -> !i.equals(watchedReposList.getSelectedValue()))
                .forEach(model::addElement);

        watchedReposList.setModel(model);
        watchedReposList.setSelectedIndex(model.getSize() - 1);
    }

}
