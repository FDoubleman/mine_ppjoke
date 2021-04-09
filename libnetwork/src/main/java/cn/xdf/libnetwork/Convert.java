package cn.xdf.libnetwork;

import java.lang.reflect.Type;

/**
 * author:fumm
 * Date : 2021/ 04/ 08 2:18 PM
 * Dec :
 **/
public interface Convert<T> {

    T convert(String response, Type type);

    T convert(String response, Class claz);
}
