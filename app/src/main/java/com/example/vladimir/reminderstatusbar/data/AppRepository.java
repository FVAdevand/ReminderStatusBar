package com.example.vladimir.reminderstatusbar.data;

import android.arch.lifecycle.LiveData;

import com.example.vladimir.reminderstatusbar.app.ReminderApp;
import com.example.vladimir.reminderstatusbar.data.database.AppDatabase;
import com.example.vladimir.reminderstatusbar.data.models.Reminder;
import com.example.vladimir.reminderstatusbar.utilities.AppExecutors;

import java.util.List;

public class AppRepository {

    private volatile static AppRepository sInstance;
    private final AppDatabase mDb;
    private final AppExecutors mExecutors;

    private AppRepository(AppDatabase db, AppExecutors executors) {
        mDb = db;
        mExecutors = executors;
    }

    public static AppRepository getInstance() {
        if (sInstance == null) {
            synchronized (AppRepository.class) {
                if (sInstance == null) {
                    sInstance = new AppRepository(ReminderApp.getInstance().getDb(),
                            AppExecutors.getInstance());
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<Reminder>> getReminderList() {
        return mDb.reminderDao().getAll();
    }

    public LiveData<Reminder> getReminderById(long id) {
        return mDb.reminderDao().getById(id);
    }

    public void insertReminder(final Reminder reminder) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.reminderDao().insert(reminder);
            }
        });
    }

    public void updateReminder(final Reminder reminder) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.reminderDao().update(reminder);
            }
        });
    }

    public void deleteReminder(final Reminder reminder) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.reminderDao().delete(reminder);
            }
        });
    }

    public void clearDb() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.clearAllTables();
            }
        });
    }
}
