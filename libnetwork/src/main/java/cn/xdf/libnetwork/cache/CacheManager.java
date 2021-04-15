package cn.xdf.libnetwork.cache;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * author:fumm
 * Date : 2021/ 04/ 15 4:14 PM
 * Dec : TODO
 **/
public class CacheManager {

    public static <T> void save(String key, T body) {
        Cache cache = new Cache();
        cache.key = key;
        cache.data = toByteArray(body);

        CacheDatabase.get().getCache().save(cache);
    }

    public static Object getCache(String key) {
        Cache cache = CacheDatabase.get().getCache().getCache(key);
        if (cache != null && cache.data != null) {
            return toByteArray(cache.data);

        }
        return null;
    }

    /***
     * 二进制 转成 对象
     * @param bytes
     * @return
     */
    private static Object toObject(byte[] bytes) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);

            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }

        return null;
    }


    /**
     * 对象转成byte 数组
     *
     * @param body
     * @param <T>
     * @return
     */
    private static <T> byte[] toByteArray(T body) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {

            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(body);
            oos.flush();
            return bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }

                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return new byte[0];
    }
}
