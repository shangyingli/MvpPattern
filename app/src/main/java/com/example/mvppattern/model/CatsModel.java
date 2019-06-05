package com.example.mvppattern.model;

import android.content.Context;

public interface CatsModel {

    /**
     * 向后台请求数据，并将数据返回给控制层
     *
     */

    void getCatsData(String url, CallBack callBack);

}
