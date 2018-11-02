package ua.fvadevand.reminderstatusbar.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ua.fvadevand.reminderstatusbar.data.models.Reminder;

@Dao
public interface ReminderDao {

    @Query("SELECT * FROM reminders")
    LiveData<List<Reminder>> getAll();

    @Query("SELECT * FROM reminders WHERE id = :id")
    LiveData<Reminder> getById(long id);

    @Insert
    long insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);
}
