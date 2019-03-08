package com.example.mvppattern.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import com.example.mvppattern.Utils.Logger;
import com.example.mvppattern.present.BasePresenter;

public abstract class BaseActivity<V, T extends BasePresenter<V>> extends Activity {
    protected T mPresenter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Logger.d("此时为横屏");
        } else {
            Logger.d("此时为竖屏");
        }
    }


    protected abstract T createPresenter();

}
