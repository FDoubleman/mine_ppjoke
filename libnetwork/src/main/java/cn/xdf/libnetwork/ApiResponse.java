package cn.xdf.libnetwork;

/**
 * author:fumm
 * Date : 2021/ 04/ 08 11:04 AM
 * Dec : 接口返回结果包装类型
 **/
public class ApiResponse<T> {
    public boolean success;
    public int status;
    public String message;
    public T body;

}
