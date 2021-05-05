package ua.fvadevand.reminderstatusbar.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ua.fvadevand.lifecycledelegates.fragmentProperty
import ua.fvadevand.reminderstatusbar.R

class NightModeDialog : DialogFragment() {

    private val fragmentProperty by fragmentProperty()
    private var listener: OnNightModeSetListener? by fragmentProperty.delegateFragmentLifecycle()
    private val nightMode by lazy {
        arguments?.getInt(ARG_NIGHT_MODE) ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    companion object {
        const val TAG = "NightModeDialog"
        private const val ARG_NIGHT_MODE = "night_mode"

        fun newInstance(nightMode: Int) = NightModeDialog().apply {
            arguments = Bundle().apply { putInt(ARG_NIGHT_MODE, nightMode) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnNightModeSetListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.night_mode_dialog_title)
            .setSingleChoiceItems(
                resources.getStringArray(R.array.night_mode_settings),
                getNightModePosition()
            ) { _, which ->
                val selectedNightMode = when (which) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                listener?.onNightModeSet(selectedNightMode)
                dismiss()
            }
            .create()
    }

    private fun getNightModePosition() = when (nightMode) {
        AppCompatDelegate.MODE_NIGHT_NO -> 0
        AppCompatDelegate.MODE_NIGHT_YES -> 1
        else -> 2
    }

    interface OnNightModeSetListener {
        fun onNightModeSet(nightMode: Int)
    }
}