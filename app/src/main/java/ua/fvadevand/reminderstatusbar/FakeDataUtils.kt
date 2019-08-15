package ua.fvadevand.reminderstatusbar

import ua.fvadevand.reminderstatusbar.data.models.Reminder
import java.util.ArrayList

object FakeDataUtils {

    val reminderList: List<Reminder>
        get() = ArrayList<Reminder>().apply {
            add(Reminder(
                    "Reminder",
                    null,
                    R.drawable.ic_notif_id_card,
                    System.currentTimeMillis()))
            add(Reminder(
                    "Reminder",
                    "Subscription",
                    R.drawable.ic_notif_file,
                    System.currentTimeMillis()))
            add(Reminder(
                    "Reminder",
                    null,
                    R.drawable.ic_notif_like,
                    System.currentTimeMillis()))
        }

    val reminder: Reminder
        get() = Reminder(
                "Reminder",
                null,
                R.drawable.ic_notif_briefcase,
                System.currentTimeMillis()
        )
}
