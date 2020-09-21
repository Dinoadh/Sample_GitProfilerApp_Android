package com.code.gitprofiler.UI.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.code.gitprofiler.Adapters.UsersAdapter;
import com.code.gitprofiler.Helper.Utility;
import com.code.gitprofiler.Models.User;
import com.code.gitprofiler.Models.UserResponse;
import com.code.gitprofiler.R;
import com.code.gitprofiler.UI.Activities.HomeActivity;
import com.code.gitprofiler.WebHttp.APIClient;
import com.code.gitprofiler.WebHttp.APIInterfaces;
import com.code.gitprofiler.enums.ContainerMode;
import com.code.gitprofiler.enums.ViewMode;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment
        implements View.OnClickListener,
        TextView.OnEditorActionListener,
        UsersAdapter.IUserConsole,
        SortDialogFragment.ISortWatcher {
    public static final String CLASSTAG = HomeFragment.class.getSimpleName();

    private LinearLayout searchView;
    private RelativeLayout homeView;
    private EditText searchText;
    private RelativeLayout welcomeView;
    private LinearLayout resultsView;
    private LinearLayout emptyResultsView;

    private int pageCounter = 0;
    private List<User> usersList;
    private UsersAdapter adapter;
    private SortDialogFragment sortingFragment;

    @Nullable
    @Override
    public View onCreateView(
            @NotNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        setRetainInstance(true);
        View view = null;
        try {
            view = inflater.inflate(R.layout.frag_home, container, false);
            initView(view);
        } catch (Exception e) {
            //TODO:: log Exception and show Error Dialog
        }
        return view;
    }

    private void initView(View view) {
        searchView = view.findViewById(R.id.search_view);
        homeView = view.findViewById(R.id.home_view);
        view.findViewById(R.id.search_button).setOnClickListener(this);
        view.findViewById(R.id.clear_query).setOnClickListener(this);
        updateView(ViewMode.HOME_VIEW);
        searchText = view.findViewById(R.id.query_text);
        searchText.setOnEditorActionListener(this);
        welcomeView = view.findViewById(R.id.welcome_message);
        resultsView = view.findViewById(R.id.result_sucess);
        emptyResultsView = view.findViewById(R.id.results_empty);
        updateContainerState(ContainerMode.WELCOME_MODE);
    }

    @Override
    public void onClick(View v) {
        Utility.hideKeyboard(getActivity());
        try {
            switch (v.getId()) {
                case R.id.search_button:
                    onSearchClick();
                    break;
                case R.id.clear_query:
                    onQueryClear();
                    break;

                case R.id.sort_by_options:
                    showSortingOptions();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            //TODO:: log Exception and show Error Dialog
        }
    }

    private boolean shouldSearchUser() {
        if (TextUtils.isEmpty(searchText.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please provide GitHub User Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Utility.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            Toast.makeText(getActivity(), "Please check your internet connenctivity", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Show sorting dialog fragment , set listener and get selected Menu
     */
    private void showSortingOptions() {
        if (sortingFragment == null) {
            sortingFragment = new SortDialogFragment();
            sortingFragment.setSortingListner(this);
        }
        sortingFragment.show(getFragmentManager(), SortDialogFragment.CLASSTAG);
    }

    /**
     * On Cancel image button click event handling
     */
    private void onQueryClear() {
        searchText.setText("");
        updateContainerState(ContainerMode.WELCOME_MODE);
    }

    private void onSearchClick() {
        updateView(ViewMode.SEARCH_VIEW);
    }

    /**
     * As per given mode in View Mode ENUM update header state.
     *
     * @param mode
     */
    private void updateView(ViewMode mode) {
        if (mode.equals(ViewMode.HOME_VIEW)) {
            homeView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
        } else {
            homeView.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method to decide what contents to show in container as per container mode ENUM
     *
     * @param containerMode
     */
    private void updateContainerState(ContainerMode containerMode) {
        if (containerMode.equals(ContainerMode.WELCOME_MODE)) {
            welcomeView.setVisibility(View.VISIBLE);
            resultsView.setVisibility(View.GONE);
            emptyResultsView.setVisibility(View.GONE);
        }

        if (containerMode.equals(ContainerMode.RESULTS_MODE)) {
            welcomeView.setVisibility(View.GONE);
            resultsView.setVisibility(View.VISIBLE);
            emptyResultsView.setVisibility(View.GONE);
        }

        if (containerMode.equals(ContainerMode.EMPTY_RESUTS_MODE)) {
            welcomeView.setVisibility(View.GONE);
            resultsView.setVisibility(View.GONE);
            emptyResultsView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Key board query search Action
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        try {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (shouldSearchUser()) {
                    pageCounter = 0;
                    usersList = null;
                    getGitHubUsers();
                    return true;
                }
            }
        } catch (Exception e) {
            // Error Handling
        }
        return false;
    }

    /**
     * API to get data from api.github.com
     */
    private void getGitHubUsers() {
        try {
            pageCounter++;
            ((HomeActivity) getActivity()).showProgress();
            APIInterfaces apiInterfaces = APIClient.getClient().create(APIInterfaces.class);

            Call<ResponseBody> call = apiInterfaces.getUsers(searchText.getText().toString().trim(), pageCounter);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    try {
                        if (response.body() != null) {
                            UserResponse userResponse = new UserResponse(new JSONObject(response.body().string()));
                            if (userResponse.getUserList() != null && userResponse.getUserList().size() > 0) {
                                showUserList(userResponse);
                            } else {
                                showEmptyUsers();
                            }
                        } else {
                            showEmptyUsers();
                        }
                    } catch (Exception e) {
                        handleError();
                    }
                    ((HomeActivity) Objects.requireNonNull(getActivity())).hideProgress();
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    ((HomeActivity) Objects.requireNonNull(getActivity())).hideProgress();
                    Toast.makeText(getActivity(), "In Error", Toast.LENGTH_SHORT).show();
                    handleError();
                }
            });
        } catch (Exception e) {
            handleError();
        }
    }

    private void handleError() {
        Utility.showDialog(getString(R.string.app_name), "Something went wrong", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getGitHubUsers(); // Retry
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing
            }
        });
    }

    /**
     * Show data in recycler view
     *
     * @param userResponse
     */
    private void showUserList(UserResponse userResponse) {
        try {
            updateContainerState(ContainerMode.RESULTS_MODE);
            TextView resultMessageView = getView().findViewById(R.id.result_for_text);
            resultMessageView.setText("Showing search results for '" + searchText.getText().toString().trim() + "'");
            ImageView sortBy = getView().findViewById(R.id.sort_by_options);
            sortBy.setOnClickListener(this);

            if (usersList == null) {
                usersList = new ArrayList<>();
                usersList.addAll(userResponse.getUserList());
                RecyclerView recyclerView = getView().findViewById(R.id.results);
                adapter = new UsersAdapter(getActivity(), usersList);
                adapter.setListener(this);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                adapter.setUsersList(
                        userResponse.getUserList(),
                        ((HomeActivity) Objects.requireNonNull(getActivity()))
                                .getSelectedSortOption());
            }
        } catch (Exception e) {
            //TODO:: log Exception and show Error Dialog
        }
    }

    /**
     * If empty result is returned by API then show appropriate message
     */
    private void showEmptyUsers() {
        updateContainerState(ContainerMode.EMPTY_RESUTS_MODE);
        ((TextView) getView()
                .findViewById(R.id.no_users_txt))
                .setText("There are no users of name : " +
                        searchText.getText().toString() +
                        " on GitHub. Please Retry"
                );
        if (adapter != null) {
            adapter.setLoadingComplete(true);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Load User Details frament in activity container
     *
     * @param user pss object to user details fragment
     */
    @Override
    public void onItemSelected(User user) {
        try {
            ShowDetailsFragment showDetailsFragment = new ShowDetailsFragment();
            showDetailsFragment.setUser(user);
            ((HomeActivity) getActivity()).loadFragment(
                    showDetailsFragment,
                    ShowDetailsFragment.CLASSTAG,
                    true, R.id.fragment_container
            );
        } catch (Exception e) {
            //TODO:: log Exception and show Error Dialog
        }
    }

    /**
     * Callback received from adapter to load next set of users
     */
    @Override
    public void loadNextUsers() {
        getGitHubUsers();
    }

    @Override
    public void onSortingSelected(SortDialogFragment.SortingMenu menu) {
        try {
            adapter.sortList(menu);
            sortingFragment.getDialog().dismiss();
        } catch (Exception e) {
            //TODO:: log Exception and show Error Dialog
        }
    }
}
