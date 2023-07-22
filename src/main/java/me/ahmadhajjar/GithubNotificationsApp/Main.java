package me.ahmadhajjar.GithubNotificationsApp;

import me.ahmadhajjar.GithubNotificationsApp.service.GitHubAPIService;
import me.ahmadhajjar.GithubNotificationsApp.ui.TrayAdapter;
import me.ahmadhajjar.GithubNotificationsApp.ui.GithubNotifierApp;

public class Main {

    private static final String GITHUB_TOKEN = System.getenv("GITHUB_TOKEN");

    public static void main(String[] args) {
        System.out.println("Starting application ...");
        GitHubAPIService gitHubAPIService = new GitHubAPIService(GITHUB_TOKEN);
        TrayAdapter trayAdapter = TrayAdapter.getInstance();

        RepoChecker checker = new RepoChecker(gitHubAPIService, trayAdapter);
        Thread uiThread = new Thread(GithubNotifierApp::new);
        System.out.println("Starting threads ...");
        uiThread.start();
        checker.start();
    }
}
