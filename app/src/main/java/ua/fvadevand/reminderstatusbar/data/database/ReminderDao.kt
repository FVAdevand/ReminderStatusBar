package ua.fvadevand.reminderstatusbar.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus.ReminderStatuses

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders")
    fun getAllLive(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getLiveById(id: Long): LiveData<Reminder>

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getById(id: Long): Reminder

    @Query("SELECT * FROM reminders WHERE status > ${ReminderStatus.DONE}")
    fun getRemindersForNotify(): List<Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder): Long

    @Update
    fun update(reminder: Reminder)

    @Query("UPDATE reminders SET status = :status WHERE id = :reminderId ")
    fun updateStatus(reminderId: Long, @ReminderStatuses status: Int)

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    fun deleteById(reminderId: Long)

    @Query("DELETE FROM reminders")
    fun deleteAll()
}
