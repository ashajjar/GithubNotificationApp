package me.ahmadhajjar.GithubNotificationsApp;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GithubNotifierApp extends JFrame {
    private JTextField orgName;
    private JTextField newRepoName;
    private JButton addRepo;
    private JList<String> watchedReposList;
    private JPanel mainPanel;

    GithubNotifierApp(String title) {
        setTitle(title);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the window icon
        ImageIcon appIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));
        setIconImage(appIcon.getImage());

        pack();
        setVisible(true);

        addRepo.addActionListener(e -> {
            DefaultListModel<String> model = new DefaultListModel<>();

            for (int i = 0; i < watchedReposList.getModel().getSize(); i++) {
                model.addElement(watchedReposList.getModel().getElementAt(i));
            }

            // Add items to the model
            model.addElement(newRepoName.getText().trim().isEmpty() ? "Nononono" : newRepoName.getText().trim());
            watchedReposList.setModel(model);
        });
    }
}
