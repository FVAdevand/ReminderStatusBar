package ua.fvadevand.reminderstatusbar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.utils.fragmentProperty
import ua.fvadevand.reminderstatusbar.utils.isAndroidO
import ua.fvadevand.reminderstatusbar.utils.isNightMode

abstract class BaseBottomSheetDialogFragment(
    private val layoutResId: Int
) : BottomSheetDialogFragment() {

    val fragmentProperty by fragmentProperty()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAndroidO()) {
            dialog?.window?.apply {
                navigationBarColor = view.context.getColor(R.color.colorSurface)
                if (!view.context.isNightMode()) {
                    decorView.systemUiVisibility =
                        decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }
        }
    }
}