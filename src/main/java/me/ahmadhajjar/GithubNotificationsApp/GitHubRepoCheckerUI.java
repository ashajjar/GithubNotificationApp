package me.ahmadhajjar.GithubNotificationsApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class GitHubRepoCheckerUI {

    private static final String LAST_ORG_FILE = "last_org.data.txt";

    private final GitHubAPIService gitHubAPIService;
    private final NotificationService notificationService;
    private JTextField orgNameField;
    private JList<JTextField> textFieldJList;

    public GitHubRepoCheckerUI(GitHubAPIService gitHubAPIService, NotificationService notificationService) {
        this.gitHubAPIService = gitHubAPIService;
        this.notificationService = notificationService;
    }

    public void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new GithubNotifierApp("GitHub Repo Checker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //  frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);

        // Set the window icon
        ImageIcon appIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));
        frame.setIconImage(appIcon.getImage());


        // Add a text field
        orgNameField = new JTextField(20);
      //  frame.getContentPane().add(orgNameField, BorderLayout.NORTH);

        // Load the last used organization name
        loadLastOrgName();

        // Add a button
        JButton button = new JButton("Check Repos");
      //  frame.getContentPane().add(button, BorderLayout.SOUTH);

        // Add an action listener to the button
        button.addActionListener(actionEvent -> {
            String orgName = orgNameField.getText();
            RepoChecker checker = new RepoChecker(gitHubAPIService, notificationService, orgName);
            try {
                checker.checkReposForOpenPullRequests();
                saveLastOrgName(orgName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private void loadLastOrgName() {
        File file = new File(LAST_ORG_FILE);
        if (file.exists()) {
            try {
                String lastOrgName = new String(Files.readAllBytes(Paths.get(LAST_ORG_FILE)));
                orgNameField.setText(lastOrgName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveLastOrgName(String orgName) {
        try (FileWriter writer = new FileWriter(LAST_ORG_FILE)) {
            writer.write(orgName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
