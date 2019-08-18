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
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.dialogs.AlarmSetDialog
import ua.fvadevand.reminderstatusbar.dialogs.AlarmSetDialog.OnAlarmSetListener
import ua.fvadevand.reminderstatusbar.dialogs.IconsDialog
import ua.fvadevand.reminderstatusbar.utils.ReminderDateUtils
import java.util.Calendar

class ReminderEditFragment : BottomSheetDialogFragment(), View.OnClickListener, OnAlarmSetListener, IconsDialog.OnIconClickListener {

    private lateinit var titleView: EditText
    private lateinit var textView: EditText
    private lateinit var iconBtn: ImageButton
    private lateinit var notifyBtn: Button
    private lateinit var timeView: TextView
    private lateinit var delayNotificationView: CheckBox
    private lateinit var viewModel: RemindersViewModel
    private lateinit var currentReminderLive: LiveData<Reminder>
    private lateinit var calendar: Calendar
    private var editMode = false
    private var iconResId = 0

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
                    currentReminderLive.removeObservers(this@ReminderEditFragment)
                    fillView(it)
                }
            })
        } else {
            setVisibilityTimeView(false)
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
        iconResId = R.drawable.ic_notif_edit
        iconBtn.setImageResource(R.drawable.ic_grid)
        view.findViewById<View>(R.id.btn_edit_reminder_time).setOnClickListener(this)
        notifyBtn = view.findViewById(R.id.btn_edit_reminder_notify)
        notifyBtn.setOnClickListener(this)
        timeView = view.findViewById(R.id.tv_edit_reminder_time)
        delayNotificationView = view.findViewById(R.id.cb_edit_reminder_delay_notification)
        delayNotificationView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                notifyBtn.setText(R.string.edit_reminder_action_save)
            } else {
                notifyBtn.setText(R.string.edit_reminder_action_notify)
            }
        }
        calendar = Calendar.getInstance()
    }

    private fun fillView(reminder: Reminder) {
        titleView.setText(reminder.title)
        textView.setText(reminder.text)
        iconResId = reminder.iconResId
        iconBtn.setImageResource(iconResId)
        if (reminder.timestamp > System.currentTimeMillis()) {
            setVisibilityTimeView(true)
            calendar.timeInMillis = reminder.timestamp
            timeView.text = getNotificationTimeString(calendar)
        } else {
            setVisibilityTimeView(false)
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
        AlarmSetDialog.newInstance(calendar.timeInMillis).show(childFragmentManager, AlarmSetDialog.TAG)
    }

    private fun showIconsDialog() {
        IconsDialog().show(childFragmentManager, IconsDialog.TAG)
    }

    private fun saveAndNotifyReminder() {
        val title = titleView.text.toString().trim()
        if (TextUtils.isEmpty(title)) {
            titleView.error = getString(R.string.reminder_edit_error_empty_title)
            return
        }
        val text = textView.text.toString().trim()
        val timeInMillis = if (delayNotificationView.isChecked) {
            calendar.timeInMillis
        } else {
            System.currentTimeMillis()
        }
        val reminder = Reminder(
                title,
                text,
                iconResId,
                timeInMillis,
                notify = true
        )
        if (editMode) {
            reminder.id = viewModel.currentReminderId
        }
        viewModel.addReminder(reminder)
        editMode = false
        dismiss()
    }

    override fun onAlarmSet(alarmTimeInMillis: Long) {
        calendar.timeInMillis = alarmTimeInMillis
        setVisibilityTimeView(true)
        timeView.text = getNotificationTimeString(calendar)
        notifyBtn.setText(R.string.edit_reminder_action_save)
    }

    private fun getNotificationTimeString(calendar: Calendar): String {
        return getString(R.string.edit_reminder_notification_time, ReminderDateUtils.getNotificationTime(context!!, calendar))
    }

    private fun setVisibilityTimeView(isVisible: Boolean) {
        delayNotificationView.isChecked = isVisible
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        delayNotificationView.visibility = visibility
        timeView.visibility = visibility
    }

    override fun onIconClick(iconId: Int) {
        iconResId = iconId
        iconBtn.setImageResource(iconResId)
    }

    companion object {
        const val TAG = "ReminderEditFragment"
        private const val DELAY_UP_DIALOG = 200L
        private const val DELAY_DOWN_DIALOG = 50L
    }
}
