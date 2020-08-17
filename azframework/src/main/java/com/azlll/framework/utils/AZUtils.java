package com.azlll.framework.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class AZUtils {
    public static JSONObject fromJsonString(String strJson) {
        JSONObject ret = new JSONObject();

        if (strJson == null) {
            return ret;
        }

        try {
            ret = new JSONObject(strJson);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }

    public static String getCountryCodeFormatedMobile(String mobile) {
        if (mobile == null) {
            mobile = "";
        }
        return "86 " + mobile;
    }
}
