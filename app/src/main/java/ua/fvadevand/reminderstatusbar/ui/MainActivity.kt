package ua.fvadevand.reminderstatusbar.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.listeners.OnReminderInteractListener
import ua.fvadevand.reminderstatusbar.ui.dialogs.NightModeDialog
import ua.fvadevand.reminderstatusbar.utils.doOnApplyWindowInsets
import ua.fvadevand.reminderstatusbar.utils.updateSystemWindowInsets

class MainActivity : AppCompatActivity(), OnReminderInteractListener,
    NightModeDialog.OnNightModeSetListener {

    private lateinit var viewModel: RemindersViewModel
    private lateinit var fab: FloatingActionButton
    private lateinit var container: ViewGroup

    private var recentlyDeletedReminder: Reminder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(RemindersViewModel::class.java)
        initView()
        if (savedInstanceState == null) {
            showRemindersFragment()
        }

        viewModel.showSnackbar.subscribe {
            it?.let { data ->
                val snackbar = Snackbar.make(container, data.messageResId, data.duration)
                snackbar.anchorView = fab
                snackbar.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
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
        val snackbar = Snackbar.make(
            container,
            R.string.reminders_reminder_deleted_message,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.action_undo) {
            recentlyDeletedReminder?.let {
                viewModel.addReminder(reminder)
            }
        }
        snackbar.anchorView = fab
        snackbar.show()
    }

    override fun onNightModeSet(nightMode: Int) {
        viewModel.nightMode = nightMode
    }

    private fun initView() {
        container = findViewById(R.id.fragment_container)

        val bottomBar = findViewById<BottomAppBar>(R.id.bottom_app_bar)
        setSupportActionBar(bottomBar)

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            showReminderEditFragment(Const.NEW_REMINDER_ID)
        }

        window?.decorView?.doOnApplyWindowInsets { view, insets, initialPadding ->
            view.updatePadding(top = initialPadding.top + insets.systemWindowInsetTop)
            insets.updateSystemWindowInsets(top = 0)
        }

        bottomBar.doOnApplyWindowInsets { view, insets, initialPadding ->
            view.updatePadding(bottom = initialPadding.bottom + insets.systemWindowInsetBottom)
            insets.updateSystemWindowInsets(bottom = 0)
        }

        container.doOnApplyWindowInsets { view, insets, initialPadding ->
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

    private fun <T> LiveData<T>.subscribe(onChange: (T) -> Unit) {
        observe(this@MainActivity, Observer { onChange(it) })
    }
}
