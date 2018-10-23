package ua.fvadevand.reminderstatusbar.data.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ua.fvadevand.reminderstatusbar.data.models.Reminder;

@Database(entities = {Reminder.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ReminderDao reminderDao();
}
