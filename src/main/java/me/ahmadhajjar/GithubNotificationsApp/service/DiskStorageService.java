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
    private static final String WATCHED_REPOS_FILE = "watched_repos.json";
    private static final String WATCHED_REPOS_KNOWN_PRS_FILE = "watched_repos_known_prs.json";
    private static final DiskStorageService instance = new DiskStorageService();

    private DiskStorageService() {
    }

    public static DiskStorageService getInstance() {
        return instance;
    }

    @Override
    public List<String> loadReposList() {
        List<String> result = null;
        File file = new File(WATCHED_REPOS_FILE);
        if (file.exists()) {
            try {
                String reposJson = Files.readString(Paths.get(WATCHED_REPOS_FILE));
                JSONArray reposJsonArray = new JSONArray(reposJson);

                result = reposJsonArray.toList().stream().map(Object::toString).toList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void saveReposList(List<String> repos) {
        try (FileWriter writer = new FileWriter(WATCHED_REPOS_FILE)) {
            writer.write(JSONWriter.valueToString(repos));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, List<Integer>> loadReposPRList() {
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
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void saveReposPRList(Map<String, List<Integer>> reposPrs) {
        try (FileWriter writer = new FileWriter(WATCHED_REPOS_KNOWN_PRS_FILE)) {
            writer.write(JSONWriter.valueToString(reposPrs));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
