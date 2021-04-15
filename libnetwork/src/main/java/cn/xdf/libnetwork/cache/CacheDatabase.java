package cn.xdf.libnetwork.cache;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cn.xdf.libcommon.AppGlobals;

/**
 * author:fumm
 * Date : 2021/ 04/ 14 3:03 PM
 * Dec : 创建缓存数据库
 **/
@Database(entities = {Cache.class}, version = 1)
public abstract class CacheDatabase extends RoomDatabase {

    private static String ROOM_DB_NAME = "mine_ppjoke_cache";

    static {
        // 创建 一个内存数据库
        // 但是这种数据库的数据只存在于内存中，也就是进程被杀之后，数据随之丢失
        // Room.inMemoryDatabaseBuilder()
        database = Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, ROOM_DB_NAME)
                // 是否允许在主线程进行查询
                .allowMainThreadQueries()
                // 添加数据库打开和创建的回调
                // .addCallback()
                // 设置查询线程池
                // .setQueryExecutor()
                // .openHelperFactory()
                // room日志模式
                // .setJournalMode()
                // 数据库升级异常之后的回滚
                // .fallbackToDestructiveMigration()
                // 数据库升级异常后根据指定版本进行回滚
                //.fallbackToDestructiveMigrationFrom()
                // 数据库升级使用
                // .addMigrations()
                .build();
    }


    private static CacheDatabase database;

     public abstract CacheDao getCache();

    public static CacheDatabase get() {
        return database;
    }
}
