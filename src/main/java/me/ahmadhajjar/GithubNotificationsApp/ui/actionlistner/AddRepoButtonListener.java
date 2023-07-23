package me.ahmadhajjar.GithubNotificationsApp.ui.actionlistner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AddRepoButtonListener implements ActionListener {
    private static final Logger logger = LogManager.getLogger(AddRepoButtonListener.class);
    private final JTextField newRepoName;
    private final JList<String> watchedReposList;

    public AddRepoButtonListener(JTextField newRepoName, JList<String> watchedReposList) {
        this.newRepoName = newRepoName;
        this.watchedReposList = watchedReposList;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultListModel<String> model = new DefaultListModel<>();
        String newRepoNameValue = newRepoName.getText().trim();

        if (newRepoNameValue.isEmpty()) {
            logger.debug("Repo name is empty!");
            return;
        }

        if (!newRepoNameValue.toLowerCase().matches("^[a-z\\d](?:[a-z\\d]|-(?=[a-z\\d])){0,38}/[a-z\\d](?:[a-z\\d]|-(?=[a-z\\d])){0,38}$")) {
            logger.debug("Repo name is invalid!");
            showValidationError(e);
            return;
        }

        ListModel<String> currentModel = watchedReposList.getModel();

        Stream<String> currentRepos = getCurrentReposAsStream(currentModel);

        if (currentRepos.anyMatch(s -> s.equalsIgnoreCase(newRepoNameValue))) {
            logger.debug("Repo name exists!");
            return;
        }

        currentRepos = getCurrentReposAsStream(currentModel);

        currentRepos.forEach(model::addElement);

        model.addElement(newRepoNameValue);
        watchedReposList.setModel(model);
        newRepoName.setText("");
    }

    private static void showValidationError(ActionEvent e) {
        Container parentComponent = ((Component) e.getSource()).getParent();
        JOptionPane.showMessageDialog(
                parentComponent,
                """
                        The repo name is invalid. Repo names are of the format "owner/repo"
                        """,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private Stream<String> getCurrentReposAsStream(ListModel<String> currentModel) {
        return IntStream
                .range(0, currentModel.getSize())
                .mapToObj(currentModel::getElementAt);
    }
}
