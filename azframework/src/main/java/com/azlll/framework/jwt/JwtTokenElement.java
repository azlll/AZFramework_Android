package com.azlll.framework.jwt;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JwtTokenElement {

    private String rawString;// 原始字符串，base64格式
    private String lightString;// 明文字符串，解码base64后获得，json格式

    public JwtTokenElement(String rawString){
        if (rawString == null) {
            this.rawString = "";
        }
        this.rawString = rawString;
        try {
            //DEFAULT\
            byte[] contentByte = Base64.decode(rawString, Base64.URL_SAFE);
            this.lightString = new String(contentByte, "UTF-8");
        } catch (Exception e) {
            this.lightString = "";
        }
    }

    public String getRawString() {
        return this.rawString;
    }

    public String getLightString() {
        return this.lightString;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(this.lightString);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = new JSONObject();
        } finally {
            return jsonObject;
        }
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(this.lightString);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonArray = new JSONArray();
        } finally {
            return jsonArray;
        }
    }


}
