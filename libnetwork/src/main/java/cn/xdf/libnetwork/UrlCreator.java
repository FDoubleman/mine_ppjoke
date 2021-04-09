package cn.xdf.libnetwork;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * author:fumm
 * Date : 2021/ 04/ 08 11:24 AM
 * Dec : Url解析类
 **/
public class UrlCreator {
    public static String createUrlFromParams(String url, HashMap<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        builder.append(url);

        if (url.indexOf("?") > 0 || url.indexOf("&") > 0) {
            builder.append("&");
        } else {
            builder.append("?");
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                String value = URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8");
                builder.append(entry.getKey()).append("=").append(value).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
