package cn.xdf.mine_ppjoke.utils;

import android.app.Application;

import java.lang.reflect.InvocationTargetException;

/**
 * author:fumm
 * Date : 2021/ 03/ 19 11:23 AM
 * Dec : 获取Application 对象
 **/
public class AppGlobals {

    private static Application sApplication;

    public static Application getApplication() {
        if (sApplication != null) {
            return sApplication;
        }
        try {
            sApplication = (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return sApplication;
    }
}
