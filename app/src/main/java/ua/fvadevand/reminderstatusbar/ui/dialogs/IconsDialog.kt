package ua.fvadevand.reminderstatusbar.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ua.fvadevand.lifecycledelegates.fragmentProperty
import ua.fvadevand.reminderstatusbar.adapters.IconAdapter
import ua.fvadevand.reminderstatusbar.databinding.DialogIconsBinding
import ua.fvadevand.reminderstatusbar.utils.iconsIds

class IconsDialog : DialogFragment() {

    private val fragmentProperty by fragmentProperty()
    private val binding by fragmentProperty.bindingByInflater(DialogIconsBinding::inflate)
    private var listener: OnIconClickListener? by fragmentProperty.delegateFragmentLifecycle()

    companion object {
        const val TAG = "IconsDialog"
        private const val SPAN_COUNT = 4

        fun newInstance() = IconsDialog()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as OnIconClickListener?
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding.iconsGrid.layoutManager = GridLayoutManager(context, SPAN_COUNT)
        val adapter = IconAdapter(iconsIds) { iconId ->
            listener?.onIconClick(iconId)
            dismiss()
        }
        binding.iconsGrid.adapter = adapter
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    interface OnIconClickListener {
        fun onIconClick(iconId: Int)
    }
}
