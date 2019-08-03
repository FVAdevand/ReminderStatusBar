package ua.fvadevand.reminderstatusbar.dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import ua.fvadevand.reminderstatusbar.R;
import ua.fvadevand.reminderstatusbar.utilities.ReminderDateUtils;

public class AlarmSetDialog extends DialogFragment {

    public static final String TAG = "AlarmSetDialog";
    private static final String ARG_CALENDAR = "calendar";
    private OnAlarmSetListener mListener;
    private TextView mTimeTextView;
    private TextView mDateTextView;
    private Calendar mCalendar;

    TimePickerDialog.OnTimeSetListener timeCallBack = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            mTimeTextView.setText(ReminderDateUtils.formatTime(view.getContext(), mCalendar));
        }
    };

    DatePickerDialog.OnDateSetListener dateCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.YEAR, year);
            mDateTextView.setText(ReminderDateUtils.formatFullDate(view.getContext(), mCalendar));
        }
    };

    public static AlarmSetDialog newInstance(Calendar calendar) {
        AlarmSetDialog fragment = new AlarmSetDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CALENDAR, calendar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mCalendar = (Calendar) getArguments().getSerializable(ARG_CALENDAR);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View rootView = layoutInflater.inflate(R.layout.dialog_alarm, null);
        mTimeTextView = rootView.findViewById(R.id.tv_time);
        mTimeTextView.setText(ReminderDateUtils.formatTime(context, mCalendar));
        mTimeTextView.setOnClickListener(v -> {
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = mCalendar.get(Calendar.MINUTE);
            TimePickerDialog timeDialog = new TimePickerDialog(v.getContext(),
                    timeCallBack,
                    hour,
                    minute,
                    DateFormat.is24HourFormat(v.getContext()));
            timeDialog.show();
        });

        mDateTextView = rootView.findViewById(R.id.tv_date);
        mDateTextView.setText(ReminderDateUtils.formatFullDate(context, mCalendar));
        mDateTextView.setOnClickListener(v -> {
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH);
            int day = mCalendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dateDialog = new DatePickerDialog(v.getContext(),
                    dateCallBack,
                    year,
                    month,
                    day);
            dateDialog.show();
        });

        return new AlertDialog.Builder(context)
                .setView(rootView)
                .setTitle(R.string.alarm_dialog_title)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> mListener.onAlarmSet(mCalendar))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {

                })
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnAlarmSetListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment() + " must implement OnAlarmSetListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAlarmSetListener {
        void onAlarmSet(Calendar calendar);
    }
}
