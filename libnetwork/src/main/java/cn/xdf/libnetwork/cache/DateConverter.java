package cn.xdf.libnetwork.cache;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * author:fumm
 * Date : 2021/ 04/ 15 4:03 PM
 * Dec : 存储数据 数据格式转换
 **/
public class DateConverter {

    @TypeConverter
    public static Long date2Long(Date date) {
        return date.getTime();
    }


    public static Date long2Date(Long date) {
        return new Date(date);
    }
}
