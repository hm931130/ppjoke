package com.beantechs.libnetwork;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PostRequest<T> extends Request<T, PostRequest> {


    public PostRequest(String url) {
        super(url);
    }

    /**
     * post 暂定为表单提交
     *
     * @param builder
     * @return
     */
    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.Request request = null;
        if (mPostType == POST_FORM) { //表单
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                bodyBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
            request = builder.url(mUrl).post(bodyBuilder.build()).build();
        } else if (mPostType == POST_BODY) { //json提交
            String content = JSONObject.toJSONString(params);
            RequestBody requestBody = RequestBody.create(JSON, content);
            request = builder.url(mUrl).post(requestBody).build();
        }
        return request;
    }
}
