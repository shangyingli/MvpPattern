package com.example.mvppattern.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mvppattern.Utils.Logger;
import com.example.mvppattern.R;
import com.example.mvppattern.bean.CatBean;
import com.example.mvppattern.cache.CacheUtil;
import com.example.mvppattern.present.BasePresenter;

import java.util.List;

public class CatAdapter extends BaseAdapter {

    private Context context;
    private List<CatBean> catBeans;
    private BasePresenter presenter;

    public CatAdapter(Context context, List<CatBean> catsData, BasePresenter presenter) {
        this.context  = context;
        this.catBeans = catsData;
        this.presenter = presenter;
        Logger.d("size : " + catsData.size());
    }

    private class ViewHolder {
        private ImageView catView;
        private TextView  catDesc;
    }

    @Override
    public int getCount() {
        if (catBeans == null) return 0;
        return catBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return catBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * getView会被调用多次的原因是因为 ： 在加载view时， 会走onLayout, OnMeasure方法， 里面会调用getView方法
     * 根本原因是listView没有固定高度
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.view_item_cats, null);
            viewHolder = new ViewHolder();
            viewHolder.catView = convertView.findViewById(R.id.cat_image);
            viewHolder.catDesc = convertView.findViewById(R.id.cat_desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Logger.d("position : " + position);
        CatBean catBean = catBeans.get(position);
        presenter.loadImage(catBean.getImageUrl(), viewHolder.catView);
        viewHolder.catDesc.setText(catBean.getDesc());
        return convertView;
    }

}
