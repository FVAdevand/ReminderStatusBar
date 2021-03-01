package ua.fvadevand.reminderstatusbar.ui.dialogs

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private val timeListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            with(calendar) {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            binding.tvAlarmDialogTime.text = context.formatTime(calendar)
        }

    private val dateListener: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            with(calendar) {
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
            }
            binding.tvAlarmDialogDate.text = context.formatFullDate(calendar)
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

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding.tvAlarmDialogTime.text = context.formatTime(calendar)
        binding.tvAlarmDialogTime.setOnClickListener { v ->
            TimePickerDialog(
                v.context,
                timeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(v.context)
            ).show()
        }

        binding.tvAlarmDialogDate.text = context.formatFullDate(calendar)
        binding.tvAlarmDialogDate.setOnClickListener { v ->
            DatePickerDialog(
                v.context,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis()
            }.show()
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
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
    }

    interface OnAlarmSetListener {
        fun onAlarmSet(alarmTimeInMillis: Long, @PeriodTypes periodType: Int)
    }
}
