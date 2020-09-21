package com.code.gitprofiler.UI.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.code.gitprofiler.Adapters.RepositoryAdapter;
import com.code.gitprofiler.Helper.Utility;
import com.code.gitprofiler.Models.RepositoryResponse;
import com.code.gitprofiler.Models.User;
import com.code.gitprofiler.R;
import com.code.gitprofiler.UI.Activities.HomeActivity;
import com.code.gitprofiler.WebHttp.APIClient;
import com.code.gitprofiler.WebHttp.APIInterfaces;

import org.json.JSONArray;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowDetailsFragment extends Fragment implements View.OnClickListener {
    public static final String CLASSTAG = ShowDetailsFragment.class.getSimpleName();
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.frag_details, container, false);
            initView(view);
        } catch (Exception e) {
            Utility.showDialog(getString(R.string.app_name), "Something went wrong", null, null);
        }
        return view;
    }

    private void initView(View view) {
        ImageView imageView = view.findViewById(R.id.user_image);
        try {

            ((TextView) view.findViewById(R.id.user_name)).setText(user.getName());
            ((TextView) view.findViewById(R.id.user_score)).setText("Score : " + Utility.getFormattedScore(user.getScore()));
            TextView profile = (TextView) view.findViewById(R.id.user_profile);
            profile.setText(user.getProfileURL());
            profile.setOnClickListener(this);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.github)
                    .error(R.mipmap.ic_launcher_round);
            Glide.with(getActivity()).load(user.getImageURL()).apply(options).into(imageView);

            getUserRepositories();
        } catch (Exception e) {
            //TODO:: Log Exception and show Error dialog
        }
    }

    /**
     * API to get user repositories
     */
    private void getUserRepositories() {
        try {
            ((HomeActivity) getActivity()).showProgress();
            APIInterfaces apiInterfaces = APIClient.getClient().create(APIInterfaces.class);

            Call<ResponseBody> call = apiInterfaces.getUserRepositories(user.getName());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response != null && response.body() != null) {
                            RepositoryResponse userResponse = new RepositoryResponse(new JSONArray(response.body().string()));
                            if (userResponse != null && userResponse.getRepositories() != null && userResponse.getRepositories().size() > 0) {
                                showRepositories(userResponse);
                            } else {
                                //showEmptyUsers();
                            }
                        } else {
                            //showEmptyUsers();
                        }
                    } catch (Exception e) {
                        handleError();
                    }
                    ((HomeActivity) getActivity()).hideProgress();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ((HomeActivity) getActivity()).hideProgress();
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
                getUserRepositories(); // Retry
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing
            }
        });
    }

    private void showRepositories(RepositoryResponse userResponse) {
        RecyclerView recyclerView = getView().findViewById(R.id.repo_list);
        RepositoryAdapter adapter = new RepositoryAdapter(getActivity(), userResponse.getRepositories());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onClick(View v) {
        Utility.hideKeyboard(getActivity());
        if (v.getId() == R.id.user_profile) {
            onProfileURLClick();
        }
    }

    /**
     * Load profile in browser
     */
    private void onProfileURLClick() {
        TextView textView = getView().findViewById(R.id.user_profile);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(textView.getText().toString().trim()));
        startActivity(browserIntent);
    }
}
