package com.example.mvppattern.bean;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * 后台返回的猫数据
 */
public class CatBean {

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String ImageUrl;

    @SerializedName("cuteLevel")
    private int level;

    @SerializedName("desc")
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResponseStr{");
        sb.append("ImageUrl='").append(ImageUrl).append('\'');
        sb.append(", cuteLevel=").append(level);
        sb.append(", desc='").append(desc).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
