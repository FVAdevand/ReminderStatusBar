package ua.fvadevand.reminderstatusbar.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.FakeDataUtils
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.listeners.OnFabVisibilityChangeListener

class MainActivity : AppCompatActivity(), OnFabVisibilityChangeListener, RemindersFragment.OnReminderClickListener {

    private lateinit var viewModel: RemindersViewModel
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(RemindersViewModel::class.java)
        setupToolbar()
        if (savedInstanceState == null) {
            showRemindersFragment()
        }
        fab = findViewById(R.id.fab)
        fab.setOnClickListener { showReminderEditFragment(Const.NEW_REMINDER_ID) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_all -> FakeDataUtils.reminderList.forEach { viewModel.insertReminder(it) }
            R.id.action_clear -> viewModel.clearDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun showRemindersFragment() {
        val fragment = supportFragmentManager.findFragmentByTag(RemindersFragment.TAG)
                ?: RemindersFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, RemindersFragment.TAG)
                .commit()
    }

    private fun showReminderEditFragment(reminderId: Long) {
        val fragment = supportFragmentManager.findFragmentByTag(ReminderEditFragment.TAG)
                ?: ReminderEditFragment.newInstance(reminderId)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, ReminderEditFragment.TAG)
                .addToBackStack(ReminderEditFragment.TAG)
                .commit()
    }

    override fun onFabVisibilityChange(isVisible: Boolean) {
        if (isVisible) fab.show() else fab.hide()
    }

    override fun onReminderClick(id: Long) {
        showReminderEditFragment(id)
    }
}
