package ua.fvadevand.reminderstatusbar.ui

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.utils.isAndroidO
import ua.fvadevand.reminderstatusbar.utils.isNightMode

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAndroidO()) {
            dialog?.window?.apply {
                navigationBarColor = view.context.getColor(R.color.colorBottomSheetBackground)
                if (!view.context.isNightMode()) {
                    decorView.systemUiVisibility =
                        decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }
        }
    }
}