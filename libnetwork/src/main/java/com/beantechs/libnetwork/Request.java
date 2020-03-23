package com.beantechs.libnetwork;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.arch.core.executor.ArchTaskExecutor;


import com.beantechs.libnetwork.cache.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public abstract class Request<T, R extends Request> implements Cloneable {
    protected String mUrl;
    //添加请求头
    protected HashMap<String, String> headers = new HashMap<>();
    //添加请求参数
    protected HashMap<String, Object> params = new HashMap<>();
    //自定义拦截器
    protected List<Interceptor> interceptors = new ArrayList<>();
    //仅仅只访问本地缓存，即便本地缓存不存在，也不会发起网络请求
    public static final int CACHE_ONLY = 1;
    //先访问缓存，同时发起网络的请求，成功后缓存到本地
    public static final int CACHE_FIRST = 2;
    //仅仅只访问服务器，不存任何存储
    public static final int NET_ONLY = 3;
    //先访问网络，成功后缓存到本地
    public static final int NET_CACHE = 4;

    //POST表单提交
    public static final int POST_FORM = 1;
    //json提交
    public static final int POST_BODY = 2;
    //默认为表单
    protected int mPostType = POST_FORM;


    //缓存key
    private String cacheKey;
    //请求策略
    private int mCacheStrategy = NET_ONLY;
    //同步请求需要设置返回值类型
    private Type mType;

    @IntDef({POST_FORM, POST_BODY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PostType {
    }

    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_ONLY, NET_CACHE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CacheStrategy {
    }

    public Request(String url) {
        this.mUrl = url;
    }

    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    public R addParams(String key, Object value) {
        if (value == null) {
            return (R) this;
        } else {
            try {
                if (value.getClass() == String.class) {
                    params.put(key, value);
                } else {
                    //反射获取所属类型
                    Field field = value.getClass().getField("TYPE");
                    Class claz = (Class) field.get(null);
                    if (claz.isPrimitive()) { //如果为基本类型
                        params.put(key, value); //可以考虑转为String
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return (R) this;
    }

    public R addInterceptor(Interceptor interceptor) {
        if (!interceptors.contains(interceptor)) {
            interceptors.add(interceptor);
        }
        return (R) this;
    }

    public R cacheStrategy(@CacheStrategy int cacheStrategy) {
        mCacheStrategy = cacheStrategy;
        return (R) this;
    }

    public R postType(@PostType int postType) {
        mPostType = postType;
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type argument = parameterizedType.getActualTypeArguments()[1];
        if (argument == GetRequest.class) {
            throw new RuntimeException("GET方法无需设置POST上传类型");
        }
        return (R) this;
    }

    public R cacheKey(String key) {
        cacheKey = key;
        return (R) this;
    }

    public R responseType(Type type) {
        mType = type;
        return (R) this;
    }


    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        OkHttpClient okHttpClient = ApiService.okHttpClient;
        if (interceptors.size() > 0) {
            for (Interceptor interceptor : interceptors) {
                okHttpClient = okHttpClient.newBuilder().addInterceptor(interceptor).build();
            }
        }
        Call call = okHttpClient.newCall(request);
        return call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            builder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }
    }


    /**
     * 同步方法
     */
    public ApiResponse<T> execute() {

        if (mType == null) {
            throw new RuntimeException("同步方法，response返回值类型必须设置");
        }
        if (mCacheStrategy == CACHE_ONLY) {
            return readCache();
        }

        ApiResponse<T> result = null;
        try {
            Response response = getCall().execute();
            result = parseResponse(response, null);
        } catch (Exception e) {
            e.printStackTrace();
            if (result == null) {
                result = new ApiResponse<>();
                result.message = e.getMessage();
            }

        }
        return result;

    }

    /**
     * 异步方法
     *
     * @param callback
     */
    @SuppressLint("RestrictedApi")
    public void execute(final JsonCallback callback) {
        if (mCacheStrategy != NET_ONLY) {
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ApiResponse<T> response = readCache();
                    if (callback != null) {
                        callback.onCacheSuccess(response);
                    }
                }
            });
        }

        if (mCacheStrategy != CACHE_ONLY) {
            getCall().enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ApiResponse<T> result = new ApiResponse<>();
                    result.message = e.getMessage();
                    callback.onError(result);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ApiResponse<T> result = parseResponse(response, callback);
                    if (!result.success) {
                        callback.onError(result);
                    } else {
                        callback.onSuccess(result);
                    }

                }
            });
        }
    }

    private ApiResponse<T> readCache() {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> result = new ApiResponse<>();
        result.status = 304;
        result.message = "缓存获取成功";
        result.body = (T) cache;
        result.success = true;
        return result;
    }

    private ApiResponse<T> parseResponse(Response response, JsonCallback callback) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();
        Convert convert = ApiService.sConvert;
        try {
            String content = response.body().string();
            if (success) {
                if (callback != null) {
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                } else if (mType != null) {
                    result.body = (T) convert.convert(content, mType);
                } else {
                    Log.e("request", "parseResponse:无法解析");
                }
            } else {
                message = content;
            }

        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
            success = false;
            status = 0;
        }
        result.success = success;
        result.status = status;
        result.message = message;

        if (mCacheStrategy != NET_ONLY && result.success && result.body != null && result.body instanceof Serializable) {
            saveCache(result.body);
        }
        return result;
    }

    private void saveCache(T body) {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        CacheManager.save(key, body);
    }

    private String generateCacheKey() {
        cacheKey = UrlCreator.createUrlFromParams(mUrl, params);
        return cacheKey;
    }

}
