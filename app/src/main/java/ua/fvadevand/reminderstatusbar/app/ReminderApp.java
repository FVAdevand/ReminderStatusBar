package ua.fvadevand.reminderstatusbar.app;

import android.app.Application;
import android.arch.persistence.room.Room;

import ua.fvadevand.reminderstatusbar.Const;
import ua.fvadevand.reminderstatusbar.data.database.AppDatabase;

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
