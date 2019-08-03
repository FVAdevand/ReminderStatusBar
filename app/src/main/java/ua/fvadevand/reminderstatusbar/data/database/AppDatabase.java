package ua.fvadevand.reminderstatusbar.data.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import ua.fvadevand.reminderstatusbar.data.models.Reminder;

@Database(entities = {Reminder.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ReminderDao reminderDao();
}
