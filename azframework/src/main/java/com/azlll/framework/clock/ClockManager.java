package com.azlll.framework.clock;

import android.app.Application;
import android.os.SystemClock;

import com.tencent.mmkv.MMKV;
import com.azlll.framework.constant.ZbbCacheConstant;
import com.azlll.framework.log.ZBBLog;

import java.io.File;
import java.util.Random;
import java.util.TimeZone;

public class ClockManager {

    private static final String TAG = ClockManager.class.getSimpleName();
    private static int SYNC_NTP_MAX_RETRY_COUNT = 10;

    /**
     * 可用NTP服务器列表
     * 参考资料：
     * https://blog.csdn.net/yajie_12/article/details/82346256
     * https://help.aliyun.com/document_detail/92704.html
     */
    private static String[] ARRAY_NTP_SERVER = new String[]{
            "cn.pool.ntp.org",
            "0.cn.pool.ntp.org",
            "1.cn.pool.ntp.org",
            "2.cn.pool.ntp.org",
            "3.cn.pool.ntp.org",
            "ntp1.aliyun.com",
            "ntp2.aliyun.com",
            "ntp3.aliyun.com",
            "ntp4.aliyun.com",
            "ntp5.aliyun.com",
            "ntp6.aliyun.com",
            "ntp7.aliyun.com"
    };


    private Application application;
    private String cacheDir;

    /**
     * 标识是否正在同步中
     */
    private volatile boolean isSyncing = false;
    /**
     * NTP同步当前的错误重试次数
     */
    private volatile int ntpErrorRetryCount = 0;
    /**
     * 本次同步过的NTP时间信息
     */
    private SyncTimeInfo ntpSyncTimeInfo = null;
    /**
     * 本次同步过的服务器时间信息
     */
    private SyncTimeInfo serverSyncTimeInfo = null;

    public ClockManager(Application application) {
        this.cacheDir =  application.getFilesDir().getAbsolutePath() + File.separator + ZbbCacheConstant.ClockCache.CLOCK_CACHE_DIR;
        this.application = application;
        // 自动启动NTP时间同步
        syncNtpUtc();
    }

