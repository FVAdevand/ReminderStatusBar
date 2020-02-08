package ua.fvadevand.reminderstatusbar.ui.dialogs

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
import ua.fvadevand.reminderstatusbar.utils.formatFullDate
import ua.fvadevand.reminderstatusbar.utils.formatTime
import java.util.Calendar

class AlarmSetDialog : DialogFragment() {

    private lateinit var timeTextView: TextView
    private lateinit var dateTextView: TextView
    private val calendar by lazy {
        Calendar.getInstance().apply {
            timeInMillis = arguments?.getLong(ARG_CALENDAR) ?: System.currentTimeMillis()
        }
    }
    private var periodType = PeriodType.ONE_TIME
    private var listener: OnAlarmSetListener? = null

    private var timeCallBack: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            with(calendar) {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            timeTextView.text = context.formatTime(calendar)
        }

    private var dateCallBack: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            with(calendar) {
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
            }
            dateTextView.text = context.formatFullDate(calendar)
        }

    companion object {

        const val TAG = "AlarmSetDialog"
        private const val ARG_CALENDAR = "calendar"
        private const val ARG_PERIOD_TYPE = "period_type"

        fun newInstance(timeInMillis: Long, @PeriodTypes periodType: Int) = AlarmSetDialog().apply {
            arguments = Bundle().apply {
                putLong(ARG_CALENDAR, timeInMillis)
                putInt(ARG_PERIOD_TYPE, periodType)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? OnAlarmSetListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        periodType = arguments?.getInt(ARG_PERIOD_TYPE) ?: PeriodType.ONE_TIME
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_alarm, null)
        timeTextView = rootView.findViewById(R.id.tv_alarm_dialog_time)
        timeTextView.text = context.formatTime(calendar)
        timeTextView.setOnClickListener { v ->
            TimePickerDialog(
                v.context,
                timeCallBack,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(v.context)
            ).show()
        }

        dateTextView = rootView.findViewById(R.id.tv_alarm_dialog_date)
        dateTextView.text = context.formatFullDate(calendar)
        dateTextView.setOnClickListener { v ->
            DatePickerDialog(
                v.context,
                dateCallBack,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        val spinner: Spinner = rootView.findViewById(R.id.spinner_alarm_dialog_repeat)
        val periodTypes = PeriodType.getPeriodTypes()
        val adapter = PeriodTypesAdapter(periodTypes)
        spinner.adapter = adapter
        spinner.setSelection(periodTypes.indexOf(periodType))
        return AlertDialog.Builder(context!!)
            .setView(rootView)
            .setTitle(R.string.alarm_dialog_title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                listener?.onAlarmSet(
                    calendar.timeInMillis,
                    adapter.getItem(spinner.selectedItemPosition)
                )
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    interface OnAlarmSetListener {
        fun onAlarmSet(alarmTimeInMillis: Long, @PeriodTypes periodType: Int)
    }
}
