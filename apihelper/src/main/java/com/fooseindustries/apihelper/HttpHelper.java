/*
 * Copyright (c) 2017. Foose Industries
 */

package com.fooseindustries.apihelper;

/**
 * Created by ianfoose on 6/4/17.
 */

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HttpHelper extends AsyncTask<Void, Void, Void> {
    private String method = "GET";
    private String stringURL;
    boolean https = false;
    private Map<String,String> headers;
    private Map<String,String> paramData;
    private APIListener listener;

    private String response;

    public HttpHelper(boolean https, String method, String url, Map<String,String> headers, Map<String,String> paramData, APIListener apiListener) {
        this.listener = apiListener;

        if(method != null) {
            this.method = method;
        }

        if(url == null) {
            listener.error(new Exception("Null URL"));
        } else {
            this.stringURL = url;
        }

        this.https = https;
        this.headers = headers;
        this.paramData = paramData;

        this.execute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection con;

        if(Looper.myLooper() == null) {
            Looper.prepare();
        }

        try {
            URL url = new URL(stringURL);

            if(this.https) {
                con = (HttpsURLConnection) url.openConnection();
            } else {
                con = (HttpURLConnection) url.openConnection();
            }

            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod(method);

            if(headers != null) {
                for(Map.Entry<String,String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    con.setRequestProperty(key,value);
                }
            }

            if(paramData != null) {
                con.setDoOutput(true);
                con.setDoInput(true);

                Uri.Builder builder = new Uri.Builder();

                for(Map.Entry<String,String> entry: paramData.entrySet()) {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue());
                }

                String query = builder.build().getEncodedQuery();

                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
            }

            con.connect();

            if(con.getResponseCode() == 200) {
                String result = "";

                InputStream in = new BufferedInputStream(con.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                in.close();

                Log.d("HTTP BODY:",result);

                response = result;

                listener.success(result);
            } else { // error
                listener.error(new Exception("Connection Error"));
            }

            con.disconnect();
        } catch(MalformedURLException e) {
            e.printStackTrace();
            listener.error(new Exception("Bad URL"));
        } catch(IOException e) {
            e.printStackTrace();
            listener.error(new Exception("IO Error"));
        }

        return null;
    }
}