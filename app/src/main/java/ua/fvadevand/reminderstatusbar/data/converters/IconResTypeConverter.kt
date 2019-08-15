package ua.fvadevand.reminderstatusbar.data.converters

import androidx.annotation.DrawableRes
import androidx.room.TypeConverter
import ua.fvadevand.reminderstatusbar.ReminderApp

object IconResTypeConverter {

    private const val RESOURCE_FOLDER_DRAWABLE = "drawable"

    @TypeConverter
    @DrawableRes
    fun toResId(resName: String): Int {
        val context = ReminderApp.instance.applicationContext
        return context.resources.getIdentifier(resName, RESOURCE_FOLDER_DRAWABLE, context.packageName)
    }

    @TypeConverter
    fun toResName(@DrawableRes resId: Int): String =
            ReminderApp.instance.resources.getResourceEntryName(resId)
}