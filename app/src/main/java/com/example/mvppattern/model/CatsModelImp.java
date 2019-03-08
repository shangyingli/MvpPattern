package com.example.mvppattern.model;

import android.annotation.SuppressLint;

import com.example.mvppattern.BuildConfig;
import com.example.mvppattern.Utils.Logger;
import com.example.mvppattern.bean.CatBean;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatsModelImp implements CatsModel {

    public static final int LOG_NONE = 0;
    public static final int LOG_INFO = 1;
    public static final int LOG_DEBUG = 2;
    private Disposable disposable;
    private String baseUrl = "http://169.254.29.48:80/catsDemo/";

    // 可重试次数
    private int maxConnectCount = 10;
    // 当前已重试次数
    private int currentRetryCount = 0;
    // 重试等待时间
    private int waitRetryTime = 0;

    public CatsModelImp() {

    }

    /**
     * 使用rxJava + Retrofit实现请求
     *
     * @param baseUrl
     * @param callBack
     */
    @Override
    public void getCatsData(String baseUrl, final CallBack callBack) {
        int loggable = BuildConfig.DEBUG ? LOG_DEBUG : LOG_INFO;
        HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.NONE;
        switch (loggable) {
            case LOG_NONE:
                logLevel = HttpLoggingInterceptor.Level.NONE;
                break;
            case LOG_DEBUG:
                logLevel = HttpLoggingInterceptor.Level.BODY; //打印最多，包含body和header
                break;
            case LOG_INFO:
                logLevel = HttpLoggingInterceptor.Level.BASIC;//仅打印请求基本信息，不包括body和header
                break;
        }
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(logLevel);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //动态传header
                        Request originRequest = chain.request();
                        Request newRequest = originRequest.newBuilder()
                                .header("country", Locale.getDefault().getCountry())
                                .header("language", Locale.getDefault().getLanguage())
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ApiService apiService = retrofit.create(ApiService.class);
        //传body
        final HashMap<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("userName", "liyishan");
        requestBody.put("password", "123455");
        Observable<List<CatBean>> observableCats = apiService.getCatsData(requestBody);
        observableCats
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                Logger.d("失败原因 : " + throwable.getMessage());
                                if (throwable instanceof IOException) {
                                    if (currentRetryCount <= maxConnectCount) {
                                        currentRetryCount ++;
                                        Logger.d("当前重试次数为 ： " + currentRetryCount);
                                        waitRetryTime = 1000 + currentRetryCount * 1000;
                                        return Observable.just(1).delay(waitRetryTime, TimeUnit.MILLISECONDS);
                                    } else {
                                        return Observable.error(new Throwable("当前重试次数为 : " + currentRetryCount + "大于设定的次数，不再重试!"));
                                    }
                                } else {
                                    return Observable.error(new Throwable("发生了非网络异常（非I/O异常）"));
                                }
                            }
                        });
                    }
                })
                .doOnNext(new Consumer<List<CatBean>>() {
                    @Override
                    public void accept(List<CatBean> catBeans) throws Exception {
                        Logger.d("doOnNext : Thread : " + Thread.currentThread().getName());
                        Logger.d("catsBean : " + catBeans.toString());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CatBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                        // onSubscribe 固定在主线程
                        Logger.d("线程 : " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(List<CatBean> catBeans) {
                        Logger.d("线程 : onNext" + Thread.currentThread().getName());
                        callBack.onSuccess(catBeans);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Logger.d("请求失败!");
                        callBack.onFailed(e);
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("onComplete");
                    }
                });

    }

    @SuppressLint("CheckResult")
    public Observable<ResponseBody> getFileFromWeb(final String fileUrl) {
        int loggable = BuildConfig.DEBUG ? LOG_DEBUG : LOG_INFO;
        HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.NONE;
        switch (loggable) {
            case LOG_NONE:
                logLevel = HttpLoggingInterceptor.Level.NONE;
                break;
            case LOG_DEBUG:
                logLevel = HttpLoggingInterceptor.Level.BODY; //打印最多，包含body和header
                break;
            case LOG_INFO:
                logLevel = HttpLoggingInterceptor.Level.BASIC;//仅打印请求基本信息，不包括body和header
                break;
        }
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(logLevel);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //动态传header
                        Request originRequest = chain.request();
                        Request newRequest = originRequest.newBuilder()
                                .header("country", Locale.getDefault().getCountry())
                                .header("language", Locale.getDefault().getLanguage())
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Observable<ResponseBody> observable = apiService.getFile(fileUrl);
        return observable;
    }

}
