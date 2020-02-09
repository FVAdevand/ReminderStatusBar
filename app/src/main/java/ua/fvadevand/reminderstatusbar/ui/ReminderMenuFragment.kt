package ua.fvadevand.reminderstatusbar.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.listeners.OnReminderClickListener
import ua.fvadevand.reminderstatusbar.utils.IconUtils

class ReminderMenuFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: RemindersViewModel
    private lateinit var reminderTitleView: TextView
    private lateinit var reminderIconView: ImageView
    private var listener: OnReminderClickListener? = null

    companion object {
        const val TAG = "ReminderMenuFragment"

        fun newInstance() = ReminderMenuFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnReminderClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(activity!!).get(RemindersViewModel::class.java)
        reminderTitleView = view.findViewById(R.id.tv_menu_reminder_title)
        reminderIconView = view.findViewById(R.id.iv_menu_reminder_icon)
        val navigationView: NavigationView = view.findViewById(R.id.reminder_menu)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_reminder_edit -> listener?.onClickReminderEdit()
                R.id.menu_reminder_notify -> viewModel.notifyCurrentReminder()
                R.id.menu_reminder_done -> viewModel.setCurrentReminderStatusDone()
                R.id.menu_reminder_delete -> viewModel.removeCurrentReminder()
            }
            dismiss()
            true
        }
        viewModel.getCurrentReminder {
            if (it == null) return@getCurrentReminder
            reminderIconView.setImageResource(
                IconUtils.toResId(
                    reminderIconView.context,
                    it.iconName
                )
            )
            reminderTitleView.text = it.title
            val menu = navigationView.menu
            when (it.status) {
                ReminderStatus.DONE -> {
                    menu.findItem(R.id.menu_reminder_notify).isVisible = true
                }
                ReminderStatus.NOTIFYING -> {
                    menu.findItem(R.id.menu_reminder_done).isVisible = true
                }
                ReminderStatus.DELAYED -> {
                    menu.findItem(R.id.menu_reminder_notify).isVisible = true
                    menu.findItem(R.id.menu_reminder_done).isVisible = true
                }
                ReminderStatus.PERIODIC -> {
                }
            }
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }
}