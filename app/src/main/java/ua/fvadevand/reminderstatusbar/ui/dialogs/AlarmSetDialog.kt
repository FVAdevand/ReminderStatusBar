package ua.fvadevand.reminderstatusbar.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.adapters.PeriodTypesAdapter
import ua.fvadevand.reminderstatusbar.data.models.PeriodType
import ua.fvadevand.reminderstatusbar.data.models.PeriodType.PeriodTypes
import ua.fvadevand.reminderstatusbar.databinding.DialogAlarmBinding
import ua.fvadevand.reminderstatusbar.utils.formatFullDate
import ua.fvadevand.reminderstatusbar.utils.formatTime
import ua.fvadevand.reminderstatusbar.utils.fragmentProperty
import java.util.Calendar

class AlarmSetDialog : DialogFragment() {

    private val fragmentProperty by fragmentProperty()
    private val binding by fragmentProperty.fragmentLateinitViewBindingByInflater(DialogAlarmBinding::inflate)
    private val calendar by lazy {
        Calendar.getInstance().apply {
            timeInMillis = arguments?.getLong(ARG_CALENDAR) ?: System.currentTimeMillis()
        }
    }
    private val periodType by lazy { arguments?.getInt(ARG_PERIOD_TYPE) ?: PeriodType.ONE_TIME }
    private var listener: OnAlarmSetListener? by fragmentProperty.delegateFragmentLifecycle()

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding.tvAlarmDialogTime.text = context.formatTime(calendar)
        binding.tvAlarmDialogTime.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(
                    if (DateFormat.is24HourFormat(requireContext())) TimeFormat.CLOCK_24H
                    else TimeFormat.CLOCK_12H
                )
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .build()
            picker.addOnPositiveButtonClickListener {
                with(calendar) {
                    set(Calendar.HOUR_OF_DAY, picker.hour)
                    set(Calendar.MINUTE, picker.minute)
                }
                binding.tvAlarmDialogTime.text = context.formatTime(calendar)
            }
            picker.show(childFragmentManager, picker.javaClass.simpleName)
        }

        binding.tvAlarmDialogDate.text = context.formatFullDate(calendar)
        binding.tvAlarmDialogDate.setOnClickListener {
            val calendarConstraint = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build()

            val picker = MaterialDatePicker.Builder.datePicker()
                .setSelection(calendar.timeInMillis)
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(calendarConstraint)
                .build()
            picker.addOnPositiveButtonClickListener { timeInMillis ->
                val newCalendar = Calendar.getInstance()
                newCalendar.timeInMillis = timeInMillis
                with(calendar) {
                    set(Calendar.DAY_OF_MONTH, newCalendar.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.MONTH, newCalendar.get(Calendar.MONTH))
                    set(Calendar.YEAR, newCalendar.get(Calendar.YEAR))
                }
                binding.tvAlarmDialogDate.text = context.formatFullDate(calendar)
            }
            picker.show(childFragmentManager, picker.javaClass.simpleName)
        }
        val periodTypes = PeriodType.getPeriodTypes()
        val adapter = PeriodTypesAdapter(periodTypes)
        binding.spinnerAlarmDialogRepeat.adapter = adapter
        binding.spinnerAlarmDialogRepeat.setSelection(periodTypes.indexOf(periodType))
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(R.string.alarm_dialog_title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                listener?.onAlarmSet(
                    calendar.timeInMillis,
                    adapter.getItem(binding.spinnerAlarmDialogRepeat.selectedItemPosition)
                )
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                listener?.onAlarmCancelled()
            }
            .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener?.onAlarmCancelled()
    }

    interface OnAlarmSetListener {
        fun onAlarmSet(alarmTimeInMillis: Long, @PeriodTypes periodType: Int)
        fun onAlarmCancelled()
    }

}
