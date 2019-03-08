package com.example.mvppattern;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mvppattern.adapter.CatAdapter;
import com.example.mvppattern.bean.CatBean;
import com.example.mvppattern.model.CallBack;
import com.example.mvppattern.model.CatsModelImp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CallBack<List<CatBean>> {

    private ListView listView;
    private CatsModelImp catsModel;
    private CatAdapter adapter;
    private List<CatBean> catsList = new ArrayList<>();
    private static final String BASE_URL = "http://169.254.29.48:80/catsDemo/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        catsModel = new CatsModelImp();
        catsModel.getCatsData( BASE_URL, this);
    }

    private void initView() {
        listView = findViewById(R.id.cats_list);
        adapter = new CatAdapter(this, catsList);
        listView.setAdapter(adapter);
    }

    private void freshView() {
        if (catsList != null && catsList.size() > 0) {
            Logger.d("freshView");
            adapter.notifyDataSetChanged();
        }
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

    @Override
    public void onSuccess(List<CatBean> data) {
        catsList.clear();
        catsList.addAll(data);
        Logger.d("onSuccess cats size : " + data.size());
        Toast.makeText(MainActivity.this, "aa", Toast.LENGTH_LONG).show();
        freshView();
    }

    @Override
    public void onFailed(Throwable e) {
        Logger.d("请求出错， 界面无法加载" + e.getMessage());
    }
}
