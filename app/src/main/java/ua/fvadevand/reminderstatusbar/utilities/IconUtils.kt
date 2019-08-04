package ua.fvadevand.reminderstatusbar.utilities

import android.content.Context
import androidx.annotation.DrawableRes
import ua.fvadevand.reminderstatusbar.R

object IconUtils {

    private const val RESOURCE_FOLDER_DRAWABLE = "drawable"

    val iconsIds: List<Int>
        get() {
            return listOf(
                    R.drawable.ic_notif_agenda,
                    R.drawable.ic_notif_attachment,
                    R.drawable.ic_notif_battery,
                    R.drawable.ic_notif_briefcase,
                    R.drawable.ic_notif_calendar,
                    R.drawable.ic_notif_database,
                    R.drawable.ic_notif_edit,
                    R.drawable.ic_notif_file,
                    R.drawable.ic_notif_folder,
                    R.drawable.ic_notif_garbage,
                    R.drawable.ic_notif_gift,
                    R.drawable.ic_notif_home,
                    R.drawable.ic_notif_id_card,
                    R.drawable.ic_notif_idea,
                    R.drawable.ic_notif_like,
                    R.drawable.ic_notif_locked,
                    R.drawable.ic_notif_mail,
                    R.drawable.ic_notif_notebook,
                    R.drawable.ic_notif_photo_camera,
                    R.drawable.ic_notif_placeholder,
                    R.drawable.ic_notif_print,
                    R.drawable.ic_notif_search,
                    R.drawable.ic_notif_settings,
                    R.drawable.ic_notif_smartphone,
                    R.drawable.ic_notif_speaker,
                    R.drawable.ic_notif_star,
                    R.drawable.ic_notif_umbrella,
                    R.drawable.ic_notif_worldwide
            )
        }

    fun getIconName(context: Context, @DrawableRes iconResId: Int): String {
        return context.resources.getResourceEntryName(iconResId)
    }

    fun getIconResId(context: Context, iconName: String): Int {
        return context.resources.getIdentifier(iconName, RESOURCE_FOLDER_DRAWABLE, context.packageName)
    }
}
