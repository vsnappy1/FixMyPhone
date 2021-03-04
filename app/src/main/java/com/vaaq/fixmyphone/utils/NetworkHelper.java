package com.vaaq.fixmyphone.utils;

import android.content.Context;

import java.io.IOException;

public class NetworkHelper {

    public static final String TAG = "Error network";

    OnResponse onResponse;

    Context context;

    public NetworkHelper(Context context, OnResponse onResponse) {
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
