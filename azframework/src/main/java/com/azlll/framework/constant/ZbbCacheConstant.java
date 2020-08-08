package com.azlll.framework.constant;

import android.os.Environment;

import java.io.File;

public class ZbbCacheConstant {

    /**
     * 早半步Framework的通用Cache目录
     * Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com.azlll.cache"
     * https://blog.csdn.net/qq_32259411/article/details/105275180
     * https://blog.csdn.net/tsdfk1455/article/details/94288585
     * 如果Target SDK > 28，请在manifest中添加android:requestLegacyExternalStorage=“true”
     */
    public static final String ZBB_FRAMEWORK_EXTERNAL_CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com.azlll.cache";

    /**
     * 时钟缓存相关
     */
    public static class ClockCache {

        /**
         * 时钟同步缓存的目录
         * application.getFilesDir().getAbsolutePath() + File.separator + ZbbCacheConstant.ClockCache.CLOCK_CACHE_DIR
         */
        public static final String CLOCK_CACHE_DIR = "zbbClockManager";

        public static final String KEY_SERVER_TIME_INFO = "zbbServerTimeInfo";
        public static final String SUB_KEY_SERVER_TIME_ZONE = "serverTimeZone";
        public static final String SUB_KEY_SERVER_TIME_DIFF_MILLIS = "serverTimeDiffMills";
        public static final String SUB_KEY_SERVER_TIME_CACHE_TIME = "serverTimeCacheTime";

        public static final String KEY_NTP_TIME_INFO = "zbbNtpTimeInfo";
    }

    /**
     * 设备缓存相关
     */
    public static class DeviceCache {

        /**
         * 设备缓存的目录
         * ZBB_FRAMEWORK_EXTERNAL_CACHE_DIR + File.separator + "device"
         */
        public static final String DEVICE_CACHE_DIR = ZBB_FRAMEWORK_EXTERNAL_CACHE_DIR + File.separator + "device";

        /**
         * 设备ID 文件名
         * 无后缀名，防止一些程序能直接打开
         */
        public static final String DEVICE_ID_FILE_NAME = DEVICE_CACHE_DIR + File.separator + "did";

    }

    /**
     * 网络缓存相关
     */
    public static class NetworkCache {

        /**
         * API缓存的目录
         * application.getExternalCacheDir().getAbsolutePath() + File.separator + "http_api"
         */
        public static final String HTTP_API_CACHE_DIR = "http_api";

    }

    /**
     * 临时文件相关
     */
    public static class TempCache {

        /**
         * 临时缓存的目录
         * ZBB_FRAMEWORK_EXTERNAL_CACHE_DIR + File.separator + "tmp"
         */
        public static final String TEMP_CACHE_DIR = ZBB_FRAMEWORK_EXTERNAL_CACHE_DIR + File.separator + "tmp";

        /**
         * 临时图片的存放路径
         */
        public static final String TEMP_IMAGE_DIR = TEMP_CACHE_DIR + File.separator + "img";
        /**
         * 临时文件的存放路径
         */
        public static final String TEMP_OTHER_DIR = TEMP_CACHE_DIR + File.separator + "other";

    }

    /**
     * 日志文件相关
     */
    public static class LogCache {

        /**
         * 日志文件的目录
         * ZBB_FRAMEWORK_EXTERNAL_CACHE_DIR + File.separator + "log"
         */
        public static final String LOG_CACHE_DIR = ZBB_FRAMEWORK_EXTERNAL_CACHE_DIR + File.separator + "log";

    }

    /**
     * Apk文件相关
     */
    public static class ApkCache {

        /**
         * Apk文件的目录
         * ZBB_FRAMEWORK_EXTERNAL_CACHE_DIR + File.separator + "apk"
         */
        public static final String APK_CACHE_DIR = ZBB_FRAMEWORK_EXTERNAL_CACHE_DIR + File.separator + "apk";

        /**
         * 增量文件的目录
         * ZBB_FRAMEWORK_EXTERNAL_CACHE_DIR + File.separator + "patch"
         */
        public static final String APK_PATCH_CACHE_DIR = APK_CACHE_DIR + File.separator + "patch";

    }
}
