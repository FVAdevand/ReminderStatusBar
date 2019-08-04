package ua.fvadevand.reminderstatusbar.data.database


import androidx.room.Database
import androidx.room.RoomDatabase

import ua.fvadevand.reminderstatusbar.data.models.Reminder

@Database(entities = [Reminder::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao
}
