package me.ahmadhajjar.GithubNotificationsApp.service;

import org.json.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiskStorageService implements StorageService {
    private static final String WATCHED_REPOS_FILE = "watched_repos.data.json";
    private static final String WATCHED_REPOS_KNOWN_PRS_FILE = "watched_repos_known_prs.data.json";
    private static final DiskStorageService instance = new DiskStorageService();

    private DiskStorageService() {
    }

    public static DiskStorageService getInstance() {
        return instance;
    }

    @Override
    public List<String> loadReposList() {
        System.out.println("Loading repos from disk ...");
        List<String> result = new ArrayList<>();
        File file = new File(WATCHED_REPOS_FILE);
        if (file.exists()) {
            try {
                String reposJson = Files.readString(Paths.get(WATCHED_REPOS_FILE));
                JSONArray reposJsonArray = new JSONArray(reposJson);

                result = reposJsonArray.toList().stream().map(Object::toString).toList();
            } catch (IOException e) {
                System.err.println("Error happened during loading the repos from disk ...");
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void saveReposList(List<String> repos) {
        System.out.println("Saving the repos to disk ...");
        try (FileWriter writer = new FileWriter(WATCHED_REPOS_FILE)) {
            writer.write(JSONWriter.valueToString(repos));
        } catch (IOException e) {
            System.err.println("Error happened during saving the repos to disk ...");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, List<Integer>> loadReposPRList() {
        System.out.println("Loading Pull Requests from disk ...");
        Map<String, List<Integer>> result = new HashMap<>();
        File file = new File(WATCHED_REPOS_KNOWN_PRS_FILE);
        if (file.exists()) {
            try {
                String reposJson = Files.readString(Paths.get(WATCHED_REPOS_KNOWN_PRS_FILE));
                JSONObject jsonObject = new JSONObject(reposJson);

                Map<String, Object> map = jsonObject.toMap();

                for (String key : map.keySet()) {
                    List<?> intObjects = (List<?>) map.get(key);
                    List<Integer> ints = new ArrayList<>();

                    for (Object object : intObjects) {
                        ints.add(Integer.parseInt(object.toString()));
                    }
                    result.put(key, ints);
                }
            } catch (IOException e) {
                System.err.println("Error happened during loading the pull requests from disk ...");
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void saveReposPRList(Map<String, List<Integer>> reposPrs) {
        System.out.println("Saving the pull requests to disk ...");
        try (FileWriter writer = new FileWriter(WATCHED_REPOS_KNOWN_PRS_FILE)) {
            writer.write(JSONWriter.valueToString(reposPrs));
        } catch (IOException e) {
            System.err.println("Error happened during saving the pull requests to disk ...");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
