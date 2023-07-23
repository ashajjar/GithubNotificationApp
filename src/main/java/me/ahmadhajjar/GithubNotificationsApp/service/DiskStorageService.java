package me.ahmadhajjar.GithubNotificationsApp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

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
    private static final Logger logger = LogManager.getLogger(DiskStorageService.class);

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
        logger.debug("Loading repos from disk ...");
        List<String> result = new ArrayList<>();
        File file = new File(WATCHED_REPOS_FILE);
        if (file.exists()) {
            try {
                String reposJson = Files.readString(Paths.get(WATCHED_REPOS_FILE));
                JSONArray reposJsonArray = new JSONArray(reposJson);

                result = reposJsonArray.toList().stream().map(Object::toString).toList();
            } catch (IOException e) {
                logger.error("Error happened during loading the repos from disk ...");
                logger.error(e);
            }
        }
        return result;
    }

    @Override
    public void saveReposList(List<String> repos) {
        logger.debug("Saving the repos to disk ...");
        try (FileWriter writer = new FileWriter(WATCHED_REPOS_FILE)) {
            writer.write(JSONWriter.valueToString(repos));
        } catch (IOException e) {
            logger.error("Error happened during saving the repos to disk ...");
            logger.error(e);
        }
    }

    @Override
    public Map<String, List<Integer>> loadReposPRList() {
        logger.debug("Loading Pull Requests from disk ...");
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
                logger.error("Error happened during loading the pull requests from disk ...");
                logger.error(e);
            }
        }
        return result;
    }

    @Override
    public void saveReposPRList(Map<String, List<Integer>> reposPrs) {
        logger.debug("Saving the pull requests to disk ...");
        try (FileWriter writer = new FileWriter(WATCHED_REPOS_KNOWN_PRS_FILE)) {
            writer.write(JSONWriter.valueToString(reposPrs));
        } catch (IOException e) {
            logger.error("Error happened during saving the pull requests to disk ...");
            logger.error(e);
        }
    }
}
