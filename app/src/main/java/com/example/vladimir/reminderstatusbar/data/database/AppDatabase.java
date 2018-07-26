package com.example.vladimir.reminderstatusbar.data.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.vladimir.reminderstatusbar.data.models.Reminder;

@Database(entities = {Reminder.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ReminderDao reminderDao();
}
