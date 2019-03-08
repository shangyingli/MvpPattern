package com.example.mvppattern.Utils;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class Logger {

    private static boolean debug = true;

    public static void setDebug(boolean debug) {
        Logger.debug = debug;
    }

    public static boolean isDebug() {
        return debug;
    }

    private static String getStackTrace() {
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        String threadName = "";
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            threadName = Thread.currentThread().getName();
        }

        StackTraceElement currentStack = stackTraceElement[4];
        return "[MVP_PATTERN]" + threadName + "(" + currentStack.getFileName() + ":"
                + currentStack.getLineNumber() + ")."
                + currentStack.getMethodName() + "()";
    }

    ///////////////////////////////////////////////////////////////////////////
    // 简化版本日志记录, tag = [Transsion_Ad]
    ///////////////////////////////////////////////////////////////////////////

    public static void i(String message) {
        if (debug) {
            Log.d(getStackTrace(), message);
        }
    }

    public static void d(String message) {
        if (debug) {
            Log.d(getStackTrace(), message);
        }
    }

    public static void json(String json) {
        if (debug && !TextUtils.isEmpty(json)) {
            try {
                int jsonIndent = 2;
                json = json.trim();
                if (json.startsWith("{")) {
                    JSONObject object = new JSONObject(json);
                    String msg = object.toString(jsonIndent);
                    d(msg);
                } else if (json.startsWith("[")) {
                    JSONArray array = new JSONArray(json);
                    d(array.toString(jsonIndent));
                } else {
                    w("invalid json");
                }
            } catch (JSONException e) {
                w("invalid json");
            }
        }
    }

    /**
     * 复杂的字符串拼接以及包含一些耗时的获取字符串放的callable里面, 可以在release时候节约资源
     *
     * @param run 返回一个string即可.
     */
    public static void d(Callable<String> run) {
        if (debug) {
            try {
                Log.d(getStackTrace(), run.call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void w(String message) {
        if (debug) {
            Log.w(getStackTrace(), message);
        }
    }

    public static void e(String message, Throwable e) {
        if (debug) {
            Log.e(getStackTrace(), message, e);
        }
    }

}
