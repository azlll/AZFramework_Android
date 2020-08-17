package com.azlll.framework.network.download;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DownloadTask implements Comparable<DownloadTask> {

    public static final int PRIORITY_LOWEST    = 0;
    public static final int PRIORITY_LOW       = 100;
    public static final int PRIORITY_MIDDLE    = 200;
    public static final int PRIORITY_HIGH      = 300;
    public static final int PRIORITY_HIGHEST   = 999999;

    private Context context;
    private String requestUrl;
    private Map<String, String> header;
    private Map<String, Object> paramsToBody;
    private Map<String, Object> paramsToUrl;

    private String[] subPath;
    private EnumDownloadDestinationType destType;
    private String destFilename;

    private double downloadedBytesSize;
    private double totalBytesSize;
    private int currentPercent;
    private long consumeTime;
    private Exception exception;

    private UUID uuid;
    private EnumDownloadStatus status;
    /**
     * 优先级
     */
    private int priority = 0;
    private OnDownloadListener onDownloadListener;


    public DownloadTask() {
        this.uuid = UUID.randomUUID();
        this.priority = PRIORITY_MIDDLE;
        this.header = new HashMap<>();
        this.paramsToBody = new HashMap<>();
        this.paramsToUrl = new HashMap<>();
        this.status = EnumDownloadStatus.INITED;
    }

    public DownloadTask(int priority) {
        this.uuid = UUID.randomUUID();
        this.priority = priority;
        this.header = new HashMap<>();
        this.paramsToBody = new HashMap<>();
        this.paramsToUrl = new HashMap<>();
        this.status = EnumDownloadStatus.INITED;
    }

    @Override
    public int compareTo(@NonNull DownloadTask o) {
        if (this.priority > o.getPriority()) {
            return 1;
        }
        else if (this.priority < o.getPriority()) {
            return -1;
        }
        else {
            return 0;
        }
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public Map<String, String> getHeader() {
        return header;
    }


    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void addParamToBody(String key, Object obj) {
        paramsToBody.put(key, obj);
    }

    public void addParamToUrl(String key, Object obj) {
        paramsToUrl.put(key, obj);
    }

    public Map<String, Object> getParamsToUrl() {
        return paramsToUrl;
    }

    public void setParamsToUrl(Map<String, Object> paramsToUrl) {
        this.paramsToUrl = paramsToUrl;
    }

    public Map<String, Object> getParamsToBody() {
        return paramsToBody;
    }

    public void setParamsToBody(Map<String, Object> paramsToBody) {
        this.paramsToBody = paramsToBody;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setDestType(EnumDownloadDestinationType destType) {
        this.destType = destType;
    }

    public EnumDownloadDestinationType getDestType() {
        return destType;
    }

    public String[] getSubPath() {
        return subPath;
    }

    public void setDestSubPath(String... subPath) {
        this.subPath = subPath;
    }

    /**
     * 本地存放的文件名，不含路径，仅文件名+后缀
     * @param destFilename
     */
    public void setDestFilename(String destFilename) {
        this.destFilename = destFilename;
    }

    public String getDestFilename() {
        return destFilename;
    }

    public String getDestFolderPath() {

        String baseFolderPath = "";

        switch (this.destType) {

            // 系统路径
            case SYSTEM_DOWNLOADS:
                baseFolderPath = getSystemDirectoryPath(Environment.DIRECTORY_DOWNLOADS);
                break;
            case SYSTEM_MOVIES:
                baseFolderPath = getSystemDirectoryPath(Environment.DIRECTORY_MOVIES);
                break;
            case SYSTEM_MUSIC:
                baseFolderPath = getSystemDirectoryPath(Environment.DIRECTORY_MUSIC);
                break;
            case SYSTEM_PICTURES:
                baseFolderPath = getSystemDirectoryPath(Environment.DIRECTORY_PICTURES);
                break;
            case SYSTEM_DCIM:
                baseFolderPath = getSystemDirectoryPath(Environment.DIRECTORY_DCIM);
                break;
//            case SYSTEM_DOCUMENTS:
//                baseFolderPath = getSystemDirectoryPath(Environment.DIRECTORY_DOCUMENTS);
//                break;


            // 应用外根目录，通常为SD卡根目录
            case EXTERNAL_STORAGE_DIRECTORY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    baseFolderPath = context.getExternalCacheDir().getAbsolutePath();
                }else {
                    baseFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                }
                break;


            // 应用内目录
            case INTERNAL_FILES:
                baseFolderPath = context.getFilesDir().getAbsolutePath();
                break;
            case INTERNAL_CACHE:
            default:
                // 默认时为INTERNAL_CACHE
                baseFolderPath = context.getCacheDir().getAbsolutePath();
                break;
        }

        // 拼接子路径
        if (this.subPath != null && this.subPath.length > 0) {
            for (int i = 0; i < this.subPath.length; i++) {
                String per = subPath[i];
                baseFolderPath = baseFolderPath + File.separator + per;
            }
        }


        return baseFolderPath;

    }

    private String getSystemDirectoryPath(String systemDirectoryType) {

        String systemDirectoryPath = null;

        systemDirectoryPath = Environment.getExternalStoragePublicDirectory(systemDirectoryType).getAbsolutePath();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            systemDirectoryPath = context.getExternalFilesDir(systemDirectoryType).getAbsolutePath();
//        }else {
//            systemDirectoryPath = Environment.getExternalStoragePublicDirectory(systemDirectoryType).getAbsolutePath();
//        }

        return systemDirectoryPath;
    }
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public UUID getUuid() {
        return uuid;
    }

    public OnDownloadListener getOnDownloadListener() {
        return onDownloadListener;
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }


    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }


    public double getDownloadedBytesSize() {
        return downloadedBytesSize;
    }

    public void setDownloadedBytesSize(double downloadedBytesSize) {
        this.downloadedBytesSize = downloadedBytesSize;
    }

    public double getTotalBytesSize() {
        return totalBytesSize;
    }

    public void setTotalBytesSize(double totalBytesSize) {
        this.totalBytesSize = totalBytesSize;
    }

    public int getCurrentPercent() {
        return currentPercent;
    }

    public void setCurrentPercent(int currentPercent) {
        this.currentPercent = currentPercent;
    }

    public long getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(long consumeTime) {
        this.consumeTime = consumeTime;
    }

    public EnumDownloadStatus getStatus() {
        return status;
    }

    public void setStatus(EnumDownloadStatus status) {
        this.status = status;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public interface OnDownloadListener {
        /**
         * 下载过程的进度回调
         * @param task 当前下载任务的对象
         * @param downloadedBytesSize 当前已下载的字节数
         * @param totalBytesSize 该文件总共的字节数
         * @param currentPercent 当前已下载字节的百分比，值为0～100之间整数
         * @param consumeTime 从开始下载开始，当前过去了多少时间（毫秒ms）
         */
        void onProgress(DownloadTask task, double downloadedBytesSize, double totalBytesSize, int currentPercent, long consumeTime);

        /**
         * 文件下载完成回调
         * @param task 当前下载任务的对象
         * @param totalBytesSize 该文件总共的字节数
         * @param fileMd5 该文件的MD5值
         * @param consumeTime 从开始下载开始，当前过去了多少时间（毫秒ms）
         */
        void onFinish(DownloadTask task, double totalBytesSize, String fileMd5, long consumeTime);

        /**
         * 下载过程中主动取消下载
         * @param task 当前下载任务的对象
         * @param consumeTime 从开始下载开始，当前过去了多少时间（毫秒ms）
         */
        void onCancel(DownloadTask task, long consumeTime);

        /**
         * 下载过程中出错的回调
         * @param task 当前下载任务的对象
         * @param ex 错误对象
         * @param consumeTime 从开始下载开始，当前过去了多少时间（毫秒ms）
         */
        void onError(DownloadTask task, Exception ex, long consumeTime);
    }

}