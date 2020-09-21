package com.code.gitprofiler.WebHttp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterfaces {

    @GET("search/users")
    Call<ResponseBody> getUsers(@Query("q") String name, @Query("page") int pageNumber);

    @GET("users/{username}/repos")
    Call<ResponseBody> getUserRepositories(@Path("username") String userName);
}

