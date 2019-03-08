package com.example.mvppattern.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.example.mvppattern.Logger;

import java.lang.ref.SoftReference;
import java.util.Map;

public class ImageCache extends LruCache<String, Bitmap> {

    //软引用，将从LruCache中被移除的图片存入软引用
    private Map<String, SoftReference<Bitmap>> cacheMap;

    public ImageCache(Map<String, SoftReference<Bitmap>> cacheMap) {
        super((int) (Runtime.getRuntime().maxMemory() / 8));
        this.cacheMap = cacheMap;
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        Logger.d("key : " + key + "value : " + value);
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        Logger.d(key + " was removed");
        if (oldValue != null) {
            SoftReference<Bitmap> softReference = new SoftReference<>(oldValue);
            Logger.d(key + " was saved into soft Reference");
            cacheMap.put(key, softReference);
        }
    }

    public Map<String, SoftReference<Bitmap>> getCacheMap() {
        return cacheMap;
    }
}
