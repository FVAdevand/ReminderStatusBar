package ua.fvadevand.reminderstatusbar.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.fvadevand.reminderstatusbar.Const;
import ua.fvadevand.reminderstatusbar.R;
import ua.fvadevand.reminderstatusbar.listeners.FabVisibilityChangeListener;

public class ReminderEditFragment extends Fragment {

    public static final String TAG = "ReminderEditFragment";
    private static final String ARG_REMINDER_ID = "reminder_id";
    private long mReminderId;
    private FabVisibilityChangeListener mFabVisibilityChangeListener;
    private boolean isEditMode;

    public ReminderEditFragment() {
    }

    public static ReminderEditFragment newInstance(long reminderId) {
        ReminderEditFragment fragment = new ReminderEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_REMINDER_ID, reminderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mReminderId = getArguments().getLong(ARG_REMINDER_ID);
            isEditMode = mReminderId != Const.NEW_REMINDER_ID;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FabVisibilityChangeListener) {
            mFabVisibilityChangeListener = (FabVisibilityChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FabVisibilityChangeListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mFabVisibilityChangeListener.fabVisibilityChange(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFabVisibilityChangeListener = null;
    }
}
