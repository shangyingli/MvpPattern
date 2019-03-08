package com.example.mvppattern.model;

public interface CallBack<T> {

    /**
     * 向后台请求数据成功后回调
     * @param data
     */
    void onSuccess(T data);

    /**
     * 向后台请求数据失败后回调
     * @param e
     */
    void onFailed(Throwable e);


}
