package ua.fvadevand.reminderstatusbar.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import ua.fvadevand.reminderstatusbar.Const;
import ua.fvadevand.reminderstatusbar.FakeDataUtils;
import ua.fvadevand.reminderstatusbar.R;
import ua.fvadevand.reminderstatusbar.adapters.ReminderAdapter.OnReminderClickListener;
import ua.fvadevand.reminderstatusbar.data.models.Reminder;
import ua.fvadevand.reminderstatusbar.listeners.FabVisibilityChangeListener;

public class MainActivity extends AppCompatActivity
        implements FabVisibilityChangeListener, OnReminderClickListener {

    private RemindersViewModel mViewModel;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        showRemindersFragment();

        mViewModel = ViewModelProviders.of(this).get(RemindersViewModel.class);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(v -> showReminderEditFragment(Const.NEW_REMINDER_ID));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_all:
                List<Reminder> fakeReminderList = FakeDataUtils.getReminderList();
                for (Reminder reminder : fakeReminderList) {
                    mViewModel.insertReminder(reminder);
                }
                return true;
            case R.id.action_clear:
                mViewModel.clearDb();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void showRemindersFragment() {
        RemindersFragment fragment =
                (RemindersFragment) getSupportFragmentManager().findFragmentByTag(RemindersFragment.TAG);
        if (fragment == null) {
            fragment = RemindersFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, RemindersFragment.TAG)
                    .commit();
        }
    }

    private void showReminderEditFragment(long reminderId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ReminderEditFragment.newInstance(reminderId), ReminderEditFragment.TAG)
                .addToBackStack(ReminderEditFragment.TAG)
                .commit();
    }

    @Override
    public void fabVisibilityChange(boolean isVisible) {
        if (isVisible) {
            mFab.show();
        } else {
            mFab.hide();
        }
    }

    @Override
    public void onReminderClick(long id) {
        showReminderEditFragment(id);
    }
}
