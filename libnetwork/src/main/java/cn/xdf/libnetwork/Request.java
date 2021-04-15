package cn.xdf.libnetwork;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.arch.core.executor.ArchTaskExecutor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import cn.xdf.libnetwork.cache.Cache;
import cn.xdf.libnetwork.cache.CacheManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * author:fumm
 * Date : 2021/ 04/ 08 10:50 AM
 * Dec : TODO
 **/
public abstract class Request<T, R extends Request> {


    protected String mUrl;
    protected HashMap<String, String> headers = new HashMap<>();
    protected HashMap<String, Object> params = new HashMap<>();

    //仅仅只访问本地缓存，即便本地缓存不存在，也不会发起网络请求
    public static final int CACHE_ONLY = 1;
    //先访问缓存，同时发起网络的请求，成功后缓存到本地
    public static final int CACHE_FIRST = 2;
    //仅仅只访问服务器，不存任何存储
    public static final int NET_ONLY = 3;
    //先访问网络，成功后缓存到本地
    public static final int NET_CACHE = 4;

    private int mCacheStrategy = NET_ONLY;
    private Type mType;
    private Class mClaz;
    private String cacheKey;

    // 使用注解 标记归纳类型
    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_CACHE, NET_ONLY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CacheStrategy {

    }


    public Request(String mUrl) {
        this.mUrl = mUrl;
    }

    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }


    public R addParam(String key, Object value) {

        if (value == null) {
            return (R) this;
        }
        //int byte char short long double float boolean 和他们的包装类型，但是除了 String.class 所以要额外判断
        try {
            Field field = value.getClass().getField("TYPE");
            Class claz = (Class) field.get(null);
            if (claz.isPrimitive()) {
                params.put(key, value);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return (R) this;
    }

    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.okHttpClient.newCall(request);
        return call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

    }

    public R cacheStrategy(@CacheStrategy int cacheStrategy) {
        mCacheStrategy = cacheStrategy;
        return (R) this;
    }

    public R responseType(Type type) {
        mType = type;
        return (R) this;
    }

    public R responseType(Class claz) {
        mClaz = claz;
        return (R) this;
    }

    public R cacheKey(String key) {
        this.cacheKey = key;
        return (R) this;
    }

    /**
     * 同步请求
     *
     * @return
     */
    public ApiResponse<T> execute() {
        if (mCacheStrategy == NET_ONLY) {
            return readCache();

        }
        ApiResponse<T> result = null;
        try {
            Response response = getCall().execute();
            result = parseResponse(response, null);

        } catch (IOException e) {
            e.printStackTrace();
            if (result == null) {
                result = new ApiResponse<>();
                result.message = e.getMessage();
            }
        }
        return result;
    }

    /**
     * 异步请求
     *
     * @param callback
     */
    @SuppressLint("RestrictedApi")
    public void execute(JsonCallback callback) {
        if (mCacheStrategy != NET_ONLY) {
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ApiResponse<T> response = readCache();
                    if (callback != null && response.body != null) {
                        callback.onSuccess(response);
                    }
                }
            });
        }

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
                if (result.success) {
                    callback.onSuccess(result);
                } else {
                    callback.onError(result);
                }
            }
        });
    }

    private ApiResponse<T> readCache() {
        ApiResponse<T> response = new ApiResponse<>();
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        Object cache = CacheManager.getCache(key);
        response.status = 304;
        response.message = "缓存获取成功";
        response.success = true;
        response.body = (T) cache;
        return response;
    }

    private ApiResponse<T> parseResponse(Response response, JsonCallback callback) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();

        Convert convert = ApiService.sConvert;
        try {
            String content = response.body().toString();
            if (success) {
                if (callback != null) {
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                } else if (mType != null) {
                    result.body = (T) convert.convert(content, mType);
                } else if (mClaz != null) {
                    result.body = (T) convert.convert(content, mClaz);
                } else {
                    Log.e("request", "parseResponse: 无法解析 ");
                }
            } else {
                message = content;
            }
        } catch (Exception e) {
            message = e.getMessage();
            success = false;
            status = 0;
        }

        result.success = success;
        result.status = status;
        result.message = message;

        if (mCacheStrategy != NET_ONLY && result.success && result.body != null
                && result.body instanceof Serializable) {
            saveCache(result.body);
        }
        return result;
    }

    private void saveCache(T body) {
        String key = TextUtils.isEmpty(cacheKey) ? generateCacheKey() : cacheKey;
        CacheManager.save(key, body);
    }

    private String generateCacheKey() {
        String cacheKey = UrlCreator.createUrlFromParams(mUrl, params);
        return cacheKey;
    }

}
