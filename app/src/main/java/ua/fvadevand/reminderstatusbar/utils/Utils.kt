package ua.fvadevand.reminderstatusbar.utils

import android.os.Build

object Utils {

    fun isAndroidO(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }
}