    /**
     * 同步NTP时间
     * https://www.jianshu.com/p/b3e111a5983a
     */
    public void syncNtpUtc() {
        if (isSyncing == false) {
            new Thread() {
                @Override
                public void run() {
                    isSyncing = true;

                    // 失败重试次数
                    ntpErrorRetryCount = 0;

                    while (true && ntpErrorRetryCount < SYNC_NTP_MAX_RETRY_COUNT) {

                        SntpClient sntpClient = new SntpClient();
                        int ntpServerIndex = new Random().nextInt(ARRAY_NTP_SERVER.length);// 随机取一个NTP服务器进行同步
                        String ntpServerHost = ARRAY_NTP_SERVER[ntpServerIndex];
                        ZBBLog.d(TAG, "start sync NTP time... host=" + ntpServerHost);
                        if (sntpClient.requestTime(ntpServerHost, 10 * 1000)) {
                            // 同步成功，退出死循环
                            ZBBLog.d(TAG, "NTP time get success!!!");
                            long now = sntpClient.getNtpTime() + SystemClock.elapsedRealtime() - sntpClient.getNtpTimeReference();
                            setNtpUtcTime(now);
                            break;
                        } else {
                            // 同步失败了，10秒后再试一次
                            ZBBLog.d(TAG, "NTP time get faliure!!! retry in 10 secs...  current retry times:"
                                    + String.valueOf(ntpErrorRetryCount) + "/" + String.valueOf(SYNC_NTP_MAX_RETRY_COUNT));
                            try {
                                Thread.sleep(10 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sntpClient = null;
                            ntpErrorRetryCount++;
                            continue;
                        }
                    }
                    isSyncing = false;

                }
            }.start();
        }else{
            ZBBLog.w(TAG, "NTP syncing...if you want to sync ntp manually, please wait last sync loop finish...  current retry times:"
                    + String.valueOf(ntpErrorRetryCount) + "/" + String.valueOf(SYNC_NTP_MAX_RETRY_COUNT));
        }
    }

    /**
     * 设置服务器时间
     * 需要外部单独调用服务器提供的接口，获取服务器的unix时间戳，结合本次调用接口的耗时，之后此方法进行自动计算
     * @param ntpTimeMills 已同步过的当前时间，单位：秒
     */
    private void setNtpUtcTime(long ntpTimeMills) {

        long curTime = System.currentTimeMillis();
        long serverTimeDiff = SyncTimeInfo.calcTimeDiff(ntpTimeMills ,curTime);
        // 初始化服务器时间类
        ntpSyncTimeInfo = new SyncTimeInfo("UTC", serverTimeDiff, curTime);
        // 将本次同步的时间保存
        MMKV kv = MMKV.defaultMMKV();
        kv.encode(ZbbCacheConstant.ClockCache.KEY_SERVER_TIME_INFO, ntpSyncTimeInfo.toJsonString());
    }

    /**
     * 是否同步过NTP时间
     * @return
     */
    public boolean isSyncedNtpTime(){
        // 非空就是同步过了
        return ntpSyncTimeInfo != null;
    }

    public long getNtpUtcTime() {
        return getNtpUtcTime(EnmuGetTimeFallbackType.RETURN_LAST_CACHE_TIME);
    }

    /**
     * 获取服务器时间
     * @return
     */
    public long getNtpUtcTime(EnmuGetTimeFallbackType fallbackType) {
        if (isSyncedNtpTime()) {
            return this.ntpSyncTimeInfo.getServerUnixTimeStampMillis();
        }else{
            switch (fallbackType){
                case RETURN_LAST_CACHE_TIME:
                    MMKV kv = MMKV.defaultMMKV();
                    if (kv.containsKey(ZbbCacheConstant.ClockCache.KEY_NTP_TIME_INFO)) {
                        ZBBLog.w(TAG, "sync NtpTime Not yet!!! current return last sync time!!! fallbackType=" + String.valueOf(fallbackType));
                        String jsonString = kv.decodeString(ZbbCacheConstant.ClockCache.KEY_NTP_TIME_INFO);
                        SyncTimeInfo lastServerTimeInfo = new SyncTimeInfo(jsonString);
                        return lastServerTimeInfo.getServerUnixTimeStampMillis();
                    }else{
                        ZBBLog.w(TAG, "sync NtpTime Not yet!!! current return local time!!! fallbackType=" + String.valueOf(fallbackType));
                        return getLocalTime();
                    }
                case RETURN_LOCAL_TIME:
                default:
                    ZBBLog.w(TAG, "sync NtpTime Not yet!!! current return local time!!! fallbackType=" + String.valueOf(fallbackType));
                    return getLocalTime();
            }
        }
    }

    /**
     * 设置服务器时间
     * 需要外部单独调用服务器提供的接口，获取服务器的unix时间戳，结合本次调用接口的耗时，之后此方法进行自动计算
     * @param unixTimeStampSec 服务器返回的unix时间戳，单位：秒
     * @param timeZone 服务器的时区，字符串，如：UTC +8
     * @param requestConsumeTimeMillis 本次请求服务器时间，APP端计时，请求耗时，单位：毫秒
     */
    public void setServerTime(int unixTimeStampSec, String timeZone, long requestConsumeTimeMillis) {

        long curTime = System.currentTimeMillis();
        long serverUnixTimeStampMillis = unixTimeStampSec * 1000 + (requestConsumeTimeMillis / 2);
        String serverTimeZone = timeZone;
        long serverTimeDiff = SyncTimeInfo.calcTimeDiff(serverUnixTimeStampMillis ,curTime);
        // 初始化服务器时间类
        serverSyncTimeInfo = new SyncTimeInfo(serverTimeZone, serverTimeDiff, curTime);
        // 将本次同步的时间保存
        MMKV kv = MMKV.defaultMMKV();
        kv.encode(ZbbCacheConstant.ClockCache.KEY_SERVER_TIME_INFO, serverSyncTimeInfo.toJsonString());
    }

    /**
     * 枚举：当获取本次同步的服务器时间不成功时，用什么时间进行代替
     * NTP时间共用此枚举
     */
    public enum EnmuGetTimeFallbackType {
        /**
         * 返回本机时间
         */
        RETURN_LOCAL_TIME,
        /**
         * 返回  使用上一次同步过的服务器时间的时间差与当前时间进行计算 的时
         */
        RETURN_LAST_CACHE_TIME
    }

    /**
     * 是否同步过服务器时间
     * @return
     */
    public boolean isSyncedServerTime(){
        // 非空就是同步过了
        return serverSyncTimeInfo != null;
    }

    /**
     * 获取服务器时间
     * @return
     */
    public long getServerTime() {
        // 默认使用上次缓存的时间差计算出来的服务器时间，目的是当本次同步时间未成功时仍需使用服务器时间。不一定准确，但只要用户没有设置过系统时间，那么基本是准确的
        return getServerTime(EnmuGetTimeFallbackType.RETURN_LAST_CACHE_TIME);
    }

    /**
     * 获取服务器时间
     * @return
     */
    public long getServerTime(EnmuGetTimeFallbackType fallbackType) {
        if (isSyncedServerTime()) {
            return this.serverSyncTimeInfo.getServerUnixTimeStampMillis();
        }else{
            switch (fallbackType){
                case RETURN_LAST_CACHE_TIME:
                    MMKV kv = MMKV.defaultMMKV();
                    if (kv.containsKey(ZbbCacheConstant.ClockCache.KEY_SERVER_TIME_INFO)) {
                        ZBBLog.w(TAG, "sync ServerTime Not yet!!! current return last sync time!!! fallbackType=" + String.valueOf(fallbackType));
                        String jsonString = kv.decodeString(ZbbCacheConstant.ClockCache.KEY_SERVER_TIME_INFO);
                        SyncTimeInfo lastServerTimeInfo = new SyncTimeInfo(jsonString);
                        return lastServerTimeInfo.getServerUnixTimeStampMillis();
                    }else{
                        ZBBLog.w(TAG, "sync ServerTime Not yet!!! current return local time!!! fallbackType=" + String.valueOf(fallbackType));
                        return getLocalTime();
                    }
                case RETURN_LOCAL_TIME:
                default:
                    ZBBLog.w(TAG, "sync ServerTime Not yet!!! current return local time!!! fallbackType=" + String.valueOf(fallbackType));
                    return getLocalTime();
            }
        }
    }

    /**
     * 获取服务器时区
     * https://blog.csdn.net/kzcming/article/details/81868925?depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-1&utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-1
     * @return
     * @deprecated 功能暂不完善，请不要使用
     */
    public String getServerTimeZone() {
        if (isSyncedServerTime()) {
            return this.serverSyncTimeInfo.getServerTimeZone();
        }else{
            ZBBLog.w(TAG, "sync ServerTime Not yet!!! current return local time zone!!");
            TimeZone timeZone = TimeZone.getDefault();
            return timeZone.getDisplayName();
        }
    }

    /**
     * 获取本设备当前UTC时间
     * @return
     */
    public long getLocalTime() {
        return System.currentTimeMillis();
    }
}
