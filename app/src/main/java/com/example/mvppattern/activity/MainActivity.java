package com.example.mvppattern.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mvppattern.Utils.Logger;
import com.example.mvppattern.R;
import com.example.mvppattern.adapter.CatAdapter;
import com.example.mvppattern.bean.CatBean;
import com.example.mvppattern.present.BasePresenter;
import com.example.mvppattern.present.CatPresenter;
import com.example.mvppattern.view.ICatView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ICatView, BasePresenter<ICatView>> implements ICatView {

    private ListView listView;
    private CatAdapter adapter;
    private List<CatBean> catsList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        mPresenter.fetch();
    }

    private void freshView() {
        if (catsList != null && catsList.size() > 0) {
            Logger.d("freshView");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected BasePresenter<ICatView> createPresenter() {
        return new CatPresenter(this);
    }

    protected void initComponent() {
        listView = findViewById(R.id.cats_list);
        adapter = new CatAdapter(this, catsList);
        listView.setAdapter(adapter);

    }

    @Override
    public void showLoading() {
        Toast.makeText(this, "正在拼命加载", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showCats(List<CatBean> catBeans) {
        this.catsList.clear();
        this.catsList.addAll(catBeans);
        freshView();
    }
}
