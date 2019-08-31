package ua.fvadevand.reminderstatusbar.data.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.data.models.Reminder

@Database(entities = [Reminder::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        fun getDatabase(context: Context) =
                Room.databaseBuilder(context, AppDatabase::class.java, Const.DATABASE_NAME)
                        .build()
    }
}
