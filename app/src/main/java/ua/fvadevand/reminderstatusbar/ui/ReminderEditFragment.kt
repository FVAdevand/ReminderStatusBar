package ua.fvadevand.reminderstatusbar.ui

import android.animation.ValueAnimator
import android.graphics.Rect
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
import ua.fvadevand.reminderstatusbar.utils.IconUtils
import ua.fvadevand.reminderstatusbar.utils.ReminderDateUtils
import java.util.Locale

class ReminderEditFragment : BottomSheetDialogFragment(), View.OnClickListener, OnAlarmSetListener, IconsDialog.OnIconClickListener {

    private lateinit var titleView: EditText
    private lateinit var textView: EditText
    private lateinit var iconBtn: ImageButton
    private lateinit var notifyBtn: Button
    private lateinit var repeatChip: Chip
    private lateinit var startTimeChip: Chip
    private lateinit var viewModel: RemindersViewModel
    private lateinit var currentReminderLive: LiveData<Reminder>
    private var startTimeInMillis = System.currentTimeMillis()
    private var editMode = false
    private var iconResId = R.drawable.ic_notif_edit
    @PeriodTypes
    private var periodType = PeriodType.ONE_TIME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(RemindersViewModel::class.java)
        editMode = viewModel.currentReminderId != Const.NEW_REMINDER_ID
        if (editMode) {
            currentReminderLive = viewModel.getLiveCurrentReminder()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_reminder_edit, container, false)
        handleKeyboardHeight(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        if (editMode) {
            currentReminderLive.observe(viewLifecycleOwner, Observer { reminder ->
                reminder?.let {
                    currentReminderLive.removeObservers(viewLifecycleOwner)
                    fillView(it)
                }
            })
        }
    }

    private fun handleKeyboardHeight(view: View) {
        dialog?.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
                        or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        val decorView = activity?.window?.decorView
        decorView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val displayFrame = Rect()
            decorView.getWindowVisibleDisplayFrame(displayFrame)
            val height = decorView.context.resources.displayMetrics.heightPixels
            val heightDifference = height - displayFrame.bottom
            val animator = ValueAnimator.ofInt(view.paddingBottom, heightDifference)
            val duration = if (heightDifference == 0) DELAY_DOWN_DIALOG else DELAY_UP_DIALOG
            animator.addUpdateListener { view.setPadding(0, 0, 0, it.animatedValue as Int) }
            animator.duration = duration
            animator.start()
        }
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as? BottomSheetDialog
            val bottomSheet = bottomSheetDialog?.findViewById<ViewGroup>(R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initView(view: View) {
        titleView = view.findViewById(R.id.et_edit_reminder_title)
        titleView.requestFocus()
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
            it.visibility = View.GONE
            periodType = PeriodType.ONE_TIME
        }
        startTimeChip = view.findViewById(R.id.chip_edit_reminder_time)
        startTimeChip.setOnCloseIconClickListener {
            it.visibility = View.GONE
            startTimeInMillis = System.currentTimeMillis()
            notifyBtn.setText(R.string.edit_reminder_action_notify)
        }
        startTimeInMillis = System.currentTimeMillis()
    }

    private fun fillView(reminder: Reminder) {
        titleView.setText(reminder.title)
        textView.setText(reminder.text)
        iconResId = IconUtils.toResId(context!!, reminder.iconName)
        iconBtn.setImageResource(iconResId)
        if (reminder.timestamp > System.currentTimeMillis()) {
            startTimeChip.visibility = View.VISIBLE
            startTimeInMillis = reminder.timestamp
            startTimeChip.text = getStartNotificationTimeString()
        } else {
            startTimeChip.visibility = View.GONE
        }
        periodType = reminder.periodType
        repeatChip.visibility = if (periodType == PeriodType.ONE_TIME) {
            View.GONE
        } else {
            repeatChip.text = getRepeatString()
            View.VISIBLE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_edit_reminder_icon -> showIconsDialog()
            R.id.btn_edit_reminder_time -> showSetAlarmDialog()
            R.id.btn_edit_reminder_notify -> saveAndNotifyReminder()
        }
    }

    private fun showSetAlarmDialog() {
        AlarmSetDialog.newInstance(startTimeInMillis, periodType).show(childFragmentManager, AlarmSetDialog.TAG)
    }

    private fun showIconsDialog() {
        IconsDialog().show(childFragmentManager, IconsDialog.TAG)
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
                IconUtils.toResName(context!!, iconResId),
                startTimeInMillis,
                status,
                periodType
        )
        if (editMode) {
            reminder.id = viewModel.currentReminderId
        }
        viewModel.addReminder(reminder)
        editMode = false
        dismiss()
    }

    override fun onAlarmSet(alarmTimeInMillis: Long, @PeriodTypes periodType: Int) {
        startTimeInMillis = alarmTimeInMillis
        startTimeChip.visibility = View.VISIBLE
        startTimeChip.text = getStartNotificationTimeString()
        if (startTimeInMillis > System.currentTimeMillis()) {
            notifyBtn.setText(R.string.edit_reminder_action_save)
        } else {
            notifyBtn.setText(R.string.edit_reminder_action_notify)
        }
        this.periodType = periodType
        if (periodType > PeriodType.ONE_TIME) {
            repeatChip.visibility = View.VISIBLE
            repeatChip.text = getRepeatString()
        }
    }

    private fun getRepeatString(): String {
        return getString(
                R.string.edit_reminder_repeat,
                getString(PeriodType.getPeriodTypeStringResId(periodType)).toLowerCase(Locale.getDefault()))
    }

    private fun getStartNotificationTimeString(): String {
        return getString(R.string.edit_reminder_notification_time, ReminderDateUtils.getNotificationTime(context!!, startTimeInMillis))
    }

    override fun onIconClick(iconId: Int) {
        iconResId = iconId
        iconBtn.setImageResource(iconResId)
    }

    companion object {
        const val TAG = "ReminderEditFragment"
        private const val DELAY_UP_DIALOG = 100L
        private const val DELAY_DOWN_DIALOG = 50L
    }
}
