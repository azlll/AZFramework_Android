package com.azlll.framework.log;

import android.app.Application;
import android.util.Log;

import com.blankj.utilcode.util.FileIOUtils;

import java.util.Date;

/**
 *
 */
public final class AZLog {

    /**
     * 标识当前是否Debug模式，默认false
     * by default is false, you can use setDebug() to set this value
     */
    private static boolean _isDebug = false;
    private static String _logStorageFolderPath = "";

    private AZLog() {
    }

    public static void init(Application application) {
        _isDebug = false;// by default is false, you can use setDebug() to set this value
        _logStorageFolderPath = application.getExternalCacheDir().getAbsolutePath();
    }

    public static void setDebug(boolean isDebug) {
        _isDebug = isDebug;
    }

    public static boolean isDebug() {
        return _isDebug;
    }

    // ------------------重写Log的各个方法-----------------
    public static void v(String tag, String msg) {
        if (_isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (_isDebug) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (_isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (_isDebug) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (_isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (_isDebug) {
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (_isDebug) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (_isDebug) {
            Log.w(tag, msg, tr);
        }
    }

    public static void w(String tag, Throwable tr) {
        if (_isDebug) {
            Log.w(tag, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (_isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (_isDebug) {
            Log.e(tag, msg, tr);
        }
    }


    // ------------------新增Log写入文件的方法-----------------

    public static void d2f(String fileName, String tag, String message) {
        AZLog.d(tag, message);
        AZLog.writeLog2File(fileName, tag, message);
    }

    public static void e2f(String fileName, String tag, String message) {
        AZLog.e(tag, message);
        AZLog.writeLog2File(fileName, tag, message);
    }

    public static void v2f(String fileName, String tag, String message) {
        AZLog.v(tag, message);
        AZLog.writeLog2File(fileName, tag, message);
    }

    public static void i2f(String fileName, String tag, String message) {
        AZLog.i(tag, message);
        AZLog.writeLog2File(fileName, tag, message);
    }

    public static void w2f(String fileName, String tag, String message) {
        AZLog.w(tag, message);
        AZLog.writeLog2File(fileName, tag, message);
    }

    private static void writeLog2File(String fileName, String tag, String message){
        if (_isDebug) {
            Date curDate = new Date();
            String strDate = com.blankj.utilcode.util.TimeUtils.date2String(curDate, "yyyy/MM/dd HH:mm:ss.SSS");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(strDate + "\t");
            stringBuilder.append(tag + "\t");
            stringBuilder.append(message);
            FileIOUtils.writeFileFromString(fileName, stringBuilder.toString(), true);
        }
    }
}
