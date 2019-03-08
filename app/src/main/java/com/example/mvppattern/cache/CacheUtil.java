package com.example.mvppattern.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.example.mvppattern.Logger;
import com.example.mvppattern.model.CatsModelImp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class CacheUtil {

    private volatile static CacheUtil instance;
    private ImageCache imageCache;
    private Context context;

    public static CacheUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (CacheUtil.class) {
                if (instance == null) {
                    instance = new CacheUtil(context);
                }
            }
        }
        return instance;
    }

    private CacheUtil(Context context) {
        this.context = context;
        Map<String, SoftReference<Bitmap>> cacheMap = new HashMap<>();
        this.imageCache = new ImageCache(cacheMap);
    }

    /**
     * 将从网络下载的图片存入本地缓存和内存缓存
     *
     * @param fileName
     * @param data
     */
    public void putImageToCache(String fileName, byte[] data) {

        //将图片存入本地缓存
        FileUtil.getInstance(context).writeFileToStorage(fileName, data);

        //将图片存入LurCache缓存
        imageCache.put(fileName, BitmapFactory.decodeByteArray(data, 0, data.length));
    }

    /**
     * 使用rxJava从内存缓存中获取资源
     *
     * @param fileMame
     * @return
     */
    private Observable<Bitmap> getFromMemeoryCache(final String fileMame) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                Bitmap bitmap = getImageFromMemoryCache(fileMame);
                if (bitmap != null) {
                    emitter.onNext(bitmap);
                } else {
                    emitter.onComplete();
                }
            }
        });
    }

    /**
     * 使用RxJava从本地缓存中获取资源
     *
     * @param fileName
     * @return
     */
    private Observable<Bitmap> getFromLocal(final String fileName) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                Bitmap bitmap = getImageFromLocal(fileName);
                if (bitmap != null) {
                    emitter.onNext(bitmap);
                } else {
                    emitter.onComplete();
                }
            }
        });
    }

    /**
     * 从本地disk获取缓存
     *
     * @param fileName
     * @return
     */
    private Bitmap getImageFromLocal(String fileName) {
        Bitmap bitmap = null;
        byte[] bytes = FileUtil.getInstance(context).readBytesFromStorage(fileName);
        if (bytes != null) {
            Logger.d("get image from local");
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            //存入LurCache
            imageCache.put(fileName, bitmap);
        }
        return bitmap;
    }

    /**
     * 使用rxjava从网络获取资源
     *
     * @param fileUrl
     * @return
     */
    private Observable<Bitmap> getFromWeb(final String fileUrl) {
        CatsModelImp catsModelImp = new CatsModelImp();
        return catsModelImp
                .getFileFromWeb(fileUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                })
                .map(new Function<InputStream, byte[]>() {
                    @Override
                    public byte[] apply(InputStream inputStream) throws Exception {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] tmp = new byte[1024];
                        int length = 0;
                        while ((length = inputStream.read(tmp)) != -1) {
                            baos.write(tmp, 0, length);
                        }
                        return baos.toByteArray();
                    }
                })
                .doOnNext(new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] bytes) throws Exception {
                        String fileName = fileUrl.substring(fileUrl.lastIndexOf(File.separator) + 1);
                        CacheUtil.getInstance(context).putImageToCache(getMd5(fileName), bytes);
                    }
                })
                .map(new Function<byte[], Bitmap>() {
                    @Override
                    public Bitmap apply(byte[] bytes) throws Exception {
                        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                });

    }


    /**
     * 从内存缓存获取图片
     *
     * @param fileName 图片文件名， 一般为图片名的md5
     * @return
     */
    private Bitmap getImageFromMemoryCache(String fileName) {
        //先从LurCache中取图片
        Bitmap bitmap;
        bitmap = imageCache.get(fileName);
        if (bitmap != null) Logger.d("get Image from LruCache");
        if (bitmap == null) {
            // LurCache中不存在， 从软引用中找
            Map<String, SoftReference<Bitmap>> cacheMap = imageCache.getCacheMap();
            SoftReference<Bitmap> softReference = cacheMap.get(fileName);
            if (softReference != null) {
                Logger.d("get image from softReference");
                bitmap = softReference.get();
                //bitmap存入LurCache
                imageCache.put(fileName, bitmap);
            }
        }
        return bitmap;
    }

    /**
     * 使用rxjava实现从三级缓存中获取图片资源,并加载到imageview上
     *
     * @param fileUrl
     * @param imageView
     */
    @SuppressWarnings("unchecked")
    public void setImageToViewByRxJava(final String fileUrl, final ImageView imageView) {
        final String fileName = fileUrl.substring(fileUrl.lastIndexOf(File.separator) + 1);
        //添加队列， 使得同一时间只能有固定的任务
        Disposable disposable = Observable.concat(getFromMemeoryCache(getMd5(fileName)), getFromLocal(getMd5(fileName)), getFromWeb(fileUrl))
                .firstElement()
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 将指定路径的图片加载到imageView上
     *
     * @param fileUrl
     * @param imageView
     */

    @Deprecated
    @SuppressLint("CheckResult")
    public void setImageToView(final String fileUrl, final ImageView imageView) {
        String fileName = getMd5(fileUrl.substring(fileUrl.lastIndexOf(File.separator) + 1));
        Bitmap bitmap = getImageFromMemoryCache(fileName); //从内存缓存取
        if (bitmap != null) {
            Logger.d("get image from memory cache");
            imageView.setImageBitmap(bitmap);
        } else {
            Logger.d("get image from disk cache");
            bitmap = getImageFromLocal(fileName);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Logger.d("get image from web");
                //缓存中没有该图片， 从网络获取
                getFromWeb(fileUrl)
                        .subscribe(new Consumer<Bitmap>() {
                            @Override
                            public void accept(Bitmap bitmap) throws Exception {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
            }
        }
    }

    /**
     * 获取str的MD5码
     *
     * @param str
     * @return
     */
    private String getMd5(String str) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("md5");
            digest.update(str.getBytes());
            byte[] b = digest.digest();
            return bytesToHexString(b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //MD5出错则返回hash值
            return String.valueOf(str.hashCode());
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


}
