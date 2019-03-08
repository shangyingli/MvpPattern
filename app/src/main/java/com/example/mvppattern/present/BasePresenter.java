package com.example.mvppattern.present;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<T> {

    public abstract void fetch();

    protected WeakReference<T> mViewReference;

    public void attachView(T view) {
        mViewReference = new WeakReference<T>(view);
    }

    public void detachView() {
        if (mViewReference != null) {
            mViewReference.clear();
            mViewReference = null;
        }
    }
}
