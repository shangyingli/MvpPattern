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

import java.util.List;

public class CatAdapter extends BaseAdapter {

    private Context context;
    private List<CatBean> catBeans;

    public CatAdapter(Context context, List<CatBean> catsData) {
        this.context  = context;
        this.catBeans = catsData;
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
        return 0;
    }

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
        //可用glide加载图片
        CatBean catBean = catBeans.get(position);
        CacheUtil.getInstance(context).setImageToViewByRxJava(catBean.getImageUrl(), viewHolder.catView);
        viewHolder.catDesc.setText(catBean.getDesc());
        return convertView;
    }


}
