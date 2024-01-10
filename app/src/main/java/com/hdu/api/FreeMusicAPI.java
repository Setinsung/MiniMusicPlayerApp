package com.hdu.api;

import com.google.gson.Gson;
import com.hdu.bean.FreeMusicAPIResp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FreeMusicAPI {
    private static final String BASE_URL = "https://daga.cc/yue/";

    public static void GetFreeMusics(String input,int page, ApiResponseCallback<FreeMusicAPIResp> callback) {
        OkHttpClient client = new OkHttpClient();
        UserAgentPool userAgentPool = new UserAgentPool();

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("input", input);
        builder.add("filter", "name");
        builder.add("type", "netease");
        builder.add("page", String.valueOf(page));

        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("User-Agent", userAgentPool.getRandomUserAgent())
                .addHeader("Origin", "https://daga.cc")
                .addHeader("sec-ch-ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Microsoft Edge\";v=\"120\"")
                .post(builder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    FreeMusicAPIResp freeMusicAPIResp = gson.fromJson(responseData, FreeMusicAPIResp.class);
                    callback.onSuccess(freeMusicAPIResp);
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }
        });
    }
}
