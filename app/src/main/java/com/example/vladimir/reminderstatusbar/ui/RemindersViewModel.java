package com.example.vladimir.reminderstatusbar.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.vladimir.reminderstatusbar.data.AppRepository;
import com.example.vladimir.reminderstatusbar.data.models.Reminder;

import java.util.List;

public class RemindersViewModel extends ViewModel {

    private AppRepository mRepository;

    public RemindersViewModel() {
        mRepository = AppRepository.getInstance();
    }

    public LiveData<List<Reminder>> getReminderList() {
        return mRepository.getReminderList();
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
