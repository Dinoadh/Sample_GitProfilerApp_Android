package com.code.gitprofiler.UI.Activities;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.code.gitprofiler.R;
import com.code.gitprofiler.UI.Fragments.HomeFragment;
import com.code.gitprofiler.UI.Fragments.ProgressDialogFragment;
import com.code.gitprofiler.UI.Fragments.SortDialogFragment;

public class HomeActivity extends BaseActivity {

    private DialogFragment dialogFragment;

    private SortDialogFragment.SortingMenu selectedSortOption = SortDialogFragment.SortingMenu.SCORE_DESC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), HomeFragment.CLASSTAG, false, R.id.fragment_container);
        }
    }

    public void showProgress() {
        dialogFragment = new ProgressDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), ProgressDialogFragment.CLASSTAG);
    }

    public void hideProgress() {
        dialogFragment.dismiss();
    }

    @Override
    public void loadFragment(Fragment fragment, String tag, boolean addToBackStack, int containerId) {
        super.loadFragment(fragment, tag, addToBackStack, containerId);
    }

    public SortDialogFragment.SortingMenu getSelectedSortOption() {
        return selectedSortOption;
    }

    public void setSelectedSortOption(SortDialogFragment.SortingMenu selectedSortOption) {
        this.selectedSortOption = selectedSortOption;
    }
}

