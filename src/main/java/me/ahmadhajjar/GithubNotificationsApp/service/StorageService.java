package me.ahmadhajjar.GithubNotificationsApp.service;

import me.ahmadhajjar.GithubNotificationsApp.PR;

import java.util.List;
import java.util.Map;

public interface StorageService {
    List<String> loadReposList();

    void saveReposList(List<String> repos);

    Map<String, List<PR>> loadReposPRList();

    void saveReposPRList(Map<String, List<PR>> repos);
}
