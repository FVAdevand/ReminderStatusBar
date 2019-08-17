package ua.fvadevand.reminderstatusbar.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.listeners.OnReminderClickListener

class ReminderMenuFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: RemindersViewModel
    private var listener: OnReminderClickListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnReminderClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(RemindersViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminder_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<NavigationView>(R.id.reminder_menu).setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_reminder_edit -> listener?.onClickReminderEdit()
                R.id.menu_reminder_notify -> viewModel.notifyCurrentReminder()
                R.id.menu_reminder_delete -> viewModel.removeCurrentReminder()
            }
            dismiss()
            true
        }

    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    companion object {
        const val TAG = "ReminderMenuFragment"
    }
}