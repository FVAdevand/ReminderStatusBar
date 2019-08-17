package ua.fvadevand.reminderstatusbar.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.adapters.ReminderAdapter
import ua.fvadevand.reminderstatusbar.decorators.OutsideOffsetItemDecorator
import ua.fvadevand.reminderstatusbar.listeners.OnReminderClickListener

class RemindersFragment : Fragment() {

    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var viewModel: RemindersViewModel
    private var onReminderClickListener: OnReminderClickListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onReminderClickListener = context as OnReminderClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(RemindersViewModel::class.java)
        setupRecyclerView(view)
        viewModel.reminders.observe(viewLifecycleOwner, Observer { reminderAdapter.setReminders(it) })
    }

    override fun onDetach() {
        onReminderClickListener = null
        super.onDetach()
    }

    private fun setupRecyclerView(view: View) {
        val reminderListView = view.findViewById<RecyclerView>(R.id.reminder_list)
        reminderListView.layoutManager = LinearLayoutManager(context)
        reminderAdapter = ReminderAdapter(context!!) {
            viewModel.currentReminderId = it
            onReminderClickListener?.onClickReminder()
        }
        reminderListView.adapter = reminderAdapter
        val offsetTopBottom = context!!.resources.getDimensionPixelOffset(R.dimen.item_reminder_margin_top_bottom)
        reminderListView.addItemDecoration(OutsideOffsetItemDecorator(offsetTopBottom, offsetTopBottom))
    }

    companion object {

        const val TAG = "RemindersFragment"

        fun newInstance(): RemindersFragment {
            return RemindersFragment()
        }
    }
}
