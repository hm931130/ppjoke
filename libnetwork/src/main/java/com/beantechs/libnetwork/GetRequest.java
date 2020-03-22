package com.beantechs.libnetwork;

public class GetRequest<T> extends Request<T, GetRequest> {

    public GetRequest(String url) {
        super(url);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {

        //把参数拼接到url后面
        String url = UrlCreator.createUrlFromParams(mUrl, params);
        okhttp3.Request request = builder.get().url(url).build();
        return request;
    }
}
