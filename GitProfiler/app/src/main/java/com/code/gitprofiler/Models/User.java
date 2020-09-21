package com.code.gitprofiler.Models;

import org.json.JSONObject;

public class User {

    private String name;
    private double score;
    private long id;
    private String imageURL;
    private String repositoryURL;
    private String profileURL;

    public User(JSONObject object) {
        name = object.optString("login");
        score = object.optDouble("score");
        repositoryURL = object.optString("repos_url");
        imageURL = object.optString("avatar_url");
        id = object.optLong("id");
        profileURL = object.optString("html_url");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getRepositoryURL() {
        return repositoryURL;
    }

    public void setRepositoryURL(String repositoryURL) {
        this.repositoryURL = repositoryURL;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }
}
