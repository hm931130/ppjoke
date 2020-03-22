package com.beantechs.libnetwork.cache;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.beantechs.libcommon.AppGlobals;

@Database(entities = {Cache.class},version = 1,exportSchema = true)
public abstract class CacheDatabase extends RoomDatabase {
    private static final CacheDatabase database;

    static {
        database = Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, "ppjoke_cache")
                .allowMainThreadQueries()
                .build();
    }

    public static CacheDatabase get() {
        return database;
    }

    public abstract CacheDao getCache();
}
