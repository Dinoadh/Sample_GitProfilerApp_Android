package com.code.gitprofiler.UI.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.code.gitprofiler.R;
import com.code.gitprofiler.UI.Activities.HomeActivity;

public class SortDialogFragment extends DialogFragment {
    public static final String CLASSTAG = SortDialogFragment.class.getSimpleName();
    private ISortWatcher sortingListner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sort_dialog, container, false);
        initView(view);
        return view;
    }

    /**
     * Making width of dialod to Match parent of phone
     */
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void initView(View view) {
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.sorting_options);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.score_decending:
                        ((HomeActivity) getActivity()).setSelectedSortOption(SortingMenu.SCORE_DESC);
                        sortingListner.onSortingSelected(SortingMenu.SCORE_DESC);
                        break;

                    case R.id.score_asending:
                        ((HomeActivity) getActivity()).setSelectedSortOption(SortingMenu.SCORE_ASC);
                        sortingListner.onSortingSelected(SortingMenu.SCORE_ASC);
                        break;

                    case R.id.name_asending:
                        ((HomeActivity) getActivity()).setSelectedSortOption(SortingMenu.NAME_ASC);
                        sortingListner.onSortingSelected(SortingMenu.NAME_ASC);
                        break;

                    case R.id.name_decending:
                        ((HomeActivity) getActivity()).setSelectedSortOption(SortingMenu.NAME_DESC);
                        sortingListner.onSortingSelected(SortingMenu.NAME_DESC);
                        break;
                }
            }
        });

        updateRadioButtons(view);
    }

    private void updateRadioButtons(View view) {
        final RadioButton scoreDecendingButton = (RadioButton) view.findViewById(R.id.score_decending);
        final RadioButton scoreAscendingButton = (RadioButton) view.findViewById(R.id.score_asending);
        final RadioButton nameAscendingButton = (RadioButton) view.findViewById(R.id.name_asending);
        final RadioButton nameDecendingButton = (RadioButton) view.findViewById(R.id.name_decending);

        switch (((HomeActivity) getActivity()).getSelectedSortOption()) {
            case NAME_ASC:
                nameAscendingButton.setChecked(true);
                break;

            case NAME_DESC:
                nameDecendingButton.setChecked(true);
                break;

            case SCORE_ASC:
                scoreAscendingButton.setChecked(true);
                break;

            case SCORE_DESC:
                scoreDecendingButton.setChecked(true);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public ISortWatcher getSortingListner() {
        return sortingListner;
    }

    public void setSortingListner(ISortWatcher sortingListner) {
        this.sortingListner = sortingListner;
    }

    public enum SortingMenu {
        SCORE_DESC,
        SCORE_ASC,
        NAME_DESC,
        NAME_ASC
    }

    public interface ISortWatcher {
        void onSortingSelected(SortingMenu menu);
    }
}
