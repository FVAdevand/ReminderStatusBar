package ua.fvadevand.reminderstatusbar.ui

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.data.models.Reminder
import ua.fvadevand.reminderstatusbar.dialogs.AlarmSetDialog
import ua.fvadevand.reminderstatusbar.dialogs.AlarmSetDialog.OnAlarmSetListener
import ua.fvadevand.reminderstatusbar.dialogs.IconsDialog
import ua.fvadevand.reminderstatusbar.listeners.OnFabVisibilityChangeListener
import ua.fvadevand.reminderstatusbar.utilities.IconUtils
import ua.fvadevand.reminderstatusbar.utilities.ReminderDateUtils
import java.util.Calendar

class ReminderEditFragment : Fragment(), View.OnClickListener, OnAlarmSetListener, IconsDialog.OnIconClickListener {

    private lateinit var titleView: EditText
    private lateinit var textView: EditText
    private lateinit var iconBtn: ImageButton
    private lateinit var notifyBtn: Button
    private lateinit var timeView: TextView
    private lateinit var delayNotificationView: CheckBox
    private lateinit var viewModel: RemindersViewModel
    private lateinit var currentReminderLive: LiveData<Reminder>
    private lateinit var calendar: Calendar
    private var onFabVisibilityChangeListener: OnFabVisibilityChangeListener? = null
    private var editMode: Boolean = false
    private var iconResId: Int = 0
    private var currentReminderId: Long = Const.NEW_REMINDER_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { currentReminderId = it.getLong(ARG_REMINDER_ID) }
        editMode = currentReminderId != Const.NEW_REMINDER_ID
        viewModel = ViewModelProviders.of(activity!!).get(RemindersViewModel::class.java)
        if (editMode) {
            currentReminderLive = viewModel.getReminderById(currentReminderId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminder_edit, container, false)
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

    private fun initView(view: View) {
        titleView = view.findViewById(R.id.et_edit_reminder_title)
        titleView.requestFocus()
        textView = view.findViewById(R.id.et_edit_reminder_text)
        iconBtn = view.findViewById(R.id.btn_edit_reminder_icon)
        iconBtn.setOnClickListener(this)
        iconResId = R.drawable.ic_notif_edit
        iconBtn.setImageResource(iconResId)
        view.findViewById<View>(R.id.btn_edit_reminder_time).setOnClickListener(this)
        notifyBtn = view.findViewById(R.id.btn_edit_reminder_notify)
        notifyBtn.setOnClickListener(this)
        timeView = view.findViewById(R.id.tv_edit_reminder_time)
        delayNotificationView = view.findViewById(R.id.cb_edit_reminder_delay_notification)
        calendar = Calendar.getInstance()
    }

    private fun fillView(reminder: Reminder) {
        titleView.setText(reminder.title)
        textView.setText(reminder.text)
        context?.let {
            iconResId = IconUtils.getIconResId(it, reminder.iconName)
            iconBtn.setImageResource(iconResId)
        }
        if (reminder.timestamp > System.currentTimeMillis()) {
            setVisibilityTimeView(true)
            calendar.timeInMillis = reminder.timestamp
            timeView.text = getNotificationTimeString(calendar)
        } else {
            setVisibilityTimeView(false)
        }
    }

    private fun clearView() {
        titleView.text.clear()
        textView.text.clear()
        titleView.requestFocus()
        setVisibilityTimeView(false)
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
        val timeInMillis: Long = if (delayNotificationView.isChecked) {
            calendar.timeInMillis
        } else {
            System.currentTimeMillis()
        }
        val reminder = Reminder(
                title,
                text,
                IconUtils.getIconName(context!!, iconResId),
                timeInMillis
        )
        if (editMode) {
            reminder.id = currentReminderId
        }
        viewModel.addReminder(reminder)
        editMode = false
        clearView()
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
        val visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        delayNotificationView.visibility = visibility
        timeView.visibility = visibility
    }

    override fun onIconClick(iconId: Int) {
        iconResId = iconId
        iconBtn.setImageResource(iconResId)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onFabVisibilityChangeListener = context as OnFabVisibilityChangeListener?
    }

    override fun onStart() {
        super.onStart()
        onFabVisibilityChangeListener?.onFabVisibilityChange(false)
    }

    override fun onDetach() {
        onFabVisibilityChangeListener = null
        super.onDetach()
    }

    companion object {
        const val TAG = "ReminderEditFragment"
        private const val ARG_REMINDER_ID = "reminder_id"

        fun newInstance(reminderId: Long): ReminderEditFragment {
            val fragment = ReminderEditFragment()
            val args = Bundle()
            args.putLong(ARG_REMINDER_ID, reminderId)
            fragment.arguments = args
            return fragment
        }
    }
}
