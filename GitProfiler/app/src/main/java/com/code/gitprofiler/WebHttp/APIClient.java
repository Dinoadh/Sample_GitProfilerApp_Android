package com.code.gitprofiler.WebHttp;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import timber.log.Timber;

import static com.code.gitprofiler.ApplicationConstants.BASE_URL;

public class APIClient {
    public static final String CLASSTAG = APIClient.class.getSimpleName();
    private static Retrofit retrofit;

    /**
     * This will be useful while getting  logs when application is in debug mode
     * @return okHTTP client object
     */
    private static OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor httpLogging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Timber.d(CLASSTAG, message);
            }
        });
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(httpLogging);

        return okHttpClient.build();
    }
    /**
     * @return Get Retrofit client object for the instance
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
