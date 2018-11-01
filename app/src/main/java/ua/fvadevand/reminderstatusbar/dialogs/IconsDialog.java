package ua.fvadevand.reminderstatusbar.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import ua.fvadevand.reminderstatusbar.R;
import ua.fvadevand.reminderstatusbar.adapters.IconAdapter;
import ua.fvadevand.reminderstatusbar.adapters.IconAdapter.OnIconClickListener;
import ua.fvadevand.reminderstatusbar.utilities.IconUtils;

public class IconsDialog extends DialogFragment {

    public static final String TAG = "IconsDialog";

    private static final int SPAN_COUNT = 4;
    private OnIconClickListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnIconClickListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment() + " must implement OnIconClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = getContext();
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_icons, null);
        RecyclerView iconsView = rootView.findViewById(R.id.icons_grid);
        iconsView.setLayoutManager(new GridLayoutManager(context, SPAN_COUNT));
        IconAdapter adapter = new IconAdapter(IconUtils.getIconsIds(), iconId -> {
            if (mListener != null) {
                mListener.onIconClick(iconId);
            }
            dismiss();
        });
        iconsView.setAdapter(adapter);
        return new AlertDialog.Builder(context)
                .setView(rootView)
                .create();
    }
}
