package me.ahmadhajjar.GithubNotificationsApp.service;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.HttpStatus;
import org.json.JSONArray;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class GitHubAPIService {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    private static final String REPOS_ENDPOINT = "/repos";
    private static final String PULLS_ENDPOINT = "/pulls";
    public static final int ITEMS_PER_PAGE = 10;
    private final Supplier<IllegalStateException> illegalStateExceptionSupplier = () -> new IllegalStateException("Could not get pull requests count!");
    private final String token;

    public GitHubAPIService(String token) {
        this.token = token;
    }

    public JSONArray getPullRequests(String reposFullName) throws Exception {
        Integer reposCount = getPullRequestsCount(reposFullName);

        ExecutorService exec = Executors.newCachedThreadPool();
        List<Callable<JSONArray>> tasks = new ArrayList<>();

        for (int i = 0; i < (reposCount / ITEMS_PER_PAGE) + 1; i++) {
            int page = i;
            Callable<JSONArray> c = () -> getPullRequestsPage(reposFullName, ITEMS_PER_PAGE, page);
            tasks.add(c);
        }
        List<Future<JSONArray>> results = exec.invokeAll(tasks);

        JSONArray allRepos = new JSONArray();
        return results.stream().map(jsonArrayFuture -> {
            try {
                return jsonArrayFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).reduce(allRepos, JSONArray::putAll);
    }

    public JSONArray getPullRequestsPage(String reposFullName, Integer perPage, Integer page) throws UnirestException {
        HttpResponse<String> response = Unirest.get(GITHUB_API_BASE_URL + REPOS_ENDPOINT + "/" + reposFullName + PULLS_ENDPOINT + "?per_page=" + perPage + "&page=" + page)
                .header("Authorization", "token " + token)
                .asString();

        return new JSONArray(response.getBody());
    }

    public Integer getPullRequestsCount(String reposFullName) throws Exception {
        HttpResponse<String> response = Unirest.get(GITHUB_API_BASE_URL + REPOS_ENDPOINT + "/" + reposFullName + PULLS_ENDPOINT + "?per_page=1")
                .header("Authorization", "token " + token)
                .asString();

        if (response.getStatus() != HttpStatus.SC_OK) {
            throw illegalStateExceptionSupplier.get();
        }

        Headers responseHeaders = response.getHeaders();

        String links = responseHeaders.get("Link").get(0);
        String lastLink = Arrays.stream(links.split(",")).filter(s -> s.contains("rel=\"last\"")).findFirst().orElseThrow(illegalStateExceptionSupplier);
        String lastPageUrl = lastLink.substring(lastLink.indexOf('<') + 1, lastLink.indexOf('>'));

        String[] queryParameters = (new URI(lastPageUrl))
                .getQuery()
                .split("&");
        String lastPageNumber = Arrays.stream(queryParameters)
                .filter(i -> i.startsWith("page="))
                .findFirst()
                .orElseThrow(illegalStateExceptionSupplier)
                .split("=")[1];

        return Integer.parseInt(lastPageNumber);
    }

    /**
     * According to the Github API Docs, this call will get latest 30 PRs
     *
     * @param reposFullName String
     * @return {@link JSONArray} The result of the call if the HTTP Request was successful, or empty {@link JSONArray} otherwise
     * @throws UnirestException In case of an HTTP Error
     */
    public JSONArray getLatestPullRequestsForRepo(String reposFullName) throws UnirestException {
        System.out.println("Getting pull requests for repo " + reposFullName);
        HttpResponse<String> prResponse = Unirest.get(GITHUB_API_BASE_URL + REPOS_ENDPOINT + "/" + reposFullName + PULLS_ENDPOINT)
                .header("Authorization", "token " + token)
                .asString();

        if (prResponse.getStatus() != HttpStatus.SC_OK) {
            System.err.println("Error while getting pull requests for repo " + reposFullName);
            System.err.println("API returned non OK status : " + prResponse.getStatus());
            return new JSONArray();
        }

        return new JSONArray(prResponse.getBody());
    }
}
