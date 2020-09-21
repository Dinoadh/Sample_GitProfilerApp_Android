package com.code.gitprofiler.UI.Activities;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.code.gitprofiler.Helper.Utility;
import com.code.gitprofiler.R;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Load given fragment in the provided container
     *
     * @param fragment
     * @param tag
     * @param addToBackStack
     * @param containerId
     */
    protected void loadFragment(Fragment fragment, String tag, boolean addToBackStack, int containerId) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            if (addToBackStack) {
                transaction.add(containerId, fragment, tag);
                transaction.addToBackStack(null);
            } else {
                transaction.add(containerId, fragment);
            }
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            Utility.showDialog(getString(R.string.app_name), "Something went wrong", null, null);
        }
    }
}
