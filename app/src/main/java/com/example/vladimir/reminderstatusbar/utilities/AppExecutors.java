package com.example.vladimir.reminderstatusbar.utilities;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static AppExecutors sInstance;
    private final Executor mDiskIO;

    private AppExecutors() {
        mDiskIO = Executors.newSingleThreadExecutor();
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (AppExecutors.class) {
                if (sInstance == null) {
                    sInstance = new AppExecutors();
                }
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return mDiskIO;
    }
}
