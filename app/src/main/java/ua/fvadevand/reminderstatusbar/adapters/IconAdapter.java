package ua.fvadevand.reminderstatusbar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ua.fvadevand.reminderstatusbar.R;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {

    private List<Integer> mIconIds;
    private OnIconClickListener mListener;

    public IconAdapter(List<Integer> iconIds, OnIconClickListener listener) {
        mIconIds = iconIds;
        mListener = listener;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.icon_list_item, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        holder.mIconView.setImageResource(mIconIds.get(position));
    }

    @Override
    public int getItemCount() {
        return mIconIds == null ? 0 : mIconIds.size();
    }

    public interface OnIconClickListener {
        void onIconClick(@DrawableRes int iconId);
    }

    class IconViewHolder extends RecyclerView.ViewHolder {

        ImageView mIconView;

        IconViewHolder(@NonNull View itemView) {
            super(itemView);
            mIconView = itemView.findViewById(R.id.iv_icon);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (mListener != null && position != RecyclerView.NO_POSITION) {
                    mListener.onIconClick(mIconIds.get(position));
                }
            });
        }
    }
}
