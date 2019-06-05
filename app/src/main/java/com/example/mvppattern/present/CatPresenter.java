package com.example.mvppattern.present;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.example.mvppattern.Utils.Logger;
import com.example.mvppattern.bean.CatBean;
import com.example.mvppattern.cache.CacheUtil;
import com.example.mvppattern.model.CallBack;
import com.example.mvppattern.model.CatsModel;
import com.example.mvppattern.model.CatsModelImp;
import com.example.mvppattern.view.ICatView;

import java.lang.ref.WeakReference;
import java.util.List;

public class CatPresenter extends BasePresenter<ICatView>{

    private String baseUrl = "http://169.254.29.48:80/catsDemo/";

    private ICatView catView;
    private Context context;

    private CatsModel catsModel = new CatsModelImp();

    /**
     * presenter持有view层的引用
     */
    public CatPresenter(Context context) {
        this.catView = mViewReference.get();
        this.context = context;
    }

    @Override
    public void fetch() {
        catView.showLoading();
        if (catsModel != null) {
            catsModel.getCatsData(baseUrl, new TaskCallback(catView));
        }
    }

    static class TaskCallback implements CallBack<List<CatBean>> {

        private WeakReference<ICatView> catView;

        public TaskCallback(ICatView iCatView) {
            catView = new WeakReference<>(iCatView);
        }

        @Override
        public void onSuccess(List<CatBean> data) {
            Logger.d("thread : " + Thread.currentThread().getName());
            if (catView.get() != null) {
                catView.get().showCats(data);
            }
        }

        @Override
        public void onFailed(Throwable e) {
            Logger.d("get cats data failed : " + e.getMessage());
        }
    }

    @Override
    public void loadImage(String url, ImageView view) {
        CacheUtil.getInstance(context).setImageToViewByRxJava(url, view);
    }
}
