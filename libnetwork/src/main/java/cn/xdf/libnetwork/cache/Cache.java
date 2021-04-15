package cn.xdf.libnetwork.cache;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * author:fumm
 * Date : 2021/ 04/ 14 3:50 PM
 * Dec : 缓存表
 **/
@Entity(tableName = "cache")
public class Cache {

    @PrimaryKey
    @NotNull
    public String key;


    public byte[] data;
}
