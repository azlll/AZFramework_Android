package com.azlll.framework.network.download;

import android.app.Application;
import android.os.Environment;
import android.os.Handler;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.StringUtils;
import com.azlll.framework.log.ZBBLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadHelper {

    private static String TAG = DownloadHelper.class.getSimpleName();

    private Application application;
    private Handler mainHandler;
    private PriorityBlockingQueue<DownloadTask> taskQueue;
    private Thread downloadThread;
    private OkHttpClient okHttpClient;
    private volatile boolean isDownloadThreadRunning = false;

    public DownloadHelper(Application application, Handler mainHandler) {
        this.application = application;
        this.mainHandler = mainHandler;
        this.taskQueue = new PriorityBlockingQueue();
        this.okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .build();
        this.downloadThread = initDownloadThread();

    }

    private Thread initDownloadThread() {

        ZBBLog.d(TAG, "initDownloadThread()==>");

        this.isDownloadThreadRunning = false;
        if (this.downloadThread != null) {
            this.downloadThread.interrupt();
            this.downloadThread = null;
        }

        this.downloadThread = new Thread() {
            @Override
            public void run() {
                ZBBLog.d(TAG, "start download thread success... start waiting download task...");

                while (isDownloadThreadRunning) {
                    try {
                        DownloadTask task = taskQueue.take();
                        processTask(task);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {

                    }
                }


            }
        };
        this.isDownloadThreadRunning = true;
        this.downloadThread.start();
        return this.downloadThread;
    }

    public void addToDownloadQueue(DownloadTask task) {
        if (isDownloadThreadRunning == false) {
            this.downloadThread = initDownloadThread();
        }
        task.setStatus(EnumDownloadStatus.WAITING);
        this.taskQueue.add(task);
    }

    private void processTask(final DownloadTask task) {
        ZBBLog.d(TAG, "processTask()==> start process a task...  thread=" + Thread.currentThread().toString());


        Request.Builder builder = new Request.Builder()
                .url(task.getRequestUrl())
                .addHeader("Connection", "close");

        // 添加自定义Header
        if (task.getHeader().size() > 0) {
            for (String key : task.getHeader().keySet()) {
                builder.addHeader(key, task.getHeader().get(key));
            }
        }

        Request request = builder.build();

        final long startTime = System.currentTimeMillis();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                long consumeTime = System.currentTimeMillis() - startTime;
                String errMsg = e.getMessage();
                if (StringUtils.isEmpty(errMsg)) {
                    errMsg = e.getLocalizedMessage();
                }
                ZBBLog.e(TAG, "processTask.onFailure()==> errMsg=" + errMsg);

                task.setStatus(EnumDownloadStatus.ERROR);
                task.setException(e);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (task.getOnDownloadListener() != null) {
                            task.getOnDownloadListener().onError(task, e, consumeTime);
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response)throws IOException {

                // 开始下载
                task.setStatus(EnumDownloadStatus.DOWNLOADING);
                long total = response.body().contentLength();
                task.setTotalBytesSize(total);

                // 创建本地用于保存下载文件的路径（不含文件名）
                String destFolderPath = task.getDestFolderPath();
                File dirDestFolderPath = new File(destFolderPath);
                if (!dirDestFolderPath.exists()) {
                    dirDestFolderPath.mkdirs();
                }

                // 创建本地用于保存下载文件的路径（文件绝对路径，含路径+文件名+后缀）
                File fileDest = new File(destFolderPath, task.getDestFilename());
                if (fileDest.exists()) {
                    if (fileDest.length() == total && total > 0) {
                        // 当前已存在此文件，不继续下载
                        // 回调完成
                        String fileMd5 = EncryptUtils.encryptMD5File2String(fileDest).toLowerCase();
                        long consumeTime = System.currentTimeMillis() - startTime;
                        task.setStatus(EnumDownloadStatus.FINISHED_FOUND_SAME_FILE);
                        task.setDownloadedBytesSize(task.getTotalBytesSize());

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (task.getOnDownloadListener() != null) {
                                    task.getOnDownloadListener().onFinish(task
                                            , task.getTotalBytesSize()
                                            , fileMd5
                                            , consumeTime);
                                }
                            }
                        });
                        return;
                    }else {
                        // 覆盖（删除原先的文件）
                        fileDest.delete();
                    }
                }

                // 创建下载流
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos =null;
                try {

                    is = response.body().byteStream();
                    fos = new FileOutputStream(fileDest);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {

                        if (!isDownloadThreadRunning) {
                            // 外部停止运行，立即停止下载
                            break;
                        }

                        // 保存
                        fos.write(buf, 0, len);
                        sum += len;

                        // 更新进度
                        task.setDownloadedBytesSize(sum);
                        float progress = sum * 1.0f / total * 1.0f;
                        int percent = (int) (progress * 100);
                        task.setCurrentPercent(percent);

                        // 回调进度
                        long consumeTime = System.currentTimeMillis() - startTime;

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (task.getOnDownloadListener() != null) {
                                    task.getOnDownloadListener().onProgress(task
                                            , task.getDownloadedBytesSize()
                                            , task.getTotalBytesSize()
                                            , task.getCurrentPercent()
                                            , consumeTime);
                                }
                            }
                        });
                    }
                    fos.flush();

                    if (isDownloadThreadRunning) {
                        // 下载完成
                        task.setDownloadedBytesSize(total);
                        task.setCurrentPercent(100);
                        task.setStatus(EnumDownloadStatus.FINISHED_DOWNLOAD);
                        // 回调完成
                        String fileMd5 = EncryptUtils.encryptMD5File2String(fileDest).toLowerCase();
                        long consumeTime = System.currentTimeMillis() - startTime;

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (task.getOnDownloadListener() != null) {
                                    task.getOnDownloadListener().onFinish(task
                                            , task.getTotalBytesSize()
                                            , fileMd5
                                            , consumeTime);
                                }
                            }
                        });
                    }else{
                        // 取消下载
                        // 清除临时文件
                        if (fileDest.exists()) {
                            // 覆盖（删除原先的文件）
                            fileDest.delete();
                        }
                        task.setStatus(EnumDownloadStatus.CANCLE);
                        long consumeTime = System.currentTimeMillis() - startTime;

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (task.getOnDownloadListener() != null) {
                                    task.getOnDownloadListener().onCancel(task
                                            , consumeTime);
                                }
                            }
                        });

                    }


                }catch (Exception e) {

                    // 下载过程失败
                    task.setStatus(EnumDownloadStatus.ERROR);
                    long consumeTime = System.currentTimeMillis() - startTime;

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (task.getOnDownloadListener() != null) {
                                task.getOnDownloadListener().onError(task
                                        , e
                                        , consumeTime);
                            }
                        }
                    });

                    // 清除临时文件
                    if (fileDest.exists()) {
                        // 覆盖（删除原先的文件）
                        fileDest.delete();
                    }
                }finally {

                    // 关闭流
                    try {
                        if (is !=null) {
                            is.close();
                        }
                        if (fos !=null) {
                            fos.close();
                        }
                    }catch (IOException e) {
                        ZBBLog.e(TAG, e.getMessage());
                    }
                }
            }
        });
    }

    public void cancenAllDownloadTask() {
        this.taskQueue.clear();

        this.isDownloadThreadRunning = false;
        if (this.downloadThread != null) {
            this.downloadThread.interrupt();
            this.downloadThread = null;
        }
    }
}
