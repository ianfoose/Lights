/*
 * Copyright (c) 2017. Foose Industries
 */

package com.fooseindustries.apihelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by ianfoose on 6/4/17.
 */
public class APIHelper {
    public APIHelper() { /* Empty */ }

    public void request(boolean https, Method method, String url, Map<String,String> headers, Map<String,String> params, final APIListener listener) {
        new HttpHelper(https, method.toString(), url, headers, params, new APIListener() {
            @Override
            public void success(String obj) {
                listener.success(obj);
            }

            @Override
            public void error(Exception e) {
                listener.error(e);
            }
        });
    }

    public void jsonArray(boolean https, Method method, String url, Map<String,String> headers, Map<String,String> params, final JSONArrayListener arrayListener) {
        this.request(https, method, url, headers, params, new APIListener() {
            @Override
            public void success(String obj) {
                try {
                    JSONArray array = new JSONArray(obj);
                    arrayListener.jsonArray(array);
                } catch(JSONException e) {
                    arrayListener.error(e);
                }
            }

            @Override
            public void error(Exception e) {
                arrayListener.error(e);
            }
        });
    }

    public void jsonObject(boolean https, Method method, String url, Map<String,String> headers, Map<String,String> params, final JSONObjectListener objListener) {
        this.request(https, method, url, headers, params, new APIListener() {
            @Override
            public void success(String obj) {
                try {
                    JSONObject jo = new JSONObject(obj);
                    objListener.jsonObject(jo);
                } catch(JSONException e) {
                    objListener.error(e);
                }
            }

            @Override
            public void error(Exception e) {
               objListener.error(e);
            }
        });
    }
}