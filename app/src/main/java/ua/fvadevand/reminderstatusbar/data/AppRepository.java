package ua.fvadevand.reminderstatusbar.data;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

import ua.fvadevand.reminderstatusbar.app.ReminderApp;
import ua.fvadevand.reminderstatusbar.data.database.AppDatabase;
import ua.fvadevand.reminderstatusbar.data.models.Reminder;
import ua.fvadevand.reminderstatusbar.listeners.DatabaseListener;
import ua.fvadevand.reminderstatusbar.utilities.AppExecutors;

public class AppRepository {

    private volatile static AppRepository sInstance;
    private final AppDatabase mDb;
    private final AppExecutors mExecutors;
    private final Handler mUiHandler;

    private AppRepository(AppDatabase db, AppExecutors executors) {
        mDb = db;
        mExecutors = executors;
        mUiHandler = new Handler(Looper.getMainLooper());
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

    public void insertReminder(final Reminder reminder, DatabaseListener.InsertSuccessListener listener) {
        mExecutors.diskIO().execute(() -> {
            long rowCount = mDb.reminderDao().insert(reminder);
            mUiHandler.post(() -> {
                if (listener != null) {
                    listener.insertSuccess(rowCount);
                }
            });
        });
    }

    public void updateReminder(final Reminder reminder) {
        mExecutors.diskIO().execute(() -> mDb.reminderDao().update(reminder));
    }

    public void deleteReminder(final Reminder reminder) {
        mExecutors.diskIO().execute(() -> mDb.reminderDao().delete(reminder));
    }

    public void clearDb() {
        mExecutors.diskIO().execute(mDb::clearAllTables);
    }
}
