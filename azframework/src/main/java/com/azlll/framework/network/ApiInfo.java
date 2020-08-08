package com.azlll.framework.network;

import android.support.annotation.NonNull;

public class ApiInfo implements Comparable<ApiInfo> {

    private NetworkManager.EnumMethod method;
    private String baseUrl;
    private String path;

    public ApiInfo(NetworkManager.EnumMethod method, String baseUrl, String path) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.path = path;
    }

    public NetworkManager.EnumMethod getMethod(){
        return this.method;
    }
    public String getBaseUrl(){
        return this.baseUrl;
    }
    public String getPath(){
        return this.path;
    }
    public String getUrl(){
        return this.baseUrl + this.path;
    }

    @Override
    public int compareTo(@NonNull ApiInfo o) {
        if (this.method == o.getMethod()
            && this.path.equals(o.getPath())) {
            // 相等
            return 0;
        }else{
            // 不等
            return -1;
        }
    }
}
