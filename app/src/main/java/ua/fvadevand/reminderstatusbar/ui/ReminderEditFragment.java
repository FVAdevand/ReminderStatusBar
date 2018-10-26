package ua.fvadevand.reminderstatusbar.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

import ua.fvadevand.reminderstatusbar.Const;
import ua.fvadevand.reminderstatusbar.R;
import ua.fvadevand.reminderstatusbar.dialogs.AlarmSetDialog;
import ua.fvadevand.reminderstatusbar.listeners.FabVisibilityChangeListener;
import ua.fvadevand.reminderstatusbar.utilities.ReminderDateUtils;

public class ReminderEditFragment extends Fragment
        implements View.OnClickListener, AlarmSetDialog.OnAlarmSetListener {

    public static final String TAG = "ReminderEditFragment";
    private static final String ARG_REMINDER_ID = "reminder_id";
    private long mReminderId;
    private FabVisibilityChangeListener mFabVisibilityChangeListener;
    private boolean isEditMode;
    private EditText mTitleView;
    private EditText mTextView;
    private ImageButton mIconBtn;
    private ImageButton mTimeBtn;
    private Button mNotifyBtn;
    private TextView mTimeView;
    private CheckBox mDelayNotificationView;
    private Calendar mCalendar;

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
        initView(view);
        if (isEditMode) {

        } else {
            mCalendar = Calendar.getInstance();
            setVisibilityTimeView(false);
        }

    }

    private void initView(View view) {
        mTitleView = view.findViewById(R.id.et_edit_reminder_title);
        mTextView = view.findViewById(R.id.et_edit_reminder_text);
        (mIconBtn = view.findViewById(R.id.btn_edit_reminder_icon)).setOnClickListener(this);
        (mTimeBtn = view.findViewById(R.id.btn_edit_reminder_time)).setOnClickListener(this);
        (mNotifyBtn = view.findViewById(R.id.btn_edit_reminder_notify)).setOnClickListener(this);
        mTimeView = view.findViewById(R.id.tv_edit_reminder_time);
        mDelayNotificationView = view.findViewById(R.id.cb_edit_reminder_delay_notification);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_reminder_icon:
                break;
            case R.id.btn_edit_reminder_time:
                showSetAlarmDialog();
                break;
        }
    }

    private void showSetAlarmDialog() {
        AlarmSetDialog.newInstance(mCalendar).show(getChildFragmentManager(), AlarmSetDialog.TAG);
    }

    @Override
    public void onAlarmSet(Calendar calendar) {
        mCalendar = calendar;
        setVisibilityTimeView(true);
        mDelayNotificationView.setChecked(true);
        String notificationTimeStrig = getString(R.string.edit_reminder_notification_time, ReminderDateUtils.getNotificationTime(getContext(), mCalendar));
        mTimeView.setText(notificationTimeStrig);
        mNotifyBtn.setText(R.string.edit_reminder_action_save);
    }

    private void setVisibilityTimeView(boolean isVisibility) {
        int visibility;
        if (isVisibility) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.INVISIBLE;
        }
        mDelayNotificationView.setVisibility(visibility);
        mTimeView.setVisibility(visibility);
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
