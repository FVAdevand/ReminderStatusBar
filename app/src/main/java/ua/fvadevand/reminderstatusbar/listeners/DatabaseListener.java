package ua.fvadevand.reminderstatusbar.listeners;

public class DatabaseListener {
    private DatabaseListener() {
        //no instance
    }

    public interface InsertSuccessListener {
        void insertSuccess(long reminderId);
    }
}
