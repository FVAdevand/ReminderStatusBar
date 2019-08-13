package ua.fvadevand.reminderstatusbar.dialogs

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.utils.ReminderDateUtils
import java.util.Calendar

class AlarmSetDialog : DialogFragment() {

    private lateinit var timeTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var calendar: Calendar
    private var listener: OnAlarmSetListener? = null

    private var timeCallBack: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        with(calendar) {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        timeTextView.text = ReminderDateUtils.formatTime(view.context, calendar)
    }

    private var dateCallBack: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        with(calendar) {
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }
        dateTextView.text = ReminderDateUtils.formatFullDate(view.context, calendar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance()
        calendar.timeInMillis = arguments?.getLong(ARG_CALENDAR) ?: System.currentTimeMillis()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_alarm, null)
        timeTextView = rootView.findViewById(R.id.tv_time)
        timeTextView.text = ReminderDateUtils.formatTime(context, calendar)
        timeTextView.setOnClickListener { v ->
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val timeDialog = TimePickerDialog(v.context,
                    timeCallBack,
                    hour,
                    minute,
                    DateFormat.is24HourFormat(v.context))
            timeDialog.show()
        }

        dateTextView = rootView.findViewById(R.id.tv_date)
        dateTextView.text = ReminderDateUtils.formatFullDate(context, calendar)
        dateTextView.setOnClickListener { v ->
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val dateDialog = DatePickerDialog(v.context,
                    dateCallBack,
                    year,
                    month,
                    day)
            dateDialog.show()
        }

        return AlertDialog.Builder(context)
                .setView(rootView)
                .setTitle(R.string.alarm_dialog_title)
                .setPositiveButton(android.R.string.ok) { _, _ -> listener?.onAlarmSet(calendar.timeInMillis) }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = parentFragment as OnAlarmSetListener?
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    interface OnAlarmSetListener {
        fun onAlarmSet(alarmTimeInMillis: Long)
    }

    companion object {

        const val TAG = "AlarmSetDialog"
        private const val ARG_CALENDAR = "calendar"

        fun newInstance(timeInMillis: Long): AlarmSetDialog {
            val fragment = AlarmSetDialog()
            val args = Bundle()
            args.putLong(ARG_CALENDAR, timeInMillis)
            fragment.arguments = args
            return fragment
        }
    }
}
