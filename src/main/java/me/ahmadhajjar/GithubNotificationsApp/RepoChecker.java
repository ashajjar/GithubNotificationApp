package me.ahmadhajjar.GithubNotificationsApp;

import me.ahmadhajjar.GithubNotificationsApp.service.DiskStorageService;
import me.ahmadhajjar.GithubNotificationsApp.service.GitHubAPIService;
import me.ahmadhajjar.GithubNotificationsApp.ui.TrayAdapter;
import me.ahmadhajjar.GithubNotificationsApp.service.StorageService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RepoChecker extends Thread {
    public static final long ONE_MINUTE = 60 * 1000;
    private final GitHubAPIService gitHubAPIService;
    private final TrayAdapter trayAdapter;
    private final StorageService storageService;
    Map<String, List<Integer>> reposPRMap;

    public RepoChecker(GitHubAPIService gitHubAPIService, TrayAdapter trayAdapter) {
        this.gitHubAPIService = gitHubAPIService;
        this.trayAdapter = trayAdapter;
        this.storageService = DiskStorageService.getInstance();
    }

    @Override
    public void run() {
        try {
            while (true) {
                checkReposForOpenPullRequests();
                Thread.sleep(ONE_MINUTE);
            }
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    public void checkReposForOpenPullRequests() throws Exception {
        reposPRMap = storageService.loadReposPRList();

        List<String> repos = storageService.loadReposList();
        for (String repoName : repos) {
            JSONArray pullRequests = gitHubAPIService.getLatestPullRequestsForRepo(repoName);

            if (!reposPRMap.containsKey(repoName)) {
                // Store the pull request numbers
                List<Integer> prNumbers = new ArrayList<>();
                for (int j = 0; j < pullRequests.length(); j++) {
                    JSONObject pr = pullRequests.getJSONObject(j);
                    int prNumber = pr.getInt("number");
                    prNumbers.add(prNumber);
                }
                reposPRMap.put(repoName, prNumbers);
                continue; // Skip notification for the first check
            }

            List<Integer> lastPRNumbers = reposPRMap.get(repoName);
            List<Integer> newPRNumbers = new ArrayList<>();

            for (int j = 0; j < pullRequests.length(); j++) {
                JSONObject pr = pullRequests.getJSONObject(j);
                int prNumber = pr.getInt("number");
                newPRNumbers.add(prNumber);

                // Check if the pull request number is new
                if (!lastPRNumbers.contains(prNumber)) {
                    trayAdapter.sendNotification(repoName, prNumber);
                }
            }

            // Update the last recorded pull request numbers
            reposPRMap.put(repoName, newPRNumbers);
        }
        storageService.saveReposPRList(reposPRMap);
    }

}
