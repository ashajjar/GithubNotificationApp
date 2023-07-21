package me.ahmadhajjar.GithubNotificationsApp.service;

import java.util.List;
import java.util.Map;

interface StorageService {
    List<String> loadReposList();

    void saveReposList(List<String> repos);

    Map<String, List<Integer>> loadReposPRList();

    void saveReposPRList(Map<String, List<Integer>> repos);
}
