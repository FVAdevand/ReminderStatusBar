package ua.fvadevand.reminderstatusbar.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.listeners.OnReminderClickListener

class MainActivity : AppCompatActivity(), OnReminderClickListener {

    private lateinit var viewModel: RemindersViewModel
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_app_bar)
        viewModel = ViewModelProvider(this).get(RemindersViewModel::class.java)
        initView()
        if (savedInstanceState == null) {
            showRemindersFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sorting -> showReminderSortMenuFragment()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            viewModel.currentReminderId = Const.NEW_REMINDER_ID
            showReminderEditFragment()
        }
    }

    private fun showRemindersFragment() {
        val fragment = supportFragmentManager.findFragmentByTag(RemindersFragment.TAG)
            ?: RemindersFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, RemindersFragment.TAG)
            .commit()
    }

    private fun showReminderEditFragment() {
        val fragment =
            supportFragmentManager.findFragmentByTag(ReminderEditFragment.TAG) as? ReminderEditFragment
                ?: ReminderEditFragment()
        fragment.show(supportFragmentManager, ReminderEditFragment.TAG)
    }

    private fun showReminderMenuFragment() {
        val fragment =
            supportFragmentManager.findFragmentByTag(ReminderMenuFragment.TAG) as? ReminderMenuFragment
                ?: ReminderMenuFragment()
        fragment.show(supportFragmentManager, ReminderMenuFragment.TAG)
    }

    private fun showReminderSortMenuFragment() {
        val fragment =
            supportFragmentManager.findFragmentByTag(ReminderSortMenuFragment.TAG) as? ReminderSortMenuFragment
                ?: ReminderSortMenuFragment()
        fragment.show(supportFragmentManager, ReminderSortMenuFragment.TAG)
    }

    override fun onClickReminder() {
        showReminderMenuFragment()
    }

    override fun onClickReminderEdit() {
        showReminderEditFragment()
    }
}
