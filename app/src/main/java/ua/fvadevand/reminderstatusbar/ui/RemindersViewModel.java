package ua.fvadevand.reminderstatusbar.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ua.fvadevand.reminderstatusbar.data.AppRepository;
import ua.fvadevand.reminderstatusbar.data.models.Reminder;

public class RemindersViewModel extends ViewModel {

    private static final String TAG = "RemindersViewModel";

    private AppRepository mRepository;
    private LiveData<List<Reminder>> mReminderList;

    public RemindersViewModel() {
        mRepository = AppRepository.getInstance();
        mReminderList = mRepository.getReminderList();
    }

    LiveData<List<Reminder>> getReminderList() {
        return mReminderList;
    }

    LiveData<Reminder> getReminderById(long id) {
        return mRepository.getReminderById(id);
    }

    //    for test
    void insertReminder(final Reminder reminder) {
        mRepository.insertReminder(reminder, reminderId ->
                Log.i(TAG, "insertSuccess: count=" + reminderId + " : " + Thread.currentThread().getName()));
    }

    void deleteReminder(final Reminder reminder) {
        mRepository.deleteReminder(reminder);
    }

    void clearDb() {
        mRepository.clearDb();
    }
}
