package ua.fvadevand.reminderstatusbar.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import ua.fvadevand.reminderstatusbar.data.AppRepository;
import ua.fvadevand.reminderstatusbar.data.models.Reminder;

public class RemindersViewModel extends ViewModel {

    private AppRepository mRepository;
    private LiveData<List<Reminder>> mReminderList;

    public RemindersViewModel() {
        mRepository = AppRepository.getInstance();
        mReminderList = mRepository.getReminderList();
    }

    public LiveData<List<Reminder>> getReminderList() {
        return mReminderList;
    }

    //    for test
    public void insertReminder(final Reminder reminder) {
        mRepository.insertReminder(reminder);
    }

    public void deleteReminder(final Reminder reminder) {
        mRepository.deleteReminder(reminder);
    }

    public void clearDb() {
        mRepository.clearDb();
    }
}
