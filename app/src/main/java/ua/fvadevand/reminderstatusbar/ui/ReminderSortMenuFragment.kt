package ua.fvadevand.reminderstatusbar.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.databinding.FragmentReminderMenuSortBinding

class ReminderSortMenuFragment :
    BaseBottomSheetDialogFragment(R.layout.fragment_reminder_menu_sort) {

    private val viewModel: RemindersViewModel by activityViewModels()
    private val binding by fragmentProperty.fragmentLateinitViewBindingByView(
        FragmentReminderMenuSortBinding::bind
    )

    companion object {
        const val TAG = "ReminderSortMenuFragment"

        fun newInstance() = ReminderSortMenuFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.reminderMenuSort.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_reminder_sort_title -> viewModel.setSortField(Reminder.COLUMN_TITLE)
                R.id.menu_reminder_sort_status -> viewModel.setSortField(Reminder.COLUMN_STATUS)
                R.id.menu_reminder_sort_timestamp -> viewModel.setSortField(Reminder.COLUMN_TIMESTAMP)
            }
            dismiss()
            true
        }
    }
}