package ua.fvadevand.reminderstatusbar.dialogs

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.adapters.PeriodTypesAdapter
import ua.fvadevand.reminderstatusbar.data.models.PeriodType
import ua.fvadevand.reminderstatusbar.data.models.PeriodType.PeriodTypes
import ua.fvadevand.reminderstatusbar.utils.ReminderDateUtils
import java.util.Calendar

class AlarmSetDialog : DialogFragment() {

    private lateinit var timeTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var calendar: Calendar
    private var periodType = PeriodType.ONE_TIME
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
        periodType = arguments?.getInt(ARG_PERIOD_TYPE) ?: PeriodType.ONE_TIME
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context!!
        val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_alarm, null)
        timeTextView = rootView.findViewById(R.id.tv_alarm_dialog_time)
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

        dateTextView = rootView.findViewById(R.id.tv_alarm_dialog_date)
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
        val spinner: Spinner = rootView.findViewById(R.id.spinner_alarm_dialog_repeat)
        val periodTypes = PeriodType.getPeriodTypes()
        val adapter = PeriodTypesAdapter(context, periodTypes)
        spinner.adapter = adapter
        for (i in 0 until periodTypes.size) {
            if (periodType == periodTypes[i]) {
                spinner.setSelection(i)
                break
            }
        }
        return AlertDialog.Builder(context)
                .setView(rootView)
                .setTitle(R.string.alarm_dialog_title)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    listener?.onAlarmSet(calendar.timeInMillis, adapter.getItem(spinner.selectedItemPosition))
                }
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
        fun onAlarmSet(alarmTimeInMillis: Long, @PeriodTypes periodType: Int)
    }

    companion object {

        const val TAG = "AlarmSetDialog"
        private const val ARG_CALENDAR = "calendar"
        private const val ARG_PERIOD_TYPE = "period_type"

        fun newInstance(timeInMillis: Long, @PeriodTypes periodType: Int): AlarmSetDialog {
            val fragment = AlarmSetDialog()
            val args = Bundle()
            args.putLong(ARG_CALENDAR, timeInMillis)
            args.putInt(ARG_PERIOD_TYPE, periodType)
            fragment.arguments = args
            return fragment
        }
    }
}
