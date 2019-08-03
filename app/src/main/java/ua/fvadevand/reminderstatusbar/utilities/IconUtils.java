package ua.fvadevand.reminderstatusbar.utilities;

import android.content.Context;

import androidx.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;

import ua.fvadevand.reminderstatusbar.R;

public class IconUtils {

    private static final String RESOURCE_FOLDER_DRAWABLE = "drawable";

    private IconUtils() {
        //no instance
    }

    public static List<Integer> getIconsIds() {
        List<Integer> iconIds = new ArrayList<>();
        iconIds.add(R.drawable.ic_notif_agenda);
        iconIds.add(R.drawable.ic_notif_attachment);
        iconIds.add(R.drawable.ic_notif_battery);
        iconIds.add(R.drawable.ic_notif_briefcase);
        iconIds.add(R.drawable.ic_notif_calendar);
        iconIds.add(R.drawable.ic_notif_database);
        iconIds.add(R.drawable.ic_notif_edit);
        iconIds.add(R.drawable.ic_notif_file);
        iconIds.add(R.drawable.ic_notif_folder);
        iconIds.add(R.drawable.ic_notif_garbage);
        iconIds.add(R.drawable.ic_notif_gift);
        iconIds.add(R.drawable.ic_notif_home);
        iconIds.add(R.drawable.ic_notif_id_card);
        iconIds.add(R.drawable.ic_notif_idea);
        iconIds.add(R.drawable.ic_notif_like);
        iconIds.add(R.drawable.ic_notif_locked);
        iconIds.add(R.drawable.ic_notif_mail);
        iconIds.add(R.drawable.ic_notif_notebook);
        iconIds.add(R.drawable.ic_notif_photo_camera);
        iconIds.add(R.drawable.ic_notif_placeholder);
        iconIds.add(R.drawable.ic_notif_print);
        iconIds.add(R.drawable.ic_notif_search);
        iconIds.add(R.drawable.ic_notif_settings);
        iconIds.add(R.drawable.ic_notif_smartphone);
        iconIds.add(R.drawable.ic_notif_speaker);
        iconIds.add(R.drawable.ic_notif_star);
        iconIds.add(R.drawable.ic_notif_umbrella);
        iconIds.add(R.drawable.ic_notif_worldwide);
        return iconIds;
    }

    public static String getIconName(Context context, @DrawableRes int iconResId) {
        return context.getResources().getResourceEntryName(iconResId);
    }

    public static int getIconResId(Context context, String iconName) {
        return context.getResources().getIdentifier(iconName, RESOURCE_FOLDER_DRAWABLE, context.getPackageName());
    }
}
