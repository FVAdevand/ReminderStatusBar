package ua.fvadevand.reminderstatusbar.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.databinding.FragmentReminderMenuBinding
import ua.fvadevand.reminderstatusbar.listeners.OnReminderInteractListener
import ua.fvadevand.reminderstatusbar.utils.setImageResourceName

class ReminderMenuFragment : BaseBottomSheetDialogFragment(R.layout.fragment_reminder_menu) {

    private val viewModel: RemindersViewModel by activityViewModels()
    private val binding by fragmentProperty.fragmentLateinitViewBindingByView(
        FragmentReminderMenuBinding::bind
    )
    private var listener: OnReminderInteractListener? by fragmentProperty.delegateFragmentLifecycle()
    private val currentReminderId by lazy {
        arguments?.getLong(ARG_REMINDER_ID) ?: Const.NEW_REMINDER_ID
    }

    companion object {
        const val TAG = "ReminderMenuFragment"
        private const val ARG_REMINDER_ID = "REMINDER_ID"

        fun newInstance(reminderId: Long) = ReminderMenuFragment().apply {
            arguments = Bundle().apply { putLong(ARG_REMINDER_ID, reminderId) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnReminderInteractListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.reminderMenu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_reminder_edit -> listener?.onReminderEdit(currentReminderId)
                R.id.menu_reminder_notify -> viewModel.notifyReminder(currentReminderId)
                R.id.menu_reminder_done -> viewModel.setReminderStatusDone(currentReminderId)
                R.id.menu_reminder_pause -> viewModel.pausePeriodicReminder(currentReminderId)
                R.id.menu_reminder_restore -> viewModel.restorePeriodicReminder(currentReminderId)
                R.id.menu_reminder_delete -> viewModel.deleteReminder(currentReminderId)
            }
            dismiss()
            true
        }
        viewModel.getReminder(currentReminderId) {
            if (it == null) return@getReminder
            binding.ivMenuReminderIcon.setImageResourceName(it.iconName)
            binding.tvMenuReminderTitle.text = it.title
            val menu = binding.reminderMenu.menu
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
                    if (it.periodAccepted) {
                        menu.findItem(R.id.menu_reminder_notify).isVisible = true
                    } else {
                        menu.findItem(R.id.menu_reminder_done).isVisible = true
                    }
                    menu.findItem(R.id.menu_reminder_pause).isVisible = true
                }

                ReminderStatus.PAUSED -> {
                    menu.findItem(R.id.menu_reminder_restore).isVisible = true
                }
            }
        }
    }

}