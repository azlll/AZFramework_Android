package com.azlll.framework.network;

import android.app.Application;
import android.os.Handler;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.azlll.framework.constant.ZbbCacheConstant;
import com.azlll.framework.log.ZBBLog;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkManager {

    private static final String TAG = NetworkManager.class.getSimpleName();

    public enum EnumMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    public interface OnNetworkStatusChangedListener {
        void onConnected();

        void onDisconnected();
    }

    public interface OnResponseHttpCodeErrorListener {
        boolean onError(RequestEntity requestEntity, ServerErrorException e);
    }

//    public static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private Application application;
    /* 监听网络变化的监听器列表 */
    private LinkedList<OnNetworkStatusChangedListener> lstOnNetworkStatusChangedListener;
    /* 监听网络返回的服务器错误的监听器列表 */
    private LinkedList<OnResponseHttpCodeErrorListener> lstOnResponseHttpCodeErrorListener;
    /* 主线程的Handler */
    private Handler mainHandler = null;
    /* OkHttpClient */
    private OkHttpClient okHttpClient = null;
    /**
     * 网络超时时间（单位：毫秒）
     */
    private long networkTimeout = 10 * 1000;
    /**
     * 内存缓存时间（单位：毫秒）
     */
    private long networkRawCacheTime = 3 * 1000;
    /**
     * 本地缓存时间（单位：毫秒）， 默认10天
     */
    private long networkStroageCacheTime = 10 * 24 * 60 * 60 * 1000;
    /**
     * 本地缓存文件夹路径
     */
    private String networkStorageFolderPath;

    private Map<String, ResponseCacheEntity> mapResponseCache;

    /**
     * 正在运行的PostLooper列表
     */
    private Map<UUID, PostLooper> mapPostLooper;

    public NetworkManager(Application application, long networkTimeout, long networkRawCacheTime, long networkStroageCacheTime) {
        // 保存变量
        this.application = application;
        this.networkTimeout = networkTimeout;
        this.networkRawCacheTime = networkRawCacheTime;
        this.networkStroageCacheTime = networkStroageCacheTime;
        this.networkStorageFolderPath = application.getExternalCacheDir().getAbsolutePath() + File.separator + ZbbCacheConstant.NetworkCache.HTTP_API_CACHE_DIR;
        File fileStorageFolder = new File(this.networkStorageFolderPath);
        if (!FileUtils.isFileExists(fileStorageFolder)) {
            // 如果缓存路径不存在，则创建路径
            fileStorageFolder.mkdirs();
        }
        // 初始化网络状态监听
        lstOnNetworkStatusChangedListener = new LinkedList<>();
        NetworkUtils.registerNetworkStatusChangedListener(new NetworkUtils.OnNetworkStatusChangedListener() {
            @Override
            public void onDisconnected() {
                for (OnNetworkStatusChangedListener listener : lstOnNetworkStatusChangedListener) {
                    listener.onDisconnected();
                }
            }

            @Override
            public void onConnected(NetworkUtils.NetworkType networkType) {
                for (OnNetworkStatusChangedListener listener : lstOnNetworkStatusChangedListener) {
                    listener.onConnected();
                }
            }
        });
        // 初始化网络返回错误码监听
        lstOnResponseHttpCodeErrorListener= new LinkedList<>();
        // 初始化Http请求
        mainHandler = new Handler(application.getMainLooper());
//        okHttpClient = new OkHttpClient();
        okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(10 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(10 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(10 * 1000, TimeUnit.MILLISECONDS)
                .build();

        mapResponseCache = new HashMap<>();

        mapPostLooper = new HashMap<>();
    }

    public boolean isConnected() {
        return NetworkUtils.isConnected();
    }

    public void addOnNetworkStatusChangedListener(OnNetworkStatusChangedListener listener) {
        lstOnNetworkStatusChangedListener.add(listener);
    }
    public void addOnResponseHttpCodeErrorListener(OnResponseHttpCodeErrorListener listener) {
        lstOnResponseHttpCodeErrorListener.add(listener);
    }

    /**
     * @deprecated
     * @param requestEntity
     * @param onPostFinishListener
     */
    public void get(RequestEntity requestEntity, final RequestEntity.OnPostFinishListener onPostFinishListener) {

        requestEntity.setMethod(EnumMethod.GET);
        request(requestEntity, new RequestEntity.OnRequestFinishListener() {
            @Override
            public void onResponse(String responseJson, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onResponse(responseJson, consumeTime);
                }
            }

            @Override
            public void onServerError(ServerErrorException e, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onNetworkError(e, consumeTime);
                }
            }

            @Override
            public void onNetworkError(IOException e, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onNetworkError(e, consumeTime);
                }
            }
        });
    }

    /**
     * @deprecated
     * @param requestEntity
     * @param onPostAndCacheFinishListener
     */
    public void getAndCache(final RequestEntity requestEntity,
                             final RequestEntity.OnPostAndCacheFinishListener onPostAndCacheFinishListener) {

        requestEntity.setMethod(EnumMethod.GET);
        requestWithCache(requestEntity, new RequestEntity.OnRequestAndCacheFinishListener() {
            @Override
            public void onResponseFromCache(boolean isFoundCache, String responseJson) {
                if (onPostAndCacheFinishListener != null) {
                    onPostAndCacheFinishListener.onResponseFromCache(isFoundCache, responseJson);
                }
            }

            @Override
            public boolean onResponseFromNetwork(String responseJson, long consumeTime) {
                if (onPostAndCacheFinishListener != null) {
                    return onPostAndCacheFinishListener.onResponseFromNetwork(responseJson, consumeTime);
                }else {
                    return false;
                }
            }

            @Override
            public void onServerError(ServerErrorException e, long consumeTime) {
                if (onPostAndCacheFinishListener != null) {
                    onPostAndCacheFinishListener.onNetworkError(e, consumeTime);
                }
            }

            @Override
            public void onNetworkError(IOException e, long consumeTime) {
                if (onPostAndCacheFinishListener != null) {
                    onPostAndCacheFinishListener.onNetworkError(e, consumeTime);
                }
            }
        });
    }

    /**
     * @deprecated
     * @param requestEntity
     * @param onPostFinishListener
     */
    public void post(RequestEntity requestEntity,
                     final RequestEntity.OnPostFinishListener onPostFinishListener) {

        requestEntity.setMethod(EnumMethod.POST);
        request(requestEntity, new RequestEntity.OnRequestFinishListener() {
            @Override
            public void onResponse(String responseJson, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onResponse(responseJson, consumeTime);
                }
            }

            @Override
            public void onServerError(ServerErrorException e, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onNetworkError(e, consumeTime);
                }
            }

            @Override
            public void onNetworkError(IOException e, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onNetworkError(e, consumeTime);
                }
            }
        });
    }

    /**
     * @deprecated
     * @param fileList
     * @param requestEntity
     * @param onPostFinishListener
     */
    public void postImage(List<File> fileList, RequestEntity requestEntity,
                          final RequestEntity.OnPostFinishListener onPostFinishListener) {

        requestEntity.setMethod(EnumMethod.POST);
        if (fileList != null && fileList.size() > 0) {
            for (File file: fileList) {
                requestEntity.addFormDataFile(file, "file", RequestEntity.FormDataFileInfo.MEDIA_TYPE_IMAGE_JPEG);
            }
        }
        request(requestEntity, new RequestEntity.OnRequestFinishListener() {
            @Override
            public void onResponse(String responseJson, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onResponse(responseJson, consumeTime);
                }
            }

            @Override
            public void onServerError(ServerErrorException e, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onNetworkError(e, consumeTime);
                }
            }

            @Override
            public void onNetworkError(IOException e, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onNetworkError(e, consumeTime);
                }
            }
        });
    }


    /**
     * @deprecated
     * @param requestEntity
     * @param onPostFinishListener
     */
    public void put(RequestEntity requestEntity,
                     final RequestEntity.OnPostFinishListener onPostFinishListener) {

        requestEntity.setMethod(EnumMethod.PUT);
        request(requestEntity, new RequestEntity.OnRequestFinishListener() {
            @Override
            public void onResponse(String responseJson, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onResponse(responseJson, consumeTime);
                }
            }

            @Override
            public void onServerError(ServerErrorException e, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onNetworkError(e, consumeTime);
                }
            }

            @Override
            public void onNetworkError(IOException e, long consumeTime) {
                if (onPostFinishListener != null) {
                    onPostFinishListener.onNetworkError(e, consumeTime);
                }
            }
        });
    }


    /**
     * 一次网络请求
     * @param requestEntity
     * @param onRequestFinishListener
     */
    public void request(
                final RequestEntity requestEntity,
                final RequestEntity.OnRequestFinishListener onRequestFinishListener) {


        // 拼接URL 和 构造body
        Request.Builder builder = null;
        String url = null;
        JSONObject joPrams = null;
        String jsonParams = null;
        RequestBody body = null;
        MultipartBody.Builder multipartBodyBuilder = null;

        switch (requestEntity.getMethod()) {
            case GET:
                if (requestEntity.getParamsToUrl().size() > 0) {
                    url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToUrl());
                }else {
                    url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToBody());
                }
                builder = new Request.Builder()
                        .url(url)
                        .get();
                break;

            case POST:

                List<RequestEntity.FormDataFileInfo> lstFromDataFileInfos = requestEntity.getFormDataFileInfos();
                if (lstFromDataFileInfos != null && lstFromDataFileInfos.size() > 0) {

                    // 文件上传请求
                    multipartBodyBuilder = new MultipartBody.Builder();
                    multipartBodyBuilder.setType(MultipartBody.FORM);
                    for (int i =0;i<lstFromDataFileInfos.size();i++){
                        RequestEntity.FormDataFileInfo info = lstFromDataFileInfos.get(i);
                        multipartBodyBuilder.addFormDataPart(info.getFormDataName()
                                , info.getFileName()
                                , RequestBody.create(MediaType.parse(info.getFormDataMediaType())
                                        , info.getFile()));
                    }
                    //拼接get请求url
                    if (requestEntity.getParamsToUrl().size() > 0) {
                        url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToUrl());
                    }else {
                        url = requestEntity.getUrl();
//                    url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToBody());
                    }
                    body = multipartBodyBuilder.build();
                    builder = new Request.Builder()
                            .url(url)
                            .post(body);

                } else{

                    // 普通json请求
                    joPrams = new JSONObject(requestEntity.getParamsToBody());
                    jsonParams = joPrams.toString();
                    body = RequestBody.create(MEDIA_TYPE_JSON, jsonParams);
                    url = requestEntity.getUrl();
                    if (requestEntity.getParamsToUrl().size() > 0) {
                        url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToUrl());
                    }
                    builder = new Request.Builder()
                            .url(url)
                            .post(body);
                }

                break;

            case PUT:
                joPrams = new JSONObject(requestEntity.getParamsToBody());
                jsonParams = joPrams.toString();
                body = RequestBody.create(MEDIA_TYPE_JSON, jsonParams);
                if (requestEntity.getParamsToUrl().size() > 0) {
                    url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToUrl());
                }else {
                    url = requestEntity.getUrl();
//                    url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToBody());
                }
                builder = new Request.Builder()
                        .url(url)
                        .put(body);
                break;

            case DELETE:
                // TODO:
                ZBBLog.e(TAG, "request()==> method DELETE is not impl!!!");
                return;

            default:
                ZBBLog.e(TAG, "request()==> requestEntity.getMethod() is invalid!!! method=" + requestEntity.getMethod());
                return;
        }
        // 添加Header
        if (requestEntity.getHeader() != null && requestEntity.getHeader().size() > 0) {
            for (String key : requestEntity.getHeader().keySet()) {
                builder.addHeader(key, requestEntity.getHeader().get(key));
            }
        }
        final Request request = builder.build();

        // 追踪请求日志
        traceRequestLog(requestEntity, url);

        // 初始化OkHttpClient
        OkHttpClient curClient = null;
        if (requestEntity.getTimeout() > -1) {
            OkHttpClient.Builder newBuilder = okHttpClient.newBuilder();
            newBuilder.retryOnConnectionFailure(false)
                    .connectTimeout(requestEntity.getTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(requestEntity.getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(requestEntity.getTimeout(), TimeUnit.MILLISECONDS);
            curClient = newBuilder.build();
        } else {
            curClient = okHttpClient;
        }

        // 初始化缓存时间
        long curRawCacheTime = networkRawCacheTime;
        if (requestEntity.getRawCacheTime() > -1) {
            curRawCacheTime = requestEntity.getRawCacheTime();
        }
        long curStorageCacheTime = networkStroageCacheTime;
        if (requestEntity.getStorageCacheTime() > -1) {
            curStorageCacheTime = requestEntity.getStorageCacheTime();
        }

        // 执行请求
        final long startRequestTime = System.currentTimeMillis();
        curClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final long endRequestTime = System.currentTimeMillis();
                if (!response.isSuccessful()) {
                    final ServerErrorException error = new ServerErrorException(response);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            long consumeTime = (endRequestTime - startRequestTime);
                            ZBBLog.w(TAG, "onRequestFinishListener.onServerError()==> api=" + request.url().toString()
                                    + ", consumeTimeMs=" + String.valueOf(consumeTime) + "ms"
                                    + ", httpStatus=" + error.getHttpStatusCode()
                                    + ", bodyString=" + error.getBodyString()
                                    + ", error=" + error.toString());

                            boolean isInterceptServerErrorCallback = false;
                            List<Boolean> lstIsHandled = new LinkedList<>();
                            // 触发Http返回码监听
                            for (OnResponseHttpCodeErrorListener listener : lstOnResponseHttpCodeErrorListener) {
                                boolean isHandled = listener.onError(requestEntity, error);
                                lstIsHandled.add(isHandled);
                            }
                            for (boolean isHandled : lstIsHandled) {
                                if (isHandled) {
                                    isInterceptServerErrorCallback = true;
                                    break;
                                }
                            }
                            // 判断上层是否处理掉了这次onServerError，处理掉了则不再触发
                            if (isInterceptServerErrorCallback == false) {
                                onRequestFinishListener.onServerError(error, consumeTime);
                            }else{
                                return;
                            }
                        }
                    });

                    return;
                }

                final String responseJson = response.body().string();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        long consumeTime = (endRequestTime - startRequestTime);
                        ZBBLog.d(TAG, "onRequestFinishListener.onResponse()==> api=" + request.url().toString()
                                + ", consumeTimeMs=" + String.valueOf(consumeTime) + "ms"
                                + ", responseJson=" + responseJson);
                        onRequestFinishListener.onResponse(responseJson, consumeTime);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {

                final long endRequestTime = System.currentTimeMillis();
                final IOException error = e;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        long consumeTime = (endRequestTime - startRequestTime);
                        ZBBLog.e(TAG, "onRequestFinishListener.onNetworkError()==> api=" + request.url().toString()
                                + ", consumeTimeMs=" + String.valueOf(consumeTime) + "ms"
                                + ", error=" + error.toString());
                        onRequestFinishListener.onNetworkError(error, consumeTime);
                    }
                });
            }
        });

    }

    /**
     * 循环请求
     * @param requestEntity
     * @param loopSettings
     * @param onLoopRequestFinishListener
     * @return
     */
    public UUID requestWithLoop(
            RequestEntity requestEntity,
            LoopSettings loopSettings,
            final RequestEntity.OnLoopRequestFinishListener onLoopRequestFinishListener) {

        // 构造循环器
        PostLooper postLooper = new PostLooper(UUID.randomUUID(), requestEntity, loopSettings, mainHandler, new PostLooper.onLoopListener() {
            public void onLoop(final PostLooper postLooper, RequestEntity requestEntity) {

                // 循环过程中实际上调用request方法
                request(requestEntity, new RequestEntity.OnRequestFinishListener() {
                    @Override
                    public void onResponse(String responseJson, long consumeTime) {
                        PostHistory history = new PostHistory(responseJson, consumeTime);
                        postLooper.addHistory(history);
                        onLoopRequestFinishListener.onResponse(postLooper, responseJson, consumeTime);
                    }

                    @Override
                    public void onServerError(ServerErrorException e, long consumeTime) {
                        PostHistory history = new PostHistory(e, consumeTime);
                        postLooper.addHistory(history);
                        onLoopRequestFinishListener.onServerError(postLooper, e, consumeTime);
                    }

                    @Override
                    public void onNetworkError(IOException e, long consumeTime) {
                        PostHistory history = new PostHistory(e, consumeTime);
                        postLooper.addHistory(history);
                        onLoopRequestFinishListener.onNetworkError(postLooper, e, consumeTime);
                    }
                });
            }
        });
        mapPostLooper.put(postLooper.getUuid(), postLooper);

        return postLooper.getUuid();

    }

    /**
     * 单次请求并缓存
     * @param requestEntity
     * @param onRequestAndCacheFinishListener
     */
    public void requestWithCache(
            RequestEntity requestEntity,
            final RequestEntity.OnRequestAndCacheFinishListener onRequestAndCacheFinishListener) {

        // 找到缓存，并回调一次
        final String cachekey = EncryptUtils.encryptMD5ToString(requestEntity.getUid() + requestEntity.getUrl());

        boolean isCalledBack = false;
        switch (requestEntity.getCacheType()) {
            case RAW_ONLY:
            case RAW_NOT_REQUEST:
                isCalledBack = callbackResponseRawOnly(requestEntity, cachekey, onRequestAndCacheFinishListener);
                break;
            case STORAGE_ONLY:
            case STORAGE_NOT_REQUEST:
                isCalledBack = callbackResponseStorageOnly(requestEntity, cachekey, onRequestAndCacheFinishListener);
                break;
            case ALL:
            case ALL_NOT_REQUEST:
                isCalledBack = callbackResponseRawOnly(requestEntity, cachekey, onRequestAndCacheFinishListener);
                if (isCalledBack == false) {
                    isCalledBack = callbackResponseStorageOnly(requestEntity, cachekey, onRequestAndCacheFinishListener);
                }
                break;
            case NONE:
            default:
                break;
        }

        // 处理不请求网络的各种CacheType的流程
        if (isCalledBack) {
            // 已经成功回调了FromCache
            switch (requestEntity.getCacheType()) {
                case RAW_NOT_REQUEST:
                case STORAGE_NOT_REQUEST:
                case ALL_NOT_REQUEST:
                    // 已成功回调，所以这3个无需请求的类型直接return，不走下面的网络请求流程
                    return;
                default:
                    break;
            }
        } else {
//            // 没有缓存
//            switch (requestEntity.getCacheType()) {
//                case RAW_NOT_REQUEST:
//                case STORAGE_NOT_REQUEST:
//                case ALL_NOT_REQUEST:
//                    // 没有找到相应缓存，并且不执行网络请求，所以需要回调一个错误信息给前端
//                    callbackErrorFromCache(requestEntity, cachekey, onPostAndCacheFinishListener);
//                    return;
//                default:
//                    break;
//            }
        }

        // 拼接URL 和 构造body
        Request.Builder builder = null;
        String url = null;
        JSONObject joPrams = null;
        String jsonParams = null;
        RequestBody body = null;
        MultipartBody.Builder multipartBodyBuilder = null;

        switch (requestEntity.getMethod()) {
            case GET:
                if (requestEntity.getParamsToUrl().size() > 0) {
                    url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToUrl());
                }else {
                    url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToBody());
                }
                builder = new Request.Builder()
                        .url(url)
                        .get();
                break;

            case POST:

                List<RequestEntity.FormDataFileInfo> lstFromDataFileInfos = requestEntity.getFormDataFileInfos();
                if (lstFromDataFileInfos != null && lstFromDataFileInfos.size() > 0) {

                    // 文件上传请求
                    multipartBodyBuilder = new MultipartBody.Builder();
                    multipartBodyBuilder.setType(MultipartBody.FORM);
                    for (int i =0;i<lstFromDataFileInfos.size();i++){
                        RequestEntity.FormDataFileInfo info = lstFromDataFileInfos.get(i);
                        multipartBodyBuilder.addFormDataPart(info.getFormDataName()
                                , info.getFileName()
                                , RequestBody.create(MediaType.parse(info.getFormDataMediaType())
                                        , info.getFile()));
                    }
                    //拼接get请求url
                    if (requestEntity.getParamsToUrl().size() > 0) {
                        url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToUrl());
                    }else {
                        url = requestEntity.getUrl();
//                    url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToBody());
                    }
                    body = multipartBodyBuilder.build();
                    builder = new Request.Builder()
                            .url(url)
                            .post(body);

                } else{

                    // 普通json请求
                    joPrams = new JSONObject(requestEntity.getParamsToBody());
                    jsonParams = joPrams.toString();
                    body = RequestBody.create(MEDIA_TYPE_JSON, jsonParams);
                    url = requestEntity.getUrl();
                    if (requestEntity.getParamsToUrl().size() > 0) {
                        url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToUrl());
                    }
                    builder = new Request.Builder()
                            .url(url)
                            .post(body);
                }

                break;

            case PUT:
                joPrams = new JSONObject(requestEntity.getParamsToBody());
                jsonParams = joPrams.toString();
                body = RequestBody.create(MEDIA_TYPE_JSON, jsonParams);
                if (requestEntity.getParamsToUrl().size() > 0) {
                    url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToUrl());
                }else {
                    url = requestEntity.getUrl();
//                    url = getParamsUrl(requestEntity.getUrl(), requestEntity.getParamsToBody());
                }
                builder = new Request.Builder()
                        .url(url)
                        .put(body);
                break;

            case DELETE:
                // TODO:
                ZBBLog.e(TAG, "request()==> method DELETE is not impl!!!");
                return;

            default:
                ZBBLog.e(TAG, "request()==> requestEntity.getMethod() is invalid!!! method=" + requestEntity.getMethod());
                return;
        }
        // 添加Header
        if (requestEntity.getHeader() != null && requestEntity.getHeader().size() > 0) {
            for (String key : requestEntity.getHeader().keySet()) {
                builder.addHeader(key, requestEntity.getHeader().get(key));
            }
        }
        final Request request = builder.build();

        // 追踪请求日志
        traceRequestLog(requestEntity, url);

        // 初始化OkHttpClient
        OkHttpClient curClient = null;
        if (requestEntity.getTimeout() > -1) {
            OkHttpClient.Builder newBuilder = okHttpClient.newBuilder();
            newBuilder.retryOnConnectionFailure(false)
                    .connectTimeout(requestEntity.getTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(requestEntity.getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(requestEntity.getTimeout(), TimeUnit.MILLISECONDS);
            curClient = newBuilder.build();
        } else {
            curClient = okHttpClient;
        }

        long curRawCacheTime = networkRawCacheTime;
        if (requestEntity.getRawCacheTime() > -1) {
            curRawCacheTime = requestEntity.getRawCacheTime();
        }
        long curStorageCacheTime = networkStroageCacheTime;
        if (requestEntity.getStorageCacheTime() > -1) {
            curStorageCacheTime = requestEntity.getStorageCacheTime();
        }
        final long fCurStorageCacheTime = curStorageCacheTime;

        final long startRequestTime = System.currentTimeMillis();
        curClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final long endRequestTime = System.currentTimeMillis();
                if (!response.isSuccessful()) {
                    final ServerErrorException error = new ServerErrorException(response);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            long consumeTime = (endRequestTime - startRequestTime);
                            ZBBLog.w(TAG, "onRequestAndCacheFinishListener.onServerError()==> api=" + request.url().toString()
                                    + ", consumeTimeMs=" + String.valueOf(consumeTime) + "ms"
                                    + ", httpStatus=" + error.getHttpStatusCode()
                                    + ", bodyString=" + error.getBodyString()
                                    + ", error=" + error.toString());

                            boolean isInterceptServerErrorCallback = false;
                            List<Boolean> lstIsHandled = new LinkedList<>();
                            // 触发Http返回码监听
                            for (OnResponseHttpCodeErrorListener listener : lstOnResponseHttpCodeErrorListener) {
                                boolean isHandled = listener.onError(requestEntity, error);
                                lstIsHandled.add(isHandled);
                            }
                            for (boolean isHandled : lstIsHandled) {
                                if (isHandled) {
                                    isInterceptServerErrorCallback = true;
                                    break;
                                }
                            }
                            // 判断上层是否处理掉了这次onServerError，处理掉了则不再触发
                            if (isInterceptServerErrorCallback == false) {
                                onRequestAndCacheFinishListener.onServerError(error, consumeTime);
                            }else{
                                return;
                            }
                        }
                    });
                    return;
                }

                final String responseJson = response.body().string();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        long consumeTime = (endRequestTime - startRequestTime);
                        ZBBLog.d(TAG, "onRequestAndCacheFinishListener.onResponseFromNetwork()==> api=" + request.url().toString()
                                + ", consumeTimeMs=" + String.valueOf(consumeTime) + "ms"
                                + ", responseJson=" + responseJson);
                        boolean isNeedCache = onRequestAndCacheFinishListener.onResponseFromNetwork(responseJson, (endRequestTime - startRequestTime));
                        if (isNeedCache) {
                            cacheResponseJson(cachekey, responseJson, fCurStorageCacheTime);
                        } else {
                            removeResponseJson(cachekey);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {

                final long endRequestTime = System.currentTimeMillis();
                final IOException error = e;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        long consumeTime = (endRequestTime - startRequestTime);
                        ZBBLog.e(TAG, "onRequestAndCacheFinishListener.onNetworkError()==> api=" + request.url().toString()
                                + ", consumeTimeMs=" + String.valueOf(consumeTime) + "ms"
                                + ", error=" + error.toString());
                        onRequestAndCacheFinishListener.onNetworkError(error, consumeTime);
                    }
                });
            }
        });
    }

    /**
     * 循环请求并缓存
     * @param requestEntity
     * @param loopSettings
     * @param onLoopRequestAndCacheFinishListener
     * @return
     */
    public UUID requestWithLoopAndCache(
            RequestEntity requestEntity,
            LoopSettings loopSettings,
            final RequestEntity.OnLoopRequestAndCacheFinishListener onLoopRequestAndCacheFinishListener) {

        PostLooper postLooper = new PostLooper(UUID.randomUUID(), requestEntity, loopSettings, mainHandler, new PostLooper.onLoopListener() {
            public void onLoop(final PostLooper postLooper, RequestEntity requestEntity) {
                requestWithCache(requestEntity, new RequestEntity.OnRequestAndCacheFinishListener() {
                    @Override
                    public void onResponseFromCache(boolean isFoundCache, String responseJson) {
                        onLoopRequestAndCacheFinishListener.onResponseFromCache(postLooper, isFoundCache, responseJson);
                    }
                    @Override
                    public boolean onResponseFromNetwork(String responseJson, long consumeTime) {
                        PostHistory history = new PostHistory(responseJson, consumeTime);
                        postLooper.addHistory(history);
                        return onLoopRequestAndCacheFinishListener.onResponseFromNetwork(postLooper, responseJson, consumeTime);
                    }
                    @Override
                    public void onServerError(ServerErrorException e, long consumeTime) {
                        PostHistory history = new PostHistory(e, consumeTime);
                        postLooper.addHistory(history);
                        onLoopRequestAndCacheFinishListener.onServerError(postLooper, e, consumeTime);
                    }
                    @Override
                    public void onNetworkError(IOException e, long consumeTime) {
                        PostHistory history = new PostHistory(e, consumeTime);
                        postLooper.addHistory(history);
                        onLoopRequestAndCacheFinishListener.onNetworkError(postLooper, e, consumeTime);
                    }
                });
            }
        });
        mapPostLooper.put(postLooper.getUuid(), postLooper);
        return postLooper.getUuid();

    }

    /**
     * 上传文件
     * @param requestEntity
     * @param onRequestFinishListener
     * @return 是否可成功执行上传
     */
    private boolean uploadFile(
            RequestEntity requestEntity,
            final RequestEntity.OnRequestFinishListener onRequestFinishListener) {

        if (requestEntity.getMethod() == EnumMethod.POST
                && requestEntity.getFormDataFileInfos() != null
                && requestEntity.getFormDataFileInfos().size() > 0) {

            // 本质上调用request接口，此方法几乎无敌
            request(requestEntity, onRequestFinishListener);
            return true;
        }else{
            ZBBLog.e(TAG, "uploadFile()==> requestEntity.getMethod() is not post!!!! or requestEntity.getFormDataFileInfos() size is <= 0");
            return false;
        }
    }




    private boolean callbackResponseRawOnly(RequestEntity requestEntity, String
            cachekey, RequestEntity.OnRequestAndCacheFinishListener onRequestAndCacheFinishListener) {

        long curTime = System.currentTimeMillis();
        long curRawCacheTime = networkRawCacheTime;
        if (requestEntity.getRawCacheTime() > -1) {
            curRawCacheTime = requestEntity.getRawCacheTime();
        }

        if (mapResponseCache.containsKey(cachekey)) {
            ResponseCacheEntity responseCacheEntity = mapResponseCache.get(cachekey);
            if (curTime - responseCacheEntity.getCreateTime() < curRawCacheTime) {
                // 有效
                onRequestAndCacheFinishListener.onResponseFromCache(true, responseCacheEntity.getResponseJson());
                return true;
            } else {
                // 无效，清除内存
                mapResponseCache.remove(cachekey);
            }
        }
        // 没有找到缓存
        onRequestAndCacheFinishListener.onResponseFromCache(false, null);
        return false;
    }

    private boolean callbackResponseStorageOnly(RequestEntity requestEntity, String
            cachekey, RequestEntity.OnRequestAndCacheFinishListener onRequestAndCacheFinishListener) {

        long curTime = System.currentTimeMillis();
        long curStorageCacheTime = networkStroageCacheTime;
        if (requestEntity.getStorageCacheTime() > -1) {
            curStorageCacheTime = requestEntity.getStorageCacheTime();
        }

        List<File> lstCacheFile = FileUtils.listFilesInDir(this.networkStorageFolderPath);
        File needDeleteCacheFile = null;
        for (File file : lstCacheFile) {
            if (file.getName().equals(cachekey)) {
                // 找到了缓存文件，读出来转成json字符串
                String localStorageJson = FileIOUtils.readFile2String(file);
                ResponseCacheEntity responseCacheEntity = new ResponseCacheEntity(localStorageJson);
                if (curTime - responseCacheEntity.getCreateTime() < curStorageCacheTime) {
                    // 有效
                    onRequestAndCacheFinishListener.onResponseFromCache(true, responseCacheEntity.getResponseJson());
                    return true;
                } else {
                    // 无效，删除文件
                    FileUtils.delete(file);
                    return false;
                }
            }
        }
        // 没有找到缓存
        onRequestAndCacheFinishListener.onResponseFromCache(false, null);

        return false;
    }

    private boolean callbackErrorFromCache(RequestEntity requestEntity, String
            cachekey, RequestEntity.OnRequestAndCacheFinishListener onRequestAndCacheFinishListener) {

        onRequestAndCacheFinishListener.onResponseFromCache(false, null);
        return true;
    }


    /**
     * 缓存本次返回的responseJson到raw和storage
     *
     * @param cachekey
     * @param responseJson
     */
    private void cacheResponseJson(String cachekey, String responseJson, long storageCacheTime) {

        long curTime = System.currentTimeMillis();
        if (mapResponseCache.containsKey(cachekey)) {
            // 删除旧的内存缓存
            mapResponseCache.remove(cachekey);
        }

        ResponseCacheEntity responseCacheEntity = new ResponseCacheEntity();
        responseCacheEntity.setCachekey(cachekey);
        responseCacheEntity.setResponseJson(responseJson);
        responseCacheEntity.setCreateTime(curTime);
        responseCacheEntity.setStorageCacheTime(storageCacheTime);

        // 将本次返回值存到内存RawCache Map中
        mapResponseCache.put(cachekey, responseCacheEntity);

        // 将本次返回值存到文件StorageCache中
        String localStorageJson = responseCacheEntity.toLocalStorageJson();
        String cacheFileFullName = this.networkStorageFolderPath + File.separator + cachekey;
        FileIOUtils.writeFileFromString(cacheFileFullName, localStorageJson);
    }

    /**
     * 删除指定的缓存
     *
     * @param cachekey
     */
    private void removeResponseJson(String cachekey) {
        // 删除旧的内存缓存
        if (mapResponseCache.containsKey(cachekey)) {
            mapResponseCache.remove(cachekey);
        }
        // 删除旧的文件缓存
        String cacheFileFullName = this.networkStorageFolderPath + File.separator + cachekey;
        if (FileUtils.isFileExists(cacheFileFullName)) {
            FileUtils.delete(cacheFileFullName);
        }
    }


    /**
     * 指定Looper的UUID，查找并返回Looper实例
     *
     * @param looperUuid
     * @return
     */
    public PostLooper findPostLooperByUuid(UUID looperUuid) {
        if (mapPostLooper.containsKey(looperUuid)) {
            return mapPostLooper.get(looperUuid);
        } else {
            return null;
        }
    }


    /**
     * 取消指定的looper循环
     *
     * @param looperUuid
     */
    public void cancelPostLooperByUuid(UUID looperUuid) {
        PostLooper postLooper = findPostLooperByUuid(looperUuid);
        if (postLooper != null) {
            postLooper.cancel();
            mapPostLooper.remove(looperUuid);
        }
    }


    /**
     * 取消所有都loop循环
     */
    public void cancenAllPostLoop() {
        for (UUID key : mapPostLooper.keySet()) {
            PostLooper postLooper = mapPostLooper.get(key);
            postLooper.cancel();
        }
        mapPostLooper.clear();
    }

    /**
     * 清除失效的缓存
     * 包含Raw和Storage
     */
    public void removeExpiredCache() {

        long curTime = System.currentTimeMillis();

        // 清除所有过期的RawCache
        for (String key : mapResponseCache.keySet()) {
            ResponseCacheEntity responseCacheEntity = mapResponseCache.get(key);
            long expiredTime = responseCacheEntity.getCreateTime() + responseCacheEntity.getRawCacheTime();
            if (expiredTime - curTime < 0) {
                // 过期
                mapResponseCache.remove(key);
            }
        }

        // 清除所有过期的StorageCache
        List<File> lstCacheFile = FileUtils.listFilesInDir(this.networkStorageFolderPath);
        for (File file : lstCacheFile) {
            // 找到了缓存文件，读出来转成json字符串
            String localStorageJson = FileIOUtils.readFile2String(file);
            ResponseCacheEntity responseCacheEntity = new ResponseCacheEntity(localStorageJson);
            long expiredTime = responseCacheEntity.getCreateTime() + responseCacheEntity.getStorageCacheTime();
            if (expiredTime - curTime < 0) {
                // 过期
                FileUtils.delete(file);
            }
        }
    }

    /**
     * 拼接get请求的url请求地址
     */
    public static String getParamsUrl(String url, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder(url);
        boolean isFirst = true;
        if (null != params) {
            for (String key : params.keySet()) {
                if (key != null && params.get(key) != null) {
                    if (isFirst) {
                        isFirst = false;
                        builder.append("?");
                    } else {
                        builder.append("&");
                    }
                    builder.append(key)
                            .append("=")
                            .append(params.get(key));
                }
            }
        }

        return builder.toString();
    }

    private void traceRequestLog(RequestEntity requestEntity, String realUrl) {
        if (!ZBBLog.isDebug()) {
            return;
        }

        ZBBLog.d(TAG, "request()==> method=" + requestEntity.getMethod()
                + ", api=" + realUrl
                + ", header=" + calcRequestLog_header(requestEntity)
                + ", params=" + calcRequestLog_params(requestEntity)
                + ", formdata=" + calcRequestLog_formdata(requestEntity));
    }

    private String calcRequestLog_header(RequestEntity requestEntity) {
        if (requestEntity.getHeader() == null) {
            return "null";
        }else {
            if (requestEntity.getHeader().size() <= 0) {
                return "empty";
            }else{
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("{");
                int i = 0;
                for (String key : requestEntity.getHeader().keySet()) {
                    Object value = requestEntity.getHeader().get(key);

                    if (i > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(key + ": " + value);
                    i++;
                }
                stringBuilder.append("}");
                return stringBuilder.toString();
            }
        }
    }

    private String calcRequestLog_params(RequestEntity requestEntity) {
        if (requestEntity.getParams() == null) {
            return "null";
        }else {
            if (requestEntity.getParams().size() <= 0) {
                return "empty";
            }else{
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("{");
                int i = 0;
                for (String key : requestEntity.getParams().keySet()) {
                    Object value = requestEntity.getParams().get(key);

                    if (i > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(key + ": " + value);
                    i++;
                }
                stringBuilder.append("}");
                return stringBuilder.toString();
            }
        }
    }

    private String calcRequestLog_formdata(RequestEntity requestEntity) {
        if (requestEntity.getFormDataFileInfos() == null) {
            return "null";
        }else {
            if (requestEntity.getFormDataFileInfos().size() <= 0) {
                return "empty";
            }else{
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("[");
                for (int i=0; i< requestEntity.getFormDataFileInfos().size(); i++) {
                    RequestEntity.FormDataFileInfo info = requestEntity.getFormDataFileInfos().get(i);

                    if (i > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append("{");
                    stringBuilder.append("FormDataName: " + info.getFileName()
                            + ", File=" + info.getFullFileName()
                            + ", MediaType=" + info.getFormDataMediaType());
                    stringBuilder.append("}");
                }
                stringBuilder.append("]");
                return stringBuilder.toString();
            }
        }
    }
}
