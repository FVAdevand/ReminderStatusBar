package ua.fvadevand.reminderstatusbar.ui

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.PeriodType
import ua.fvadevand.reminderstatusbar.data.models.PeriodType.PeriodTypes
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.data.models.ReminderStatus
import ua.fvadevand.reminderstatusbar.ui.dialogs.AlarmSetDialog
import ua.fvadevand.reminderstatusbar.ui.dialogs.AlarmSetDialog.OnAlarmSetListener
import ua.fvadevand.reminderstatusbar.ui.dialogs.IconsDialog
import ua.fvadevand.reminderstatusbar.utils.getNotificationTime
import ua.fvadevand.reminderstatusbar.utils.setImageResourceName
import ua.fvadevand.reminderstatusbar.utils.showKeyboard
import ua.fvadevand.reminderstatusbar.utils.toResId
import ua.fvadevand.reminderstatusbar.utils.toResName
import java.util.Locale

class ReminderEditFragment : BaseBottomSheetDialogFragment(), View.OnClickListener,
    OnAlarmSetListener,
    IconsDialog.OnIconClickListener {

    private lateinit var titleView: EditText
    private lateinit var textView: EditText
    private lateinit var iconBtn: ImageButton
    private lateinit var notifyBtn: Button
    private lateinit var repeatChip: Chip
    private lateinit var startTimeChip: Chip
    private lateinit var viewModel: RemindersViewModel
    private var startTimeInMillis = System.currentTimeMillis()
    private var editMode = false
    private var iconResId = R.drawable.ic_notif_edit
    @PeriodTypes
    private var periodType = PeriodType.ONE_TIME
    private var navBarHeight = -1
    private var behavior: BottomSheetBehavior<View>? = null
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        return inflater.inflate(R.layout.fragment_reminder_edit, container, false)
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
        viewModel = ViewModelProvider(activity!!).get(RemindersViewModel::class.java)
        editMode = currentReminderId != Const.NEW_REMINDER_ID
        initView(view)
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
                    insets.replaceSystemWindowInsets(
                        insets.systemWindowInsetLeft,
                        insets.systemWindowInsetTop,
                        insets.systemWindowInsetRight,
                        navBarHeight
                    )
                )
            }
        }
    }

    override fun onIconClick(iconId: Int) {
        iconResId = iconId
        iconBtn.setImageResource(iconResId)
    }

    override fun onDestroyView() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        super.onDestroyView()
    }

    private fun initView(view: View) {
        titleView = view.findViewById(R.id.et_edit_reminder_title)
        titleView.showKeyboard()
        textView = view.findViewById(R.id.et_edit_reminder_text)
        textView.setHorizontallyScrolling(false)
        textView.maxLines = 20
        textView.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (TextUtils.isEmpty(titleView.text)) {
                        titleView.requestFocus()
                    } else {
                        saveAndNotifyReminder()
                    }
                    true
                }
                else -> false
            }
        }
        iconBtn = view.findViewById(R.id.btn_edit_reminder_icon)
        iconBtn.setOnClickListener(this)
        iconBtn.setImageResource(R.drawable.ic_grid)
        view.findViewById<View>(R.id.btn_edit_reminder_time).setOnClickListener(this)
        notifyBtn = view.findViewById(R.id.btn_edit_reminder_notify)
        notifyBtn.setOnClickListener(this)
        repeatChip = view.findViewById(R.id.chip_edit_reminder_repeat)
        repeatChip.setOnCloseIconClickListener {
            it.isVisible = false
            periodType = PeriodType.ONE_TIME
        }
        startTimeChip = view.findViewById(R.id.chip_edit_reminder_time)
        startTimeChip.setOnCloseIconClickListener {
            it.isVisible = false
            startTimeInMillis = System.currentTimeMillis()
            setActionText()
        }
        startTimeInMillis = System.currentTimeMillis()
    }

    private fun populateData(reminder: Reminder) {
        titleView.setText(reminder.title)
        textView.setText(reminder.text)
        iconBtn.run {
            iconResId = context.toResId(reminder.iconName)
            setImageResource(iconResId)
        }
        iconBtn.setImageResourceName(reminder.iconName)
        val now = System.currentTimeMillis()
        startTimeInMillis =
            if (reminder.status == ReminderStatus.PAUSED && now > reminder.timestamp) {
                PeriodType.getNextAlarmTimeByType(reminder.periodType, reminder.timestamp)
            } else {
                reminder.timestamp
            }
        if (startTimeInMillis > now) {
            startTimeChip.isVisible = true
            startTimeChip.text = context.getNotificationTime(startTimeInMillis)
        } else {
            startTimeChip.isVisible = false
        }
        periodType = reminder.periodType
        repeatChip.isVisible = if (periodType == PeriodType.ONE_TIME) {
            false
        } else {
            repeatChip.text = getRepeatString()
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
        AlarmSetDialog.newInstance(startTimeInMillis, periodType)
            .show(childFragmentManager, AlarmSetDialog.TAG)
    }

    private fun showIconsDialog() {
        IconsDialog.newInstance().show(childFragmentManager, IconsDialog.TAG)
    }

    private fun saveAndNotifyReminder() {
        val title = titleView.text.toString().trim()
        if (TextUtils.isEmpty(title)) {
            titleView.error = getString(R.string.edit_reminder_error_empty_title)
            return
        }
        val text = textView.text.toString().trim()
        val status = when {
            periodType > PeriodType.ONE_TIME -> ReminderStatus.PERIODIC
            startTimeInMillis > System.currentTimeMillis() -> ReminderStatus.DELAYED
            else -> ReminderStatus.NOTIFYING
        }
        val reminder = Reminder(
            title,
            text,
            context!!.toResName(iconResId),
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
        startTimeInMillis = alarmTimeInMillis
        startTimeChip.isVisible = true
        startTimeChip.text = context.getNotificationTime(startTimeInMillis)
        setActionText()
        this.periodType = periodType
        if (periodType > PeriodType.ONE_TIME) {
            repeatChip.isVisible = true
            repeatChip.text = getRepeatString()
        }
    }

    private fun getRepeatString(): String {
        return getString(
            R.string.edit_reminder_repeat,
            getString(PeriodType.getPeriodTypeStringResId(periodType)).toLowerCase(Locale.getDefault())
        )
    }

    private fun setActionText() {
        if (startTimeInMillis > System.currentTimeMillis()) {
            notifyBtn.setText(R.string.edit_reminder_action_save)
        } else {
            notifyBtn.setText(R.string.edit_reminder_action_notify)
        }
    }

}
