package cn.xdf.libnetwork;

/**
 * author:fumm
 * Date : 2021/ 04/ 08 11:21 AM
 * Dec : get 请求
 **/
class GetRequest<T> extends Request<T,GetRequest>{
    public GetRequest(String mUrl) {
        super(mUrl);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        String url = UrlCreator.createUrlFromParams(mUrl,params);
        okhttp3.Request request = builder.get().url(url).build();

        return request;
    }
}
