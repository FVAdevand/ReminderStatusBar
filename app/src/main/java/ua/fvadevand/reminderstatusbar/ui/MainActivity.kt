package ua.fvadevand.reminderstatusbar.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import com.google.android.material.snackbar.Snackbar
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.databinding.ActivityMainBinding
import ua.fvadevand.reminderstatusbar.listeners.OnReminderInteractListener
import ua.fvadevand.reminderstatusbar.ui.dialogs.NightModeDialog
import ua.fvadevand.reminderstatusbar.utils.doOnApplyWindowInsets
import ua.fvadevand.reminderstatusbar.utils.updateSystemWindowInsets

class MainActivity : AppCompatActivity(), OnReminderInteractListener,
    NightModeDialog.OnNightModeSetListener {

    private val viewModel: RemindersViewModel by viewModels()
    private var recentlyDeletedReminder: Reminder? = null
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    companion object {
        private const val EXTRA_REMINDER_ID = "REMINDER_ID"

        fun getOpenIntent(context: Context, reminderId: Long): PendingIntent {
            return PendingIntent.getActivity(
                context,
                reminderId.hashCode(),
                Intent(context, MainActivity::class.java)
                    .putExtra(EXTRA_REMINDER_ID, reminderId),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        if (savedInstanceState == null) {
            showRemindersFragment()
            handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sorting -> showReminderSortMenuFragment()
            R.id.action_settings -> showThemeSettingsDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onReminderClick(id: Long) {
        showReminderMenuFragment(id)
    }

    override fun onReminderEdit(id: Long) {
        showReminderEditFragment(id)
    }

    override fun onReminderDelete(reminder: Reminder) {
        recentlyDeletedReminder = reminder
        viewModel.deleteReminder(reminder.id)
        Snackbar.make(
            binding.fragmentContainer,
            R.string.reminders_reminder_deleted_message,
            Snackbar.LENGTH_LONG
        ).apply {
            setAction(R.string.action_undo) {
                recentlyDeletedReminder?.let {
                    viewModel.addReminder(reminder)
                }
            }
            anchorView = binding.fab
            show()
        }
    }

    override fun onNightModeSet(nightMode: Int) {
        viewModel.nightMode = nightMode
    }

    private fun initView() {
        setSupportActionBar(binding.bottomAppBar)
        binding.fab.setOnClickListener {
            showReminderEditFragment(Const.NEW_REMINDER_ID)
        }

        window?.decorView?.doOnApplyWindowInsets { view, insets, initialPadding ->
            view.updatePadding(top = initialPadding.top + insets.systemWindowInsetTop)
            insets.updateSystemWindowInsets(top = 0)
        }

        binding.bottomAppBar.doOnApplyWindowInsets { view, insets, initialPadding ->
            view.updatePadding(bottom = initialPadding.bottom + insets.systemWindowInsetBottom)
            insets.updateSystemWindowInsets(bottom = 0)
        }

        binding.fragmentContainer.doOnApplyWindowInsets { view, insets, initialPadding ->
            view.updatePadding(bottom = initialPadding.bottom + insets.systemWindowInsetBottom)
            insets
        }
    }

    private fun showRemindersFragment() {
        val fragment = supportFragmentManager.findFragmentByTag(RemindersFragment.TAG)
            ?: RemindersFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, RemindersFragment.TAG)
            .commit()
    }

    private fun showReminderEditFragment(reminderId: Long) {
        ReminderEditFragment.newInstance(reminderId)
            .show(supportFragmentManager, ReminderEditFragment.TAG)
    }

    private fun showReminderMenuFragment(reminderId: Long) {
        ReminderMenuFragment.newInstance(reminderId)
            .show(supportFragmentManager, ReminderMenuFragment.TAG)
    }

    private fun showReminderSortMenuFragment() {
        ReminderSortMenuFragment.newInstance()
            .show(supportFragmentManager, ReminderSortMenuFragment.TAG)
    }

    private fun showThemeSettingsDialog() {
        NightModeDialog.newInstance(viewModel.nightMode)
            .show(supportFragmentManager, NightModeDialog.TAG)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.getLongExtra(EXTRA_REMINDER_ID, Const.NEW_REMINDER_ID)?.let { reminderId ->
            if (reminderId != Const.NEW_REMINDER_ID)
                viewModel.openReminder(reminderId)
        }
    }

}
