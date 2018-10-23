package ua.fvadevand.reminderstatusbar.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import ua.fvadevand.reminderstatusbar.FakeDataUtils;
import ua.fvadevand.reminderstatusbar.R;
import ua.fvadevand.reminderstatusbar.data.models.Reminder;

public class MainActivity extends AppCompatActivity {

    private RemindersViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupViewFragment();

        mViewModel = ViewModelProviders.of(this).get(RemindersViewModel.class);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> mViewModel.insertReminder(FakeDataUtils.getReminder()));
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

    private void setupViewFragment() {
        RemindersFragment fragment =
                (RemindersFragment) getSupportFragmentManager().findFragmentByTag(RemindersFragment.TAG);
        if (fragment == null) {
            fragment = RemindersFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, RemindersFragment.TAG)
                    .commit();
        }
    }
}
