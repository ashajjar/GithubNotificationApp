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

    private static final String GITHUB_API_BASE_URL = "https://api.github.com/";
    private static final String ORGS_ENDPOINT = "orgs/";
    private static final String REPOS_ENDPOINT = "/repos";
    private static final String PULLS_ENDPOINT = "/pulls";
    public static final int ITEMS_PER_PAGE = 10;
    private final Supplier<IllegalStateException> illegalStateExceptionSupplier = () -> new IllegalStateException("Could not get repos count!");
    private final String token;

    public GitHubAPIService(String token) {
        this.token = token;
    }

    public JSONArray getRepos(String orgName) throws Exception {
        Integer reposCount = getReposCount(orgName);

        ExecutorService exec = Executors.newCachedThreadPool();
        List<Callable<JSONArray>> tasks = new ArrayList<>();

        for (int i = 0; i < (reposCount / ITEMS_PER_PAGE) + 1; i++) {
            int page = i;
            Callable<JSONArray> c = () -> getReposPage(orgName, ITEMS_PER_PAGE, page);
            tasks.add(c);
        }
        List<Future<JSONArray>> results = exec.invokeAll(tasks);

//        for (Future<JSONArray> future : results) {
//            future.get();
//        }
        JSONArray allRepos = new JSONArray();
        return results.stream().map(jsonArrayFuture -> {
            try {
                return jsonArrayFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).reduce(allRepos, JSONArray::putAll);
    }

    public JSONArray getReposPage(String orgName, Integer perPage, Integer page) throws UnirestException {
        HttpResponse<String> response = Unirest.get(GITHUB_API_BASE_URL + ORGS_ENDPOINT + orgName + REPOS_ENDPOINT + "?per_page=" + perPage + "&page=" + page)
                .header("Authorization", "token " + token)
                .asString();

        return new JSONArray(response.getBody());
    }

    public Integer getReposCount(String orgName) throws Exception {
        HttpResponse<String> response = Unirest.get(GITHUB_API_BASE_URL + ORGS_ENDPOINT + orgName + REPOS_ENDPOINT + "?per_page=1")
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

    public JSONArray getPullRequestsForRepo(String orgName, String repoName) throws Exception {
        HttpResponse<String> prResponse = Unirest.get(GITHUB_API_BASE_URL + "repos/" + orgName + "/" + repoName + PULLS_ENDPOINT)
                .header("Authorization", "token " + token)
                .asString();
        return new JSONArray(prResponse.getBody());
    }
}
