package cn.xdf.libnetwork.cache;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * author:fumm
 * Date : 2021/ 04/ 14 3:58 PM
 * Dec : TODO
 **/
@Dao
public interface CacheDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long save(Cache cache);

    @Query("select * from cache where 'key'=:key")
    Cache getCache(String key);

    @Delete
    int delete(Cache cache);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Cache cache);
}
