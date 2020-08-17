package com.azlll.framework.network;

import android.os.Handler;

import com.azlll.framework.log.AZLog;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostLooper {

    private static final String TAG = PostLooper.class.getSimpleName();

    private UUID uuid;
    private RequestEntity requestEntity;
    private LoopSettings loopSettings;
    private Handler mainHandler;
    private onLoopListener onLoopListener;
    private int currentLoopCount;
    private int maxLoopCount;
    private boolean isCancel = false;
    private Map<Integer, PostHistory> mapPostHistory;

    public PostLooper(UUID uuid, RequestEntity requestEntity, LoopSettings loopSettings, final Handler mainHandler, onLoopListener onLoopListener) {
        this.uuid = uuid;
        this.requestEntity = requestEntity;
        this.loopSettings = loopSettings;
        this.mainHandler = mainHandler;
        this.onLoopListener = onLoopListener;
        this.mapPostHistory = new HashMap<>();

        this.currentLoopCount = 0;

        switch (loopSettings.getLoopMode()){
            case ONCE:
                this.maxLoopCount = 1;
                break;
            case SPECIFIED:
                this.maxLoopCount = loopSettings.getLoopCount();
                break;
            case INFINITY:
            default:
                this.maxLoopCount = Integer.MAX_VALUE;


                break;
        }

        startLoop();
    }

    /**
     * 开始循环
     */
    private void startLoop() {
        isCancel = false;

        if (loopSettings.getLoopMode() == LoopSettings.EnumLoopMode.ONCE) {
            this.currentLoopCount = 1;
            onLoopListener.onLoop(this, getRequestEntity());
        }else{
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (isCancel) {
                        // 外部取消了，则本次不执行
                        return;
                    }
                    // 当前循环次数+1
                    currentLoopCount++;
                    // 触发onLoop回调，在外部执行真正每次Loop的处理
                    onLoopListener.onLoop(PostLooper.this, getRequestEntity());
                    // 控制Loop的停止/继续
                    if (currentLoopCount >= maxLoopCount){
                        // 循环次数达到上限，停止
                        cancel();
                        return;
                    }else {
                        // 指定时间后在此再次执行
                        mainHandler.postDelayed(this, loopSettings.getLoopInterval());
                    }
                }
            };
            // 在主线程执行
            mainHandler.post(runnable);
        }
    }

    /**
     * 取消循环
     */
    public void cancel(){
        this.isCancel = true;
        AZLog.d(TAG, "PostLooper is cancel...");
    }

    public void addHistory(PostHistory history) {
        if (history == null){
            return;
        }
        mapPostHistory.put(this.currentLoopCount, history);
    }

    public UUID getUuid() {
        return uuid;
    }

    public RequestEntity getRequestEntity() {
        return requestEntity;
    }

    public LoopSettings getLoopSettings() {
        return loopSettings;
    }

    public int getCurrentLoopCount() {
        return currentLoopCount;
    }

    public int getMaxLoopCount() {
        return maxLoopCount;
    }

    public Map<Integer, PostHistory> getPostHistoryMap() {
        return mapPostHistory;
    }

    public boolean isLastLoop() {
        return currentLoopCount == maxLoopCount;
    }

    public interface onLoopListener {
        void onLoop(final PostLooper postLooper, RequestEntity requestEntity);
    }
}
