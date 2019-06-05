package com.example.mvppattern.view;

import com.example.mvppattern.bean.CatBean;

import java.util.List;

public interface ICatView {

    void showLoading();

    void  showCats(List<CatBean> catBeans);

}
