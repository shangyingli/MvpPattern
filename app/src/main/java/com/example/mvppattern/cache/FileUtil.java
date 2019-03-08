package com.example.mvppattern.cache;

import android.content.Context;

import com.example.mvppattern.Utils.Logger;

import java.io.File;
import java.io.FileOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

/**
 * 操作内存文件的工具类
 */
public class FileUtil {
    private static FileUtil instance;

    private Context context;

    private FileUtil(Context context) {
        this.context = context;
    }

    public static FileUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (FileUtil.class) {
                if (instance == null) {
                    instance = new FileUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * 将文件存储到本地缓存
     */
    public boolean writeFileToStorage(String fileName, byte[] b) {
        FileOutputStream fos = null;
        try {
            File file = new File(context.getFilesDir(), fileName);
            Logger.d("file : " + fileName);
            fos = new FileOutputStream(file);
            fos.write(b, 0, b.length);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 从内存中读取文件的字节码
     */
    public byte[] readBytesFromStorage(String fileName) {
        byte[] b = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = context.openFileInput(fileName);
            baos = new ByteArrayOutputStream();
            byte[] tmp = new byte[1024];
            int len = 0;
            while ((len = fis.read(tmp)) != -1) {
                baos.write(tmp, 0, len);
            }
            b = baos.toByteArray();
        } catch (Exception e) {
            Logger.d(e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return b;
    }
}
