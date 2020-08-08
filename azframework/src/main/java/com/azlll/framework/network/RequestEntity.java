package com.azlll.framework.network;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RequestEntity {

    private String HEADER_AUTHORIZATION_KEY = "Authorization";

    public enum EnumCacheType {
        // 不缓存，仅请求网络
        NONE,
        // 查询RAW缓存，并请求网络
        RAW_ONLY,
        // 查询本地STORAGE，并请求网络
        STORAGE_ONLY,
        // 有RAW返回RAW，无RAW返回STORAGE，然后请求网络
        ALL,

        // 如果有RAW则直接返回，不请求网络
        RAW_NOT_REQUEST,
        // 如果有STORAGE则直接返回，不请求网络
        STORAGE_NOT_REQUEST,
        // 有RAW返回RAW，无RAW返回STORAGE，如果无STORAGE则返回错误，不请求网络
        ALL_NOT_REQUEST
    }

    private NetworkManager.EnumMethod method;
    private EnumCacheType cacheType;
    private String uid;
    private String url;
    private Map<String, String> header;
    private Map<String, Object> paramsToBody;
    private Map<String, Object> paramsToUrl;
    private List<FormDataFileInfo> formDataFileInfos;
    private long timeout;
    private long rawCacheTime;
    private long storageCacheTime;



    public RequestEntity(ApiInfo apiInfo) {
        this.method = apiInfo.getMethod();
        this.uid = UUID.randomUUID().toString();
        this.timeout = -1;
        this.rawCacheTime = -1;
        this.storageCacheTime = -1;
        this.cacheType = EnumCacheType.NONE;
        this.url = apiInfo.getUrl();

        this.header = new HashMap<>();
        this.paramsToBody = new HashMap<>();
        this.paramsToUrl = new HashMap<>();
    }

    public void setMethod(NetworkManager.EnumMethod method) {
        this.method = method;
    }

    public NetworkManager.EnumMethod getMethod() {
        return method;
    }

    public void setCacheType(EnumCacheType cacheType) {
        this.cacheType = cacheType;
    }

    public EnumCacheType getCacheType() {
        return cacheType;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getUrl() {
        return url;
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

    public void addAuthorization(String authorization) {
        addHeader(HEADER_AUTHORIZATION_KEY, authorization);
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

    public void addFormDataFile(File file, String formDataName, String formDataMediaType) {
        if (file != null && file.exists()) {
            if (formDataFileInfos == null) {
                formDataFileInfos = new ArrayList<>();
            }
            formDataFileInfos.add(new FormDataFileInfo(file, formDataName, formDataMediaType));
        }
    }

    public List<FormDataFileInfo> getFormDataFileInfos() {
        return formDataFileInfos;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setRawCacheTime(long rawCacheTime) {
        this.rawCacheTime = rawCacheTime;
    }

    public long getRawCacheTime() {
        return rawCacheTime;
    }

    public void setStorageCacheTime(long storageCacheTime) {
        this.storageCacheTime = storageCacheTime;
    }

    public long getStorageCacheTime() {
        return storageCacheTime;
    }

    public class FormDataFileInfo {

        public static final String MEDIA_TYPE_IMAGE_JPEG = "image/jpeg";
        public static final String MEDIA_TYPE_IMAGE_PNG = "image/png";

        private File file;
        private String fileName;
        private String fullFileName;
        private String formDataName;
        private String formDataMediaType;

        public FormDataFileInfo(File file, String formDataName, String formDataMediaType) {
            this.file = file;
            this.fileName = file.getName();
            this.fullFileName = file.getAbsolutePath();
            this.formDataName = formDataName;
            this.formDataMediaType = formDataMediaType;
        }

        public File getFile() {
            return file;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFullFileName() {
            return fullFileName;
        }

        public String getFormDataName() {
            return formDataName;
        }

        public String getFormDataMediaType() {
            return formDataMediaType;
        }
    }

    public interface OnRequestFinishListener {
        void onResponse(String responseJson, long consumeTime);
        void onServerError(ServerErrorException e, long consumeTime);
        void onNetworkError(IOException e, long consumeTime);
    }

    public interface OnRequestAndCacheFinishListener {
        void onResponseFromCache(boolean isFoundCache, String responseJson);
        boolean onResponseFromNetwork(String responseJson, long consumeTime);
        void onServerError(ServerErrorException e, long consumeTime);
        void onNetworkError(IOException e, long consumeTime);
    }

    public interface OnLoopRequestFinishListener {
        void onResponse(PostLooper postLooper, String responseJson, long consumeTime);
        void onServerError(PostLooper postLooper, ServerErrorException e, long consumeTime);
        void onNetworkError(PostLooper postLooper, IOException e, long consumeTime);
    }

    public interface OnLoopRequestAndCacheFinishListener {
        void onResponseFromCache(PostLooper postLooper, boolean isFoundCache, String responseJson);
        boolean onResponseFromNetwork(PostLooper postLooper, String responseJson, long consumeTime);
        void onServerError(PostLooper postLooper, ServerErrorException e, long consumeTime);
        void onNetworkError(PostLooper postLooper, IOException e, long consumeTime);
    }




    /**
     * @deprecated
     * @return
     */
    public boolean isAddParamsToUrl() {
        return isAddParamsToUrl;
    }

    /**
     * @deprecated
     */
    private boolean isAddParamsToUrl = false;

    /**
     * @deprecated
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * @deprecated
     */
    public RequestEntity(){
        this.method = NetworkManager.EnumMethod.GET;
        this.uid = UUID.randomUUID().toString();
        this.timeout = -1;
        this.rawCacheTime = -1;
        this.storageCacheTime = -1;
        this.cacheType = EnumCacheType.NONE;
        this.url = null;

        this.header = new HashMap<>();
        this.paramsToBody = new HashMap<>();
        this.paramsToUrl = new HashMap<>();
    }

    /**
     * @deprecated
     * @param method
     */
    public RequestEntity(NetworkManager.EnumMethod method){
        this.method = method;
        this.uid = UUID.randomUUID().toString();
        this.timeout = -1;
        this.rawCacheTime = -1;
        this.storageCacheTime = -1;
        this.cacheType = EnumCacheType.NONE;
        this.url = null;

        this.header = new HashMap<>();
        this.paramsToBody = new HashMap<>();
        this.paramsToUrl = new HashMap<>();
    }

    /**
     * @deprecated
     * @param params
     */
    public void setParams(Map<String, Object> params) {
        setParams(params, false);
    }

    /**
     * @deprecated
     * @param params
     * @param isAddParamsToUrl
     */
    public void setParams(Map<String, Object> params, boolean isAddParamsToUrl) {
        this.isAddParamsToUrl = isAddParamsToUrl;
        this.paramsToBody = params;
        if (isAddParamsToUrl) {
            // 适配旧版
            this.paramsToUrl = params;
        }
    }

    /**
     * @deprecated
     * @return
     */
    public Map<String, Object> getParams() {
        return paramsToBody;
    }
    /**
     * @deprecated
     */
    public interface OnPostFinishListener {
        void onResponse(String responseJson, long consumeTime);
        void onNetworkError(IOException e, long consumeTime);
    }

    /**
     * @deprecated
     */
    public interface OnPostAndCacheFinishListener {
        void onResponseFromCache(boolean isFoundCache, String responseJson);
        boolean onResponseFromNetwork(String responseJson, long consumeTime);
        void onNetworkError(IOException e, long consumeTime);
    }

    /**
     * @deprecated
     */
    public interface OnLoopPostFinishListener {
        void onResponse(PostLooper postLooper, String responseJson, long consumeTime);
        void onNetworkError(PostLooper postLooper, IOException e, long consumeTime);
    }

    /**
     * @deprecated
     */
    public interface OnLoopPostAndCacheFinishListener {
        void onResponseFromCache(PostLooper postLooper, boolean isFoundCache, String responseJson);
        boolean onResponseFromNetwork(PostLooper postLooper, String responseJson, long consumeTime);
        void onNetworkError(PostLooper postLooper, IOException e, long consumeTime);
    }
}
