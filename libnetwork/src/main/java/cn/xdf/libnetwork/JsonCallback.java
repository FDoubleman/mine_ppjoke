package cn.xdf.libnetwork;

/**
 * author:fumm
 * Date : 2021/ 04/ 08 11:05 AM
 * Dec : 不选择使用接口，而使用抽象类 可以选择性复写方法
 **/
public abstract class JsonCallback<T> {

    public void onSuccess(ApiResponse<T> response) {

    }

    public void onError(ApiResponse<T> response) {

    }

    // 拥有缓存的能力
    public void onCacheSuccess(ApiResponse<T> response) {

    }

}
