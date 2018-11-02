package ua.fvadevand.reminderstatusbar;

import java.util.ArrayList;
import java.util.List;

import ua.fvadevand.reminderstatusbar.data.models.Reminder;

public class FakeDataUtils {
    private FakeDataUtils() {
    }

    public static List<Reminder> getReminderList() {
        List<Reminder> reminderList = new ArrayList<>();
//        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
//        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
//        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
//        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
//        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
//        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
//        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
//        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));

        return reminderList;
    }

    public static Reminder getReminder() {
        return new Reminder();
    }
}
