package ua.fvadevand.reminderstatusbar.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
