package ua.fvadevand.reminderstatusbar.ui.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.adapters.IconAdapter
import ua.fvadevand.reminderstatusbar.utils.IconUtils

class IconsDialog : DialogFragment() {
    private var listener: OnIconClickListener? = null

    companion object {
        const val TAG = "IconsDialog"
        private const val SPAN_COUNT = 4
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as OnIconClickListener?
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_icons, null)
        val iconsView = rootView.findViewById<RecyclerView>(R.id.icons_grid)
        iconsView.layoutManager = GridLayoutManager(context, SPAN_COUNT)
        val adapter = IconAdapter(IconUtils.iconsIds) { iconId ->
            listener?.onIconClick(iconId)
            dismiss()
        }
        iconsView.adapter = adapter
        return AlertDialog.Builder(context)
            .setView(rootView)
            .create()
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    interface OnIconClickListener {
        fun onIconClick(iconId: Int)
    }
}
