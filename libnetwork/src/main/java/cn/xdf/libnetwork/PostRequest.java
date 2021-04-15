package cn.xdf.libnetwork;

import java.util.Map;

import okhttp3.FormBody;

/**
 * author:fumm
 * Date : 2021/ 04/ 08 2:02 PM
 * Dec : post请求
 **/
public class PostRequest<T> extends Request<T, PostRequest> {
    public PostRequest(String mUrl) {
        super(mUrl);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            bodyBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
        okhttp3.Request request = builder.url(mUrl).post(bodyBuilder.build()).build();

        return request;
    }
}
