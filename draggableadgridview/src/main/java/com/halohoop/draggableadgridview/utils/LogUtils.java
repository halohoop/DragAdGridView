package com.halohoop.draggableadgridview.utils;

import android.util.Log;

/**
 * Created by halohoop on 2016/5/28.
 */
public class LogUtils {
    public static String TAG = "Halohoop";
    public static String defaultPreStr = "HalohoopNote:------";

    public static void i(Object logStr) {
        Log.i(TAG, defaultPreStr + logStr);
    }

    public static void e(Object logStr) {
        Log.e(TAG, defaultPreStr + logStr);
    }

    public static void d(Object logStr) {
        Log.d(TAG, defaultPreStr + logStr);
    }

    public static void v(Object logStr) {
        Log.v(TAG, defaultPreStr + logStr);
    }

    public static void w(String logStr) {
        Log.w(TAG, defaultPreStr + logStr);
    }

    public static void w(String cusPreLogStr, String logStr) {
        Log.w(TAG, cusPreLogStr + logStr);
    }

    public static void i(String cusPreLogStr, String logStr) {
        Log.i(TAG, cusPreLogStr + logStr);
    }

    public static void e(String cusPreLogStr, String logStr) {
        Log.e(TAG, cusPreLogStr + logStr);
    }

    public static void d(String cusPreLogStr, String logStr) {
        Log.d(TAG, cusPreLogStr + logStr);
    }

    public static void v(String cusPreLogStr, String logStr) {
        Log.v(TAG, cusPreLogStr + logStr);
    }


}
