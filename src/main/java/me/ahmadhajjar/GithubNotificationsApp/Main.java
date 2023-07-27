package me.ahmadhajjar.GithubNotificationsApp;

import me.ahmadhajjar.GithubNotificationsApp.service.GitHubAPIService;
import me.ahmadhajjar.GithubNotificationsApp.ui.GithubNotifierApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String GITHUB_TOKEN = System.getenv("GITHUB_TOKEN");

    public static void main(String[] args) {
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        logger.debug("Starting application ...");
        GitHubAPIService gitHubAPIService = new GitHubAPIService(GITHUB_TOKEN);

        RepoChecker checker = new RepoChecker(gitHubAPIService);
        Thread uiThread = new Thread(GithubNotifierApp::new);
        logger.debug("Starting threads ...");
        uiThread.start();
        checker.start();
    }
}
