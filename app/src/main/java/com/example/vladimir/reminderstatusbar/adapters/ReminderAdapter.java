package com.example.vladimir.reminderstatusbar.adapters;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vladimir.reminderstatusbar.R;
import com.example.vladimir.reminderstatusbar.data.models.Reminder;

import java.util.List;
import java.util.Objects;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> mReminderList;

    public ReminderAdapter(List<Reminder> reminderList) {
        mReminderList = reminderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_list_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        holder.bind(mReminderList.get(position));
    }

    @Override
    public int getItemCount() {
        return mReminderList.size();
    }

    public void setReminderList(List<Reminder> newList) {
        if (mReminderList != null && newList != null) {
            ReminderDiffUtilCallback diffUtilCallback = new ReminderDiffUtilCallback(mReminderList, newList);
            DiffUtil.DiffResult diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback);

            mReminderList.clear();
            mReminderList.addAll(newList);
            diffUtilResult.dispatchUpdatesTo(this);
        }
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {

        ImageView mIconView;
        TextView mTitleView;
        TextView mTextView;
        TextView mDateView;

        ReminderViewHolder(View itemView) {
            super(itemView);

            mIconView = itemView.findViewById(R.id.iv_item_icon);
            mTitleView = itemView.findViewById(R.id.tv_item_title);
            mTextView = itemView.findViewById(R.id.tv_item_text);
            mDateView = itemView.findViewById(R.id.tv_item_date);
        }

        void bind(Reminder reminder) {
            mIconView.setImageResource(reminder.getIconId());
            mTitleView.setText(reminder.getTitle());
            mTextView.setText(reminder.getText());
        }
    }

    class ReminderDiffUtilCallback extends DiffUtil.Callback {

        private List<Reminder> mOldList;
        private List<Reminder> mNewList;

        ReminderDiffUtilCallback(List<Reminder> oldList, List<Reminder> newList) {
            mOldList = oldList;
            mNewList = newList;
        }

        @Override
        public int getOldListSize() {
            return mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldList.get(oldItemPosition).getId() == mNewList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(mOldList.get(oldItemPosition), mNewList.get(newItemPosition));
        }
    }
}
