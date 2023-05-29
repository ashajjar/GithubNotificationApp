package me.ahmadhajjar.GithubNotificationsApp;

import org.json.JSONArray;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class GitHubAPIService {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com/";
    private static final String ORGS_ENDPOINT = "orgs/";
    private static final String REPOS_ENDPOINT = "/repos";
    private static final String PULLS_ENDPOINT = "/pulls";
    private final String token;

    public GitHubAPIService(String token) {
        this.token = token;
    }

    public JSONArray getRepos(String orgName) throws Exception {
        HttpResponse<String> response = Unirest.get(GITHUB_API_BASE_URL + ORGS_ENDPOINT + orgName + REPOS_ENDPOINT)
                .header("Authorization", "token " + token)
                .asString();
        return new JSONArray(response.getBody());
    }

    public JSONArray getPullRequestsForRepo(String orgName, String repoName) throws Exception {
        HttpResponse<String> prResponse = Unirest.get(GITHUB_API_BASE_URL + "repos/" + orgName + "/" + repoName + PULLS_ENDPOINT)
                .header("Authorization", "token " + token)
                .asString();
        return new JSONArray(prResponse.getBody());
    }
}
