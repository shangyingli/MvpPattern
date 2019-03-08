package com.example.mvppattern.present;

import com.example.mvppattern.Utils.Logger;
import com.example.mvppattern.bean.CatBean;
import com.example.mvppattern.model.CallBack;
import com.example.mvppattern.model.CatsModel;
import com.example.mvppattern.model.CatsModelImp;
import com.example.mvppattern.view.ICatView;

import java.util.List;

public class CatPresenter extends BasePresenter<ICatView>{

    private String baseUrl = "http://169.254.29.48:80/catsDemo/";

    private ICatView catView;

    private CatsModel catsModel = new CatsModelImp();

    public CatPresenter(ICatView catView) {
        this.catView = catView;
    }

    @Override
    public void fetch() {
        catView.showLoading();
        if (catsModel != null) {
            catsModel.getCatsData(baseUrl, new CallBack<List<CatBean>>() {
                @Override
                public void onSuccess(List<CatBean> data) {
                    Logger.d("thread : " + Thread.currentThread().getName());
                    catView.showCats(data);
                }

                @Override
                public void onFailed(Throwable e) {
                    Logger.d("get cats data failed : " + e.getMessage());
                }
            });
        }
    }

}
