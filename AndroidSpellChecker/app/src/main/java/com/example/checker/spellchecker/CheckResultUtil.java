package com.example.checker.spellchecker;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by song on 19/4/22.
 */

public class CheckResultUtil {

    public static void getCheckResult(String text, String url, Callback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{text:" + text + "}");
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        new OkHttpClient.Builder().build().newCall(request).enqueue(callback);
    }
}
