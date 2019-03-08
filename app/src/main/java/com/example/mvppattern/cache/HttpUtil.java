package com.example.mvppattern.cache;

import com.example.mvppattern.BuildConfig;
import com.example.mvppattern.model.ApiService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpUtil {

    private static volatile HttpUtil instance;

    public static final int LOG_NONE = 0;
    public static final int LOG_INFO = 1;
    public static final int LOG_DEBUG = 2;

    private static final String BASE_URL = "http://169.254.29.48:80/catsDemo/";


    private HttpUtil() {

    }

    public static HttpUtil getInstance() {
        if (instance == null) {
            synchronized (HttpUtil.class) {
                if (instance == null) {
                    instance = new HttpUtil();
                }
            }
        }
        return instance;
    }

}
