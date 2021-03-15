package com.vaaq.fixmyphone.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkHelper {

    public static final String TAG = "Error network";

    OnResponse onResponse;

    Context context;

    public NetworkHelper(Context context) {
        this.context = context;
        this.onResponse = onResponse;
    }

    public boolean isConnected() {
        String command = "ping -c 1 google.com";
        try {
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface OnResponse {
        void response(String result);
    }



}
