package de.hs.lucidityLog.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Entry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EntryDao entryDao();

    public static AppDatabase db;

    public static void prepareDB(Context applicationContext) {
        if(db == null) {
            synchronized (AppDatabase.class) {
                if(db == null) {
                    db = Room.databaseBuilder(applicationContext, AppDatabase.class, "AppDatabase").build();
                }
            }
        }
    }
}