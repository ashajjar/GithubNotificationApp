package me.ahmadhajjar.GithubNotificationsApp;

//    public void checkReposForOpenPullRequests() throws Exception {
//        JSONArray repos = gitHubAPIService.getRepos(orgName);
//
//        for (int i = 0; i < repos.length(); i++) {
//            JSONObject repo = repos.getJSONObject(i);
//            String repoName = repo.getString("name");
//
//            JSONArray pullRequests = gitHubAPIService.getPullRequestsForRepo(orgName, repoName);
//
//            if (pullRequests.length() > 0) {
//                notificationService.sendNotification(repoName);
//            }
//        }
//    }

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RepoChecker {

    private static final String NOTIFIED_REPOS_FILE = "notified_repos.data.txt";
    private final GitHubAPIService gitHubAPIService;
    private final NotificationService notificationService;
    private final String orgName;
    private final Map<String, JSONArray> lastRepoPRCount;

    public RepoChecker(GitHubAPIService gitHubAPIService, NotificationService notificationService, String orgName) {
        this.gitHubAPIService = gitHubAPIService;
        this.notificationService = notificationService;
        this.orgName = orgName;
        this.lastRepoPRCount = new HashMap<>();
    }

    public void checkReposForOpenPullRequests() throws Exception {
        loadLastRepoPRCount();
        int pagesCount = (gitHubAPIService.getReposCount(orgName) / GitHubAPIService.ITEMS_PER_PAGE) + 1;

        for (int page = 0; page < pagesCount; page++) {
            JSONArray repos = gitHubAPIService.getReposPage(orgName, GitHubAPIService.ITEMS_PER_PAGE, page);
            for (int i = 0; i < repos.length(); i++) {
                JSONObject repo = repos.getJSONObject(i);
                String repoName = repo.getString("name");

                JSONArray pullRequests = gitHubAPIService.getPullRequestsForRepo(orgName, repoName);

                if (!lastRepoPRCount.containsKey(repoName)) {
                    // Store the pull request numbers
                    JSONArray prNumbers = new JSONArray();
                    for (int j = 0; j < pullRequests.length(); j++) {
                        JSONObject pr = pullRequests.getJSONObject(j);
                        int prNumber = pr.getInt("number");
                        prNumbers.put(prNumber);
                    }
                    lastRepoPRCount.put(repoName, prNumbers);
                    continue; // Skip notification for the first check
                }

                JSONArray lastPRNumbers = lastRepoPRCount.get(repoName);
                JSONArray newPRNumbers = new JSONArray();

                for (int j = 0; j < pullRequests.length(); j++) {
                    JSONObject pr = pullRequests.getJSONObject(j);
                    int prNumber = pr.getInt("number");
                    newPRNumbers.put(prNumber);

                    // Check if the pull request number is new
                    if (!lastPRNumbers.toList().contains(prNumber)) {
                        // notificationService.sendNotification(repoName, prNumber);
                    }
                }

                // Update the last recorded pull request numbers
                lastRepoPRCount.put(repoName, newPRNumbers);
            }
            saveLastRepoPRCount();
        }
    }


    private void loadLastRepoPRCount() {
        File file = new File(NOTIFIED_REPOS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    lastRepoPRCount.put(parts[0], new JSONArray(parts[1]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveLastRepoPRCount() {
        try (FileWriter writer = new FileWriter(NOTIFIED_REPOS_FILE)) {
            for (Map.Entry<String, JSONArray> entry : lastRepoPRCount.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
