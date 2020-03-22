package ua.fvadevand.reminderstatusbar.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderItem
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus

@Dao
interface ReminderDao {

    @Query("SELECT * FROM ${Reminder.TABLE_NAME}")
    fun getAllLive(): LiveData<List<ReminderItem>>

    @Query("SELECT * FROM ${Reminder.TABLE_NAME} WHERE ${Reminder.COLUMN_ID} = :id")
    fun getLiveById(id: Long): LiveData<Reminder>

    @Query("SELECT * FROM ${Reminder.TABLE_NAME} WHERE ${Reminder.COLUMN_ID} = :id")
    fun getById(id: Long): Reminder

    @Query("SELECT * FROM ${Reminder.TABLE_NAME} WHERE ${Reminder.COLUMN_STATUS} > ${ReminderStatus.DONE}")
    fun getRemindersForNotify(): List<Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder): Long

    @Update
    fun update(reminder: Reminder)

    @Query("DELETE FROM ${Reminder.TABLE_NAME} WHERE ${Reminder.COLUMN_ID} = :reminderId")
    fun deleteById(reminderId: Long)

    @Query("DELETE FROM ${Reminder.TABLE_NAME}")
    fun deleteAll()
}
