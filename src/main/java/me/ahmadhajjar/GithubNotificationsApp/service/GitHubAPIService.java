package me.ahmadhajjar.GithubNotificationsApp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class GitHubAPIService {
    private static final Logger logger = LogManager.getLogger(GitHubAPIService.class);

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    private static final String REPOS_ENDPOINT = "/repos";
    private static final String PULLS_ENDPOINT = "/pulls";
    public static final int ITEMS_PER_PAGE = 10;
    private final Supplier<IllegalStateException> illegalStateExceptionSupplier = () -> new IllegalStateException("Could not get pull requests count!");
    private final String token;

    private final HttpClient client = HttpClient.newHttpClient();

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

    public JSONArray getPullRequestsPage(String reposFullName, Integer perPage, Integer page) throws Exception {
        HttpResponse<String> response = doApiGetRequest(GITHUB_API_BASE_URL + REPOS_ENDPOINT + "/" + reposFullName + PULLS_ENDPOINT + "?per_page=" + perPage + "&page=" + page);

        return new JSONArray(response.body());
    }

    public Integer getPullRequestsCount(String reposFullName) throws Exception {
        HttpResponse<String> response = doApiGetRequest(GITHUB_API_BASE_URL + REPOS_ENDPOINT + "/" + reposFullName + PULLS_ENDPOINT + "?per_page=1");

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw illegalStateExceptionSupplier.get();
        }

        HttpHeaders responseHeaders = response.headers();

        String links = responseHeaders.map().get("Link").get(0);
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
     * @throws IOException          if an I/O error occurs when sending or receiving
     * @throws InterruptedException – if the operation is interrupted
     */
    public JSONArray getLatestPullRequestsForRepo(String reposFullName) throws IOException, InterruptedException {
        logger.debug("Getting pull requests for repo " + reposFullName);

        var prResponse = doApiGetRequest(GITHUB_API_BASE_URL + REPOS_ENDPOINT + "/" + reposFullName + PULLS_ENDPOINT);

        if (prResponse.statusCode() !=  HttpURLConnection.HTTP_OK) {
            logger.error("Error while getting pull requests for repo " + reposFullName);
            logger.error("API returned non OK status : " + prResponse.statusCode());
            return null;
        }

        return new JSONArray(prResponse.body());
    }

    private HttpResponse<String> doApiGetRequest(String uri) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Authorization", "token " + token)
                .build();
        return client.send(request, BodyHandlers.ofString());
    }
}
