package com.azlll.framework.network;

import java.io.IOException;

import okhttp3.Response;

public class ServerErrorException extends IOException {

    public static final int HTTP_STATUS_CODE_UNAUTHORIZED = 401;
    public static final int HTTP_STATUS_CODE_FORBIDDEN    = 403;
    public static final int HTTP_STATUS_CODE_NOT_FOUND    = 404;
    public static final int HTTP_STATUS_CODE_SERVER_ERROR = 500;

    private int httpStatusCode;
    private String bodyString;

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getBodyString() {
        return bodyString;
    }

    public ServerErrorException(Response response) {
        super("Unexpected code " + response);

        httpStatusCode = response.code();
        try {
            bodyString = response.body().string();
        }catch (Exception ex) {
            bodyString = "";
        }
    }

}
