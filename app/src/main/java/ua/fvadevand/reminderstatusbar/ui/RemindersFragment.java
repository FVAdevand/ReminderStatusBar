package ua.fvadevand.reminderstatusbar.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.fvadevand.reminderstatusbar.R;
import ua.fvadevand.reminderstatusbar.adapters.ReminderAdapter;
import ua.fvadevand.reminderstatusbar.adapters.ReminderAdapter.OnReminderClickListener;
import ua.fvadevand.reminderstatusbar.listeners.FabVisibilityChangeListener;

public class RemindersFragment extends Fragment {

    public static final String TAG = "RemindersFragment";
    private FabVisibilityChangeListener mFabVisibilityChangeListener;
    private OnReminderClickListener mOnReminderClickListener;
    private ReminderAdapter mAdapter;

    public RemindersFragment() {
    }

    public static RemindersFragment newInstance() {
        return new RemindersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RemindersViewModel viewModel = ViewModelProviders.of(getActivity()).get(RemindersViewModel.class);
        setupRecyclerView(view);
        viewModel.getReminderList().observe(this, reminders -> mAdapter.setReminderList(reminders));
    }

    private void setupRecyclerView(View view) {
        RecyclerView reminderListView = view.findViewById(R.id.reminder_list);
        reminderListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ReminderAdapter(mOnReminderClickListener);
        reminderListView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mFabVisibilityChangeListener = (FabVisibilityChangeListener) context;
            mOnReminderClickListener = (OnReminderClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FabVisibilityChangeListener and OnReminderClickListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mFabVisibilityChangeListener.fabVisibilityChange(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFabVisibilityChangeListener = null;
        mOnReminderClickListener = null;
    }
}
