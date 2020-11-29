package de.hs.lucidityLog.database;

import androidx.lifecycle.LiveData;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface EntryDao {
    @Insert
    void insertEntry(Entry entry);

    @Update
    void updateEntry(Entry entry);

    @Delete
    void deleteEntry(Entry entry);

    @Query("select * from entry " +
            "where year=:year and month=:month " +
            "order by year, month, day, id asc")
    LiveData<List<Entry>> getEntriesByMonth(int year, int month);

    @Query("select count(*) from " +
            "(select distinct day from entry " +
            "where year=:year and month=:month)")
    LiveData<Integer> getNightsWithEntriesCountByMonth(int year, int month);

    @Query("select count(*) from " +
            "(select distinct day from entry " +
            "where year=:year and month=:month and lucid)")
    LiveData<Integer> getLucidNightsCount(int year, int month);

    @Query("select count(*) from entry " +
            "where year=:year and month=:month and lucid=:lucid")
    LiveData<Integer> getLucidNumberByMonth(int year, int month, boolean lucid);

    @Query("select * from entry " +
            "order by year, month, day, id")
    List<Entry> getAllEntries();

    @Query("delete from entry")
    void nukeTable();
}