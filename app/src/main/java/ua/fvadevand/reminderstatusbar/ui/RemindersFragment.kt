package ua.fvadevand.reminderstatusbar.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.onEach
import ua.fvadevand.lifecycledelegates.fragmentProperty
import ua.fvadevand.lifecycledelegates.observeInLifecycle
import ua.fvadevand.reminderstatusbar.R
import ua.fvadevand.reminderstatusbar.adapters.ReminderAdapter
import ua.fvadevand.reminderstatusbar.adapters.ReminderHeaderAdapter
import ua.fvadevand.reminderstatusbar.databinding.FragmentRemindersBinding
import ua.fvadevand.reminderstatusbar.decorators.DividerItemDecoration
import ua.fvadevand.reminderstatusbar.decorators.SwipeToEditOrDeleteCallback
import ua.fvadevand.reminderstatusbar.listeners.OnReminderInteractListener

class RemindersFragment : Fragment(R.layout.fragment_reminders) {

    private val fragmentProperty by fragmentProperty()
    private val viewModel: RemindersViewModel by activityViewModels()
    private val binding by fragmentProperty.bindingByView(
        FragmentRemindersBinding::bind
    )
    private var reminderAdapter: ReminderAdapter? by fragmentProperty.delegateViewLifecycle()
    private var placeholder: View? = null
    private var onReminderInteractListener: OnReminderInteractListener? by fragmentProperty.delegateFragmentLifecycle()

    companion object {
        const val TAG = "RemindersFragment"

        fun newInstance() = RemindersFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onReminderInteractListener = context as OnReminderInteractListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.getRemindersSortedFlow()
            .onEach { reminders ->
                if (reminders.isEmpty()) {
                    (placeholder ?: inflatePlaceholder())?.isVisible = true
                } else {
                    placeholder?.isVisible = false
                }
                reminderAdapter?.submitReminders(reminders)
            }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.reminderList.layoutManager = layoutManager
        val adapter = ReminderAdapter(onReminderInteractListener)
        val concatAdapter = ConcatAdapter(ReminderHeaderAdapter(R.string.reminders_title), adapter)
        binding.reminderList.adapter = concatAdapter
        binding.reminderList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                layoutManager.orientation,
                requireContext().resources.getDimensionPixelOffset(R.dimen.item_reminder_divider_offset_start),
                requireContext().resources.getDimensionPixelOffset(R.dimen.item_reminder_divider_offset_end),
                drawInFirstItem = false,
                drawInLastItem = false
            )
        )
        ItemTouchHelper(SwipeToEditOrDeleteCallback(requireContext(), adapter))
            .attachToRecyclerView(binding.reminderList)

        reminderAdapter = adapter
    }

    private fun inflatePlaceholder(): View? {
        placeholder = binding.viewStubReminders.inflate()
        return placeholder
    }

}
