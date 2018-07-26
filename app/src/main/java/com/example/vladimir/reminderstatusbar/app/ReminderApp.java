package com.example.vladimir.reminderstatusbar.app;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.vladimir.reminderstatusbar.Const;
import com.example.vladimir.reminderstatusbar.data.database.AppDatabase;

public class ReminderApp extends Application {

    private static ReminderApp sInstance;
    private AppDatabase mDb;

    public static ReminderApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public AppDatabase getDb() {
        if (mDb == null) {
            mDb = Room.databaseBuilder(this, AppDatabase.class, Const.DATABASE_NAME).build();
        }
        return mDb;
    }
}
