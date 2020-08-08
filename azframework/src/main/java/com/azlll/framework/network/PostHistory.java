package com.azlll.framework.network;

import java.io.IOException;

public class PostHistory {

    /**
     * 在内存中缓存最新5条记录
     */
    public static final int MAX_HISTORY_CACHE_COUNT_PER_LOOP = 5;
    private String responseJson;
    private long consumeTime;
    private long postFinishTime;
    private IOException exception;

    public PostHistory(String responseJson, long consumeTime) {
        this.responseJson = responseJson;
        this.consumeTime = consumeTime;
        this.postFinishTime = System.currentTimeMillis();

        this.exception = null;
    }

    public PostHistory(IOException e, long consumeTime) {
        this.responseJson = null;
        this.consumeTime = consumeTime;
        this.postFinishTime = System.currentTimeMillis();

        this.exception = e;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public long getConsumeTime() {
        return consumeTime;
    }

    public long getPostFinishTime() {
        return postFinishTime;
    }

    public IOException getException() {
        return exception;
    }
}
