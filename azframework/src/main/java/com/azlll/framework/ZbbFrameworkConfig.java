package com.azlll.framework;

public class ZbbFrameworkConfig {

    /** 网络超时时间（单位：毫秒） */
    private long networkTimeout = 10 * 1000;
    /** 网络内存缓存时间（单位：毫秒） */
    private long networkRawCacheTime = 3 * 1000;
    /** 网络本地缓存时间（单位：毫秒）， 默认10天 */
    private long networkStroageCacheTime = 10 * 24 * 60 * 60 * 1000;

    /** 是否debug模式 */
    private boolean isDebug = false;

    public ZbbFrameworkConfig(boolean isDebug){
        this.isDebug = isDebug;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public long getNetworkTimeout() {
        return networkTimeout;
    }

    public void setNetworkTimeout(long networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    public long getNetworkRawCacheTime() {
        return networkRawCacheTime;
    }

    public void setNetworkRawCacheTime(long networkRawCacheTime) {
        this.networkRawCacheTime = networkRawCacheTime;
    }

    public long getNetworkStroageCacheTime() {
        return networkStroageCacheTime;
    }

    public void setNetworkStroageCacheTime(long networkStroageCacheTime) {
        this.networkStroageCacheTime = networkStroageCacheTime;
    }
}
