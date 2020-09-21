package com.code.gitprofiler.Models;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class RepositoryResponse {
    List<Repository> repositories;

    public RepositoryResponse(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.length() > 0) {
            if (repositories == null) repositories = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                repositories.add(new Repository(jsonArray.optJSONObject(i)));
            }
        }
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }
}
