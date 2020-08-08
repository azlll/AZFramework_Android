package com.azlll.framework.network;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseCacheEntity {

    private static final String KEY_CACHE_KEY = "cacheKey";
    private static final String KEY_RESPONSE_JSON = "responseJson";
    private static final String KEY_CREATE_TIME = "createTime";
    private static final String KEY_STORAGE_CACHE_TIME = "storageCacheTime";
    private static final String KEY_RAW_CACHE_TIME = "rawCacheTime";

    private String cachekey;
    private String responseJson;
    private long createTime;
    private long rawCacheTime;
    private long storageCacheTime;

    public ResponseCacheEntity() {


    }
    public ResponseCacheEntity(String localStorageJson) {
        try {
            JSONObject jsonObject = new JSONObject(localStorageJson);
            this.cachekey = jsonObject.optString(KEY_CACHE_KEY);
            this.responseJson = jsonObject.optString(KEY_RESPONSE_JSON);
            this.createTime = jsonObject.optLong(KEY_CREATE_TIME);
            this.storageCacheTime = jsonObject.optLong(KEY_STORAGE_CACHE_TIME);
            this.rawCacheTime = jsonObject.optLong(KEY_RAW_CACHE_TIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toLocalStorageJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_CACHE_KEY, this.cachekey);
            jsonObject.put(KEY_RESPONSE_JSON, this.responseJson);
            jsonObject.put(KEY_CREATE_TIME, this.createTime);
            jsonObject.put(KEY_RAW_CACHE_TIME, this.rawCacheTime);
            jsonObject.put(KEY_STORAGE_CACHE_TIME, this.storageCacheTime);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCachekey() {
        return cachekey;
    }

    public void setCachekey(String cachekey) {
        this.cachekey = cachekey;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public void setStorageCacheTime(long storageCacheTime) {
        this.storageCacheTime = storageCacheTime;
    }

    public long getStorageCacheTime() {
        return storageCacheTime;
    }

    public long getRawCacheTime() {
        return rawCacheTime;
    }

    public void setRawCacheTime(long rawCacheTime) {
        this.rawCacheTime = rawCacheTime;
    }
}
