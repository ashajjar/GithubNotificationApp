package me.ahmadhajjar.GithubNotificationsApp;

import me.ahmadhajjar.GithubNotificationsApp.service.GitHubAPIService;
import me.ahmadhajjar.GithubNotificationsApp.ui.GithubNotifierApp;
import me.ahmadhajjar.GithubNotificationsApp.utils.OSType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String GITHUB_TOKEN = System.getenv("GITHUB_TOKEN");

    public static void main(String[] args) {
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        logger.debug("Starting application ...");
        setUILookAndFeel();

        GitHubAPIService gitHubAPIService = new GitHubAPIService(GITHUB_TOKEN);
        RepoChecker checker = new RepoChecker(gitHubAPIService);
        Thread uiThread = new Thread(() -> new GithubNotifierApp(checker));
        logger.debug("Starting threads ...");
        uiThread.start();
        checker.start();
    }

    private static void setUILookAndFeel() {
        try {
            logger.debug("Trying to change look and feel ...");
            switch (OSType.DETECTED) {
                case MacOS -> UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacDarkLaf");
                case Windows -> UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
                default -> UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            }
            logger.debug("Successfully changed look and feel!");
        } catch (Exception ex) {
            logger.error("Changing look and feel failed.");
            logger.error(ex);
        }
    }
}
