package ua.fvadevand.reminderstatusbar;

import ua.fvadevand.reminderstatusbar.data.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class FakeDataUtils {
    private FakeDataUtils() {
    }

    public static List<Reminder> getReminderList() {
        List<Reminder> reminderList = new ArrayList<>();
        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));
        reminderList.add(new Reminder(R.drawable.ic_add_24dp, "Hello", "How Are You", 54654854));

        return reminderList;
    }

    public static Reminder getReminder() {
        return new Reminder(R.drawable.ic_add_24dp, "Hi", "How Do You Do", 654547878);
    }
}
