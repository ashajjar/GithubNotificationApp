package me.ahmadhajjar.GithubNotificationsApp;

import me.ahmadhajjar.GithubNotificationsApp.service.DiskStorageService;
import me.ahmadhajjar.GithubNotificationsApp.service.GitHubAPIService;
import me.ahmadhajjar.GithubNotificationsApp.service.StorageService;
import me.ahmadhajjar.GithubNotificationsApp.ui.NotificationCenter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RepoChecker extends Thread {
    private static final Logger logger = LogManager.getLogger(RepoChecker.class);
    public static final long ONE_MINUTE = 60 * 1000;
    private static final long INITIAL_WAIT_TIME = 5000;
    private final GitHubAPIService gitHubAPIService;
    private final StorageService storageService;
    Map<String, List<PR>> reposPRMap;

    public RepoChecker(GitHubAPIService gitHubAPIService) {
        this.gitHubAPIService = gitHubAPIService;
        this.storageService = DiskStorageService.getInstance();
    }

    @Override
    public void run() {
        // todo Put the try-catch inside the loop. And implement incremental waiting strategy.
        try {
            Thread.sleep(INITIAL_WAIT_TIME);
        } catch (InterruptedException e) {
            logger.error(e);
        }
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                checkReposForOpenPullRequests();
                //noinspection BusyWait
                Thread.sleep(ONE_MINUTE);

            } catch (Throwable e) {
                logger.error(e);
            }
        }
    }

    public void checkReposForOpenPullRequests() throws Exception {
        reposPRMap = storageService.loadReposPRList();

        List<String> repos = storageService.loadReposList();
        for (String repoName : repos) {
            JSONArray pullRequests = gitHubAPIService.getLatestPullRequestsForRepo(repoName);

            if (pullRequests == null) {
                continue;
            }

            boolean newRepo = !reposPRMap.containsKey(repoName);

            List<PR> lastPRNumbers = reposPRMap.get(repoName);
            List<PR> newPRNumbers = createPullRequestsNumbersList(pullRequests);

            reposPRMap.put(repoName, newPRNumbers);

            if (!newRepo) {
                var repoNotificationThread = new RepoNotificationThread(repoName, lastPRNumbers, newPRNumbers);
                repoNotificationThread.start();
            }
        }
        storageService.saveReposPRList(reposPRMap);
    }

    private static List<PR> createPullRequestsNumbersList(JSONArray pullRequests) {
        List<PR> prNumbers = new ArrayList<>();
        for (int j = 0; j < pullRequests.length(); j++) {
            JSONObject pr = pullRequests.getJSONObject(j);
            int prNumber = pr.getInt("number");
            String username = pr.getJSONObject("user").getString("login");
            prNumbers.add(new PR(username, prNumber));
        }
        return prNumbers;
    }

    private static class RepoNotificationThread extends Thread {

        private final String repoName;
        private final List<PR> lastPRNumbers;
        private final List<PR> newPRNumbers;

        public RepoNotificationThread(String repoName, List<PR> lastPRNumbers, List<PR> newPRNumbers) {
            this.repoName = repoName;
            this.lastPRNumbers = lastPRNumbers;
            this.newPRNumbers = newPRNumbers;
        }

        @Override
        public void run() {
            notifyAboutNewPullRequests();
        }

        private void notifyAboutNewPullRequests() {
            for (PR newPRNumber : newPRNumbers) {
                if (!lastPRNumbers.contains(newPRNumber)) {
                    NotificationCenter.getInstance().showNotification(repoName, newPRNumber.getPrId());
                }
            }
        }
    }
}
