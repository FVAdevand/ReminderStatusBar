package ua.fvadevand.reminderstatusbar.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.PeriodType
import ua.fvadevand.reminderstatusbar.data.models.PeriodType.PeriodTypes
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.databinding.FragmentReminderEditBinding
import ua.fvadevand.reminderstatusbar.ui.dialogs.AlarmSetDialog
import ua.fvadevand.reminderstatusbar.ui.dialogs.AlarmSetDialog.OnAlarmSetListener
import ua.fvadevand.reminderstatusbar.ui.dialogs.IconsDialog
import ua.fvadevand.reminderstatusbar.utils.getNotificationTime
import ua.fvadevand.reminderstatusbar.utils.hideSoftKeyboard
import ua.fvadevand.reminderstatusbar.utils.setImageResourceName
import ua.fvadevand.reminderstatusbar.utils.showSoftKeyboard
import ua.fvadevand.reminderstatusbar.utils.toResId
import ua.fvadevand.reminderstatusbar.utils.toResName
import ua.fvadevand.reminderstatusbar.utils.updateSystemWindowInsets
import java.util.Locale

class ReminderEditFragment : BaseBottomSheetDialogFragment(R.layout.fragment_reminder_edit),
    View.OnClickListener,
    OnAlarmSetListener,
    IconsDialog.OnIconClickListener {

    private val binding by fragmentProperty.bindingByView(
        FragmentReminderEditBinding::bind
    )
    private val viewModel: RemindersViewModel by activityViewModels()
    private var startTimeInMillis = System.currentTimeMillis()
    private var editMode = false
    private var iconResId = R.drawable.ic_notif_edit

    private var currentFocus: View? by fragmentProperty.delegateViewLifecycle()
    private var hasKeyboard = false

    @PeriodTypes
    private var periodType = PeriodType.ONE_TIME
    private var navBarHeight = -1
    private var behavior: BottomSheetBehavior<View>? by fragmentProperty.delegateViewLifecycle()
    private val currentReminderId by lazy {
        arguments?.getLong(ARG_REMINDER_ID) ?: Const.NEW_REMINDER_ID
    }

    companion object {
        const val TAG = "ReminderEditFragment"
        private const val DELAY_UP_DIALOG = 100L
        private const val DELAY_DOWN_DIALOG = 50L
        private const val ARG_REMINDER_ID = "REMINDER_ID"

        fun newInstance(reminderId: Long) = ReminderEditFragment().apply {
            arguments = Bundle().apply { putLong(ARG_REMINDER_ID, reminderId) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener {
            (view.parent as? View)?.let {
                behavior = BottomSheetBehavior.from(it).apply {
                    peekHeight = 0
                    skipCollapsed = true
                }
            }
        }
        editMode = currentReminderId != Const.NEW_REMINDER_ID
        initView()
        if (editMode) {
            viewModel.getReminder(currentReminderId) {
                it?.let { populateData(it) }
            }
        }
        dialog?.window?.decorView?.also { decorView ->
            ViewCompat.setOnApplyWindowInsetsListener(decorView) { v, insets ->
                if (navBarHeight == -1) navBarHeight = insets.systemWindowInsetBottom
                val heightDifference = insets.systemWindowInsetBottom - navBarHeight
                if (view.paddingBottom != heightDifference) {
                    val animator = ValueAnimator.ofInt(view.paddingBottom, heightDifference)
                    val duration = if (heightDifference == 0) DELAY_DOWN_DIALOG else DELAY_UP_DIALOG
                    animator.addUpdateListener { view.setPadding(0, 0, 0, it.animatedValue as Int) }
                    animator.doOnEnd { behavior?.state = BottomSheetBehavior.STATE_EXPANDED }
                    animator.duration = duration
                    animator.start()
                }

                ViewCompat.onApplyWindowInsets(
                    v,
                    insets.updateSystemWindowInsets(bottom = navBarHeight)
                )
            }
        }
    }

    override fun onIconClick(iconId: Int) {
        iconResId = iconId
        binding.btnEditReminderIcon.setImageResource(iconResId)
    }

    override fun onDestroyView() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        super.onDestroyView()
    }

    private fun initView() {
        binding.apply {
            val editTextFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) currentFocus = v
            }
            etEditReminderTitle.onFocusChangeListener = editTextFocusChangeListener
            etEditReminderText.onFocusChangeListener = editTextFocusChangeListener
            etEditReminderTitle.showSoftKeyboard()
            etEditReminderText.setHorizontallyScrolling(false)
            etEditReminderText.maxLines = 20
            etEditReminderText.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        if (etEditReminderTitle.text.isNullOrEmpty()) {
                            etEditReminderTitle.requestFocus()
                        } else {
                            saveAndNotifyReminder()
                        }
                        true
                    }
                    else -> false
                }
            }
            btnEditReminderNotify.isEnabled = false
            etEditReminderTitle.addTextChangedListener(afterTextChanged = {
                btnEditReminderNotify.isEnabled = it.toString().trim().isNotEmpty()
            })
            btnEditReminderIcon.setOnClickListener(this@ReminderEditFragment)
            btnEditReminderIcon.setImageResource(R.drawable.ic_grid)
            btnEditReminderTime.setOnClickListener(this@ReminderEditFragment)
            btnEditReminderNotify.setOnClickListener(this@ReminderEditFragment)
            chipEditReminderRepeat.setOnCloseIconClickListener {
                it.isVisible = false
                periodType = PeriodType.ONE_TIME
            }
            chipEditReminderTime.setOnCloseIconClickListener {
                it.isVisible = false
                startTimeInMillis = System.currentTimeMillis()
                setActionText()
            }
            startTimeInMillis = System.currentTimeMillis()
        }
    }

    private fun populateData(reminder: Reminder) {
        binding.etEditReminderTitle.setText(reminder.title)
        binding.etEditReminderText.setText(reminder.text)
        binding.btnEditReminderIcon.run {
            iconResId = context.toResId(reminder.iconName)
            setImageResource(iconResId)
            setImageResourceName(reminder.iconName)
        }
        val now = System.currentTimeMillis()
        startTimeInMillis =
            if (reminder.status == ReminderStatus.PAUSED && now > reminder.timestamp) {
                PeriodType.getNextAlarmTimeByType(reminder.periodType, reminder.timestamp)
            } else {
                reminder.timestamp
            }
        if (startTimeInMillis > now) {
            binding.chipEditReminderTime.isVisible = true
            binding.chipEditReminderTime.text = context.getNotificationTime(startTimeInMillis)
        } else {
            binding.chipEditReminderTime.isVisible = false
        }
        periodType = reminder.periodType
        binding.chipEditReminderRepeat.isVisible = if (periodType == PeriodType.ONE_TIME) {
            false
        } else {
            binding.chipEditReminderRepeat.text = getRepeatString()
            true
        }
        setActionText()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_edit_reminder_icon -> showIconsDialog()
            R.id.btn_edit_reminder_time -> showSetAlarmDialog()
            R.id.btn_edit_reminder_notify -> saveAndNotifyReminder()
        }
    }

    private fun showSetAlarmDialog() {
        hasKeyboard = binding.root.paddingBottom != 0
        currentFocus?.hideSoftKeyboard()
        AlarmSetDialog.newInstance(startTimeInMillis, periodType)
            .show(childFragmentManager, AlarmSetDialog.TAG)
    }

    private fun showIconsDialog() {
        IconsDialog.newInstance().show(childFragmentManager, IconsDialog.TAG)
    }

    private fun saveAndNotifyReminder() {
        val title = binding.etEditReminderTitle.text.toString().trim()
        if (title.isEmpty()) {
            binding.etEditReminderTitle.error =
                getString(R.string.edit_reminder_error_empty_title)
            return
        }
        val text = binding.etEditReminderText.text.toString().trim()
        val status = when {
            periodType > PeriodType.ONE_TIME -> ReminderStatus.PERIODIC
            startTimeInMillis > System.currentTimeMillis() -> ReminderStatus.DELAYED
            else -> ReminderStatus.NOTIFYING
        }
        val reminder = Reminder(
            title,
            text,
            requireContext().toResName(iconResId),
            startTimeInMillis,
            status,
            periodType
        )
        if (editMode) {
            reminder.id = currentReminderId
        }
        viewModel.addReminder(reminder)
        editMode = false
        dismiss()
    }

    override fun onAlarmSet(alarmTimeInMillis: Long, @PeriodTypes periodType: Int) {
        restoreKeyboard()
        startTimeInMillis = alarmTimeInMillis
        binding.chipEditReminderTime.isVisible = true
        binding.chipEditReminderTime.text = context.getNotificationTime(startTimeInMillis)
        setActionText()
        this.periodType = periodType
        if (periodType > PeriodType.ONE_TIME) {
            binding.chipEditReminderRepeat.isVisible = true
            binding.chipEditReminderRepeat.text = getRepeatString()
        }
    }

    override fun onAlarmCancelled() {
        restoreKeyboard()
    }

    private fun restoreKeyboard() {
        if (hasKeyboard) {
            currentFocus?.run {
                showSoftKeyboard()
                if (this is EditText) setSelection(text.length)
            }
        }
    }

    private fun getRepeatString(): String {
        return getString(
            R.string.edit_reminder_repeat,
            getString(PeriodType.getPeriodTypeStringResId(periodType)).toLowerCase(Locale.getDefault())
        )
    }

    private fun setActionText() {
        val textResId =
            if (startTimeInMillis > System.currentTimeMillis()) R.string.edit_reminder_action_save
            else R.string.edit_reminder_action_notify
        binding.btnEditReminderNotify.setText(textResId)
    }

}
