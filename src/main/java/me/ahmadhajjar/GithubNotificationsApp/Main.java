package me.ahmadhajjar.GithubNotificationsApp;

import me.ahmadhajjar.GithubNotificationsApp.service.GitHubAPIService;
import me.ahmadhajjar.GithubNotificationsApp.ui.GithubNotifierApp;

public class Main {

    private static final String GITHUB_TOKEN = System.getenv("GITHUB_TOKEN");

    public static void main(String[] args) {
        GitHubAPIService gitHubAPIService = new GitHubAPIService(GITHUB_TOKEN);
        NotificationService notificationService = new NotificationService();

        new GithubNotifierApp();
//        SwingUtilities.invokeLater(gitHubRepoCheckerUI::createAndShowGUI);
    }
}
