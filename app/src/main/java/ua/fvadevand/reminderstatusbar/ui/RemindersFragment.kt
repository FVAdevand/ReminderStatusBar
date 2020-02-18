package ua.fvadevand.reminderstatusbar.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.adapters.ReminderAdapter
import ua.fvadevand.reminderstatusbar.data.models.ReminderItem
import ua.fvadevand.reminderstatusbar.decorators.DividerItemDecoration
import ua.fvadevand.reminderstatusbar.decorators.SwipeToEditOrDeleteCallback
import ua.fvadevand.reminderstatusbar.listeners.OnReminderInteractListener

class RemindersFragment : Fragment() {

    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var viewModel: RemindersViewModel
    private lateinit var reminderListView: RecyclerView
    private var placeholder: View? = null
    private var onReminderInteractListener: OnReminderInteractListener? = null

    companion object {
        const val TAG = "RemindersFragment"

        fun newInstance() = RemindersFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onReminderInteractListener = context as OnReminderInteractListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(activity!!).get(RemindersViewModel::class.java)
        setupRecyclerView(view)
        viewModel.remindersSortedLive.observe(viewLifecycleOwner,
            Observer { reminders ->
                reminders?.let {
                    if (it.isEmpty()) {
                        (placeholder ?: inflatePlaceholder())?.isVisible = true
                    } else {
                        placeholder?.isVisible = false
                    }
                    val remindersWithHeader = it.toMutableList().apply {
                        add(0, ReminderItem(ReminderItem.TYPE_HEADER).apply {
                            header = getString(R.string.reminders_title)
                        })
                    }
                    reminderAdapter.setReminders(remindersWithHeader)
                }
            })
    }

    override fun onDetach() {
        onReminderInteractListener = null
        super.onDetach()
    }

    private fun setupRecyclerView(view: View) {
        val context = view.context
        reminderListView = view.findViewById(R.id.reminder_list)
        val layoutManager = LinearLayoutManager(context)
        reminderListView.layoutManager = layoutManager
        reminderAdapter = ReminderAdapter(onReminderInteractListener)
        reminderListView.adapter = reminderAdapter
        reminderListView.addItemDecoration(
            DividerItemDecoration(
                context,
                layoutManager.orientation,
                context.resources.getDimensionPixelOffset(R.dimen.item_reminder_divider_offset_start),
                context.resources.getDimensionPixelOffset(R.dimen.item_reminder_divider_offset_end),
                drawInFirstItem = false,
                drawInLastItem = false
            )
        )
        ItemTouchHelper(SwipeToEditOrDeleteCallback(context, reminderAdapter))
            .attachToRecyclerView(reminderListView)
    }

    private fun inflatePlaceholder(): View? {
        val viewStub = view?.findViewById<ViewStub>(R.id.view_stub_reminders)
        placeholder = viewStub?.inflate()
        return placeholder
    }

}
