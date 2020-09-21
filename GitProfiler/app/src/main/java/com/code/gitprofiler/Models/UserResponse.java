package com.code.gitprofiler.Models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserResponse {

    private String totalCount;
    private boolean inCompleteResults;
    private List<User> userList;

    public UserResponse(JSONObject jsonObject) {
        totalCount = jsonObject.optString("total_count");
        inCompleteResults = jsonObject.optBoolean("incomplete_results");

        JSONArray userArray = jsonObject.optJSONArray("items");

        if (userArray != null && userArray.length() > 0) {
            if (userList == null) userList = new ArrayList<>();

            for (int userIndex = 0; userIndex < userArray.length(); userIndex++) {
                userList.add(new User(userArray.optJSONObject(userIndex)));
            }
        }
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isInCompleteResults() {
        return inCompleteResults;
    }

    public void setInCompleteResults(boolean inCompleteResults) {
        this.inCompleteResults = inCompleteResults;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
