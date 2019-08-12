package ua.fvadevand.reminderstatusbar.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import ua.fvadevand.reminderstatusbar.data.models.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders")
    fun getAll(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getById(id: Long): LiveData<Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder)

    @Update
    fun update(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)
}
