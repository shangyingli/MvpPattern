package com.example.mvppattern.model;

import com.example.mvppattern.bean.CatBean;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {


    @Streaming
    @GET("CatList.json")
    Observable<List<CatBean>> getCatsData(@QueryMap HashMap<String, String> bodys);

    @Streaming
    @GET
    Observable<ResponseBody> getFile(@Url String url);
}
