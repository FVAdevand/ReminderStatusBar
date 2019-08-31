package ua.fvadevand.reminderstatusbar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.Reminder

class ReminderSortMenuFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: RemindersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(RemindersViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminder_menu_sort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navigationView: NavigationView = view.findViewById(R.id.reminder_menu_sort)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_reminder_sort_title -> viewModel.setSortField(Reminder.COLUMN_TITLE)
                R.id.menu_reminder_sort_status -> viewModel.setSortField(Reminder.COLUMN_STATUS)
                R.id.menu_reminder_sort_timestamp -> viewModel.setSortField(Reminder.COLUMN_TIMESTAMP)
            }
            dismiss()
            true
        }
    }

    companion object {
        const val TAG = "ReminderSortMenuFragment"
    }
}