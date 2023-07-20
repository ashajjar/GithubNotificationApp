package me.ahmadhajjar.GithubNotificationsApp;

import javax.swing.*;

public class Main {

    private static final String GITHUB_TOKEN = System.getenv("GITHUB_TOKEN");

    public static void main(String[] args) {
        GitHubAPIService gitHubAPIService = new GitHubAPIService(GITHUB_TOKEN);
        NotificationService notificationService = new NotificationService();

        new GithubNotifierApp("GitHub Repo Checker");
//        SwingUtilities.invokeLater(gitHubRepoCheckerUI::createAndShowGUI);
    }
}
