package ua.fvadevand.reminderstatusbar.utils

import ua.fvadevand.reminderstatusbar.data.models.Reminder

class SortUtils {

    class ReminderTitleComparator(private val sortOrderAsc: Boolean) : Comparator<Reminder> {
        override fun compare(o1: Reminder, o2: Reminder): Int {
            return if (sortOrderAsc) {
                o1.title.compareTo(o2.title, true)
            } else {
                o2.title.compareTo(o1.title, true)
            }
        }
    }

    class ReminderStatusComparator(private val sortOrderAsc: Boolean) : Comparator<Reminder> {
        override fun compare(o1: Reminder, o2: Reminder): Int {
            return if (sortOrderAsc) {
                o1.status.compareTo(o2.status)
            } else {
                o2.status.compareTo(o1.status)
            }
        }
    }

    class ReminderTimeComparator(private val sortOrderAsc: Boolean) : Comparator<Reminder> {
        override fun compare(o1: Reminder, o2: Reminder): Int {
            return if (sortOrderAsc) {
                o1.timestamp.compareTo(o2.timestamp)
            } else {
                o2.timestamp.compareTo(o1.timestamp)
            }
        }
    }

}