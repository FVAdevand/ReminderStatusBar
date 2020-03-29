package ua.fvadevand.reminderstatusbar.data.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.data.models.Reminder

@Database(entities = [Reminder::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        fun getDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, Const.DATABASE_NAME)
                .addMigrations(MIGRATION_1_2)
                .build()

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE reminders ADD COLUMN period_accept INTEGER NOT NULL DEFAULT 1")
            }
        }

    }
}
