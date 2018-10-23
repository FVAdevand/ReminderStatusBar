package ua.fvadevand.reminderstatusbar.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vladimir.reminderstatusbar.R;
import ua.fvadevand.reminderstatusbar.adapters.ReminderAdapter;
import ua.fvadevand.reminderstatusbar.data.models.Reminder;

import java.util.List;

public class RemindersFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ReminderAdapter mAdapter;

    public RemindersFragment() {
    }

    public static RemindersFragment newInstance() {
        return new RemindersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RemindersViewModel viewModel = ViewModelProviders.of(getActivity()).get(RemindersViewModel.class);

        RecyclerView reminderListView = view.findViewById(R.id.reminder_list);
        reminderListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ReminderAdapter(viewModel.getReminderList().getValue());
        reminderListView.setAdapter(mAdapter);

        viewModel.getReminderList().observe(this, new Observer<List<Reminder>>() {
            @Override
            public void onChanged(@Nullable List<Reminder> reminders) {
                mAdapter.setReminderList(reminders);
            }
        });
    }

    //    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
