package ua.fvadevand.reminderstatusbar.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import ua.fvadevand.reminderstatusbar.adapters.IconAdapter;
import ua.fvadevand.reminderstatusbar.data.models.Reminder;
import ua.fvadevand.reminderstatusbar.dialogs.AlarmSetDialog;
import ua.fvadevand.reminderstatusbar.dialogs.AlarmSetDialog.OnAlarmSetListener;
import ua.fvadevand.reminderstatusbar.dialogs.IconsDialog;
import ua.fvadevand.reminderstatusbar.listeners.FabVisibilityChangeListener;
import ua.fvadevand.reminderstatusbar.utilities.IconUtils;
import ua.fvadevand.reminderstatusbar.utilities.ReminderDateUtils;

public class ReminderEditFragment extends Fragment
        implements View.OnClickListener, OnAlarmSetListener,
        IconAdapter.OnIconClickListener {

    public static final String TAG = "ReminderEditFragment";
    private static final String ARG_REMINDER_ID = "reminder_id";
    private FabVisibilityChangeListener mFabVisibilityChangeListener;
    private boolean isEditMode;
    private EditText mTitleView;
    private EditText mTextView;
    private ImageButton mIconBtn;
    private Button mNotifyBtn;
    private TextView mTimeView;
    private CheckBox mDelayNotificationView;
    private Calendar mCalendar;
    private int mIconResId;
    private RemindersViewModel mViewModel;
    private LiveData<Reminder> mCurrentReminderLive;
    private long mCurrentReminderId;

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
            mCurrentReminderId = getArguments().getLong(ARG_REMINDER_ID);
            isEditMode = mCurrentReminderId != Const.NEW_REMINDER_ID;
        } else {
            mCurrentReminderId = Const.NEW_REMINDER_ID;
        }
        mViewModel = ViewModelProviders.of(getActivity()).get(RemindersViewModel.class);
        if (isEditMode) {
            mCurrentReminderLive = mViewModel.getReminderById(mCurrentReminderId);
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
            mCurrentReminderLive.observe(this, reminder -> {
                mCurrentReminderLive.removeObservers(ReminderEditFragment.this);
                fillView(reminder);
            });
        } else {
            setVisibilityTimeView(false);
        }
    }

    private void initView(View view) {
        mTitleView = view.findViewById(R.id.et_edit_reminder_title);
        mTitleView.requestFocus();
        mTextView = view.findViewById(R.id.et_edit_reminder_text);
        (mIconBtn = view.findViewById(R.id.btn_edit_reminder_icon)).setOnClickListener(this);
        mIconResId = R.drawable.ic_notif_edit;
        mIconBtn.setImageResource(mIconResId);
        view.findViewById(R.id.btn_edit_reminder_time).setOnClickListener(this);
        (mNotifyBtn = view.findViewById(R.id.btn_edit_reminder_notify)).setOnClickListener(this);
        mTimeView = view.findViewById(R.id.tv_edit_reminder_time);
        mDelayNotificationView = view.findViewById(R.id.cb_edit_reminder_delay_notification);
        mCalendar = Calendar.getInstance();
    }

    private void fillView(@Nullable Reminder reminder) {
        if (reminder == null) {
            clearView();
            return;
        }
        mTitleView.setText(reminder.getTitle());
        mTextView.setText(reminder.getText());
        if (getContext() != null) {
            mIconResId = IconUtils.getIconResId(getContext(), reminder.getIconName());
            mIconBtn.setImageResource(mIconResId);
        }
        if (reminder.getTimestamp() > System.currentTimeMillis()) {
            setVisibilityTimeView(true);
            mCalendar.setTimeInMillis(reminder.getTimestamp());
            mTimeView.setText(getNotificationTimeString(mCalendar));
        } else {
            setVisibilityTimeView(false);
        }
    }

    private void clearView() {
        mTitleView.getText().clear();
        mTitleView.requestFocus();
        mTextView.getText().clear();
        setVisibilityTimeView(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_reminder_icon:
                showIconsDialog();
                break;
            case R.id.btn_edit_reminder_time:
                showSetAlarmDialog();
                break;
            case R.id.btn_edit_reminder_notify:
                saveAndNotifyReminder();
                break;
        }
    }

    private void showSetAlarmDialog() {
        AlarmSetDialog.newInstance(mCalendar).show(getChildFragmentManager(), AlarmSetDialog.TAG);
    }

    private void showIconsDialog() {
        new IconsDialog().show(getChildFragmentManager(), IconsDialog.TAG);
    }

    private void saveAndNotifyReminder() {
        String title = mTitleView.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            mTitleView.setError(getString(R.string.reminder_edit_error_empty_title));
            return;
        }
        String text = mTextView.getText().toString().trim();
        long timeInMillis;
        if (mDelayNotificationView.isChecked()) {
            timeInMillis = mCalendar.getTimeInMillis();
        } else {
            timeInMillis = System.currentTimeMillis();
        }
        Reminder reminder = new Reminder();
        if (isEditMode) {
            reminder.setId(mCurrentReminderId);
        }
        reminder.setTitle(title);
        reminder.setTimestamp(timeInMillis);
        reminder.setText(text);
        reminder.setIconName(IconUtils.getIconName(getContext(), mIconResId));
        mViewModel.insertReminder(reminder);
        isEditMode = false;
        clearView();
    }

    @Override
    public void onAlarmSet(Calendar calendar) {
        mCalendar = calendar;
        setVisibilityTimeView(true);
        mTimeView.setText(getNotificationTimeString(mCalendar));
        mNotifyBtn.setText(R.string.edit_reminder_action_save);
    }

    private String getNotificationTimeString(Calendar calendar) {
        return getString(R.string.edit_reminder_notification_time, ReminderDateUtils.getNotificationTime(getContext(), calendar));
    }

    private void setVisibilityTimeView(boolean isVisibility) {
        int visibility;
        if (isVisibility) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.INVISIBLE;
        }
        mDelayNotificationView.setVisibility(visibility);
        mDelayNotificationView.setChecked(isVisibility);
        mTimeView.setVisibility(visibility);
    }

    @Override
    public void onIconClick(int iconResId) {
        mIconResId = iconResId;
        mIconBtn.setImageResource(mIconResId);
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
