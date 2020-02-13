package ua.fvadevand.reminderstatusbar.data.models

import androidx.annotation.StringRes

data class SnackbarData(@StringRes val messageResId: Int, val duration: Int)