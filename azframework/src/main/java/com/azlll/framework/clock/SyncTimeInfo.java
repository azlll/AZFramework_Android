package com.azlll.framework.clock;

import com.azlll.framework.constant.ZbbCacheConstant;

import org.json.JSONException;
import org.json.JSONObject;

public class SyncTimeInfo {
    private String serverTimeZone;
    private long serverTimeDiff;
    private long serverTimeCacheTime;

    public static long calcTimeDiff(long serverUnixTimeStampMillis, long curTime) {
        return serverUnixTimeStampMillis - curTime;
    }

    public long getServerUnixTimeStampMillis() {
        return serverTimeCacheTime + serverTimeDiff;
    }

    public String getServerTimeZone() {
        return serverTimeZone;
    }

    public long getServerTimeDiff() {
        return serverTimeDiff;
    }

    public long getServerTimeCacheTime() {
        return serverTimeCacheTime;
    }

    public SyncTimeInfo(String serverTimeZone, long serverTimeDiff, long serverTimeCacheTime) {
        this.serverTimeZone = serverTimeZone;
        this.serverTimeDiff = serverTimeDiff;
        this.serverTimeCacheTime = serverTimeCacheTime;
    }

    public SyncTimeInfo(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            this.serverTimeZone = jsonObject.optString(ZbbCacheConstant.ClockCache.SUB_KEY_SERVER_TIME_ZONE);
            this.serverTimeDiff = jsonObject.optLong(ZbbCacheConstant.ClockCache.SUB_KEY_SERVER_TIME_DIFF_MILLIS);
            this.serverTimeCacheTime = jsonObject.optLong(ZbbCacheConstant.ClockCache.SUB_KEY_SERVER_TIME_CACHE_TIME);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toJsonString() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(ZbbCacheConstant.ClockCache.SUB_KEY_SERVER_TIME_ZONE, this.serverTimeZone);
            jsonObject.put(ZbbCacheConstant.ClockCache.SUB_KEY_SERVER_TIME_DIFF_MILLIS, this.serverTimeDiff);
            jsonObject.put(ZbbCacheConstant.ClockCache.SUB_KEY_SERVER_TIME_CACHE_TIME, this.serverTimeCacheTime);
            return jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
