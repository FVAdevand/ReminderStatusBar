package ua.fvadevand.reminderstatusbar.utils

import ua.fvadevand.reminderstatusbar.data.models.ReminderItem

class SortUtils {

    abstract class BaseReminderComparator : Comparator<ReminderItem> {
        override fun compare(o1: ReminderItem, o2: ReminderItem): Int {
            if (o1.type == ReminderItem.TYPE_HEADER) return 1
            if (o2.type == ReminderItem.TYPE_HEADER) return -1
            return compareDetail(o1, o2)
        }

        abstract fun compareDetail(o1: ReminderItem, o2: ReminderItem): Int
    }

    class ReminderTitleComparator(
        private val sortOrderAsc: Boolean
    ) : BaseReminderComparator() {
        override fun compareDetail(o1: ReminderItem, o2: ReminderItem): Int {
            return if (sortOrderAsc) {
                o1.reminder.title.toLowerCase().compareTo(o2.reminder.title.toLowerCase())
            } else {
                o2.reminder.title.toLowerCase().compareTo(o1.reminder.title.toLowerCase())
            }
        }
    }

    class ReminderStatusComparator(
        private val sortOrderAsc: Boolean
    ) : BaseReminderComparator() {
        override fun compareDetail(o1: ReminderItem, o2: ReminderItem): Int {
            return if (sortOrderAsc) {
                o1.reminder.status.compareTo(o2.reminder.status)
            } else {
                o2.reminder.status.compareTo(o1.reminder.status)
            }
        }
    }

    class ReminderTimeComparator(
        private val sortOrderAsc: Boolean
    ) : BaseReminderComparator() {
        override fun compareDetail(o1: ReminderItem, o2: ReminderItem): Int {
            return if (sortOrderAsc) {
                o1.reminder.timestamp.compareTo(o2.reminder.timestamp)
            } else {
                o2.reminder.timestamp.compareTo(o1.reminder.timestamp)
            }
        }
    }
}