package ua.fvadevand.reminderstatusbar

import ua.fvadevand.reminderstatusbar.data.models.Reminder
import java.util.ArrayList

object FakeDataUtils {

    val reminderList: List<Reminder>
        get() = ArrayList<Reminder>().apply {
            add(Reminder(
                    "Reminder",
                    null,
                    "ic_notif_mail",
                    System.currentTimeMillis()))
            add(Reminder(
                    "Reminder",
                    "Subscription",
                    "ic_notif_mail",
                    System.currentTimeMillis()))
            add(Reminder(
                    "Reminder",
                    null,
                    "ic_notif_mail",
                    System.currentTimeMillis()))
        }

    val reminder: Reminder
        get() = Reminder(
                "Reminder",
                null,
                "ic_notif_mail",
                System.currentTimeMillis()
        )
}
