package com.code.gitprofiler.Models;

import org.json.JSONObject;

public class Repository {

    private String repositoryName;
    private String language;

    public Repository(JSONObject jsonObject) {
        repositoryName = jsonObject.optString("name");
        language = jsonObject.optString("language");
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getLanguage() {
        return language;
    }
}
