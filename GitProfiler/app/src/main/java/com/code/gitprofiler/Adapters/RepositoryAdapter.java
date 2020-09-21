package com.code.gitprofiler.Adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code.gitprofiler.Helper.Utility;
import com.code.gitprofiler.Models.Repository;
import com.code.gitprofiler.R;

import java.util.ArrayList;
import java.util.List;

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder> {

    private List<Repository> repositories;
    private Context context;

    public RepositoryAdapter(Context context, List<Repository> repositories) {
        this.context = context;

        if (this.repositories == null) this.repositories = new ArrayList<>();
        this.repositories.clear();

        if (repositories != null) {
            this.repositories.addAll(repositories);
        }
    }

    @NonNull
    @Override
    public RepositoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.repository_view, parent, false);
            return new RepositoryViewHolder(view);
        } catch (Exception e) {
            Utility.showDialog(context.getString(R.string.app_name), "Something went wrong", null, null);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RepositoryViewHolder holder, int position) {
        try {
            Repository repository = repositories.get(position);

            //Showing white and grey cells
            if (position % 2 != 0) {
                holder.repositoryContainer.setBackgroundColor(context.getResources().getColor(R.color.text_color));
                holder.repoName.setTextColor(context.getResources().getColor(R.color.white_color));
                holder.languagesUsed.setTextColor(context.getResources().getColor(R.color.white_color));
            } else {
                holder.repositoryContainer.setBackgroundColor(Color.WHITE);
                holder.repoName.setTextColor(context.getResources().getColor(R.color.text_color));
                holder.languagesUsed.setTextColor(context.getResources().getColor(R.color.text_color));
            }
            holder.repoName.setText(repository.getRepositoryName());

            if(TextUtils.isEmpty(repository.getLanguage()) || repository.getLanguage().equalsIgnoreCase("null")) {
                holder.languagesUsed.setText("NA");
            } else {
                holder.languagesUsed.setText(repository.getLanguage());
            }
        } catch (Exception e) {
            Utility.showDialog(context.getString(R.string.app_name), "Something went wrong", null, null);
        }
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }

    static class RepositoryViewHolder extends RecyclerView.ViewHolder {

        private TextView repoName;
        private TextView languagesUsed;
        private LinearLayout repositoryContainer;

        RepositoryViewHolder(View itemView) {
            super(itemView);

            repoName = itemView.findViewById(R.id.repository_name);
            languagesUsed = itemView.findViewById(R.id.languages_name);
            repositoryContainer = itemView.findViewById(R.id.repository_container);
        }
    }
}
