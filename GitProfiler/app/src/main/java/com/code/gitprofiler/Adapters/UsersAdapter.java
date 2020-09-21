package com.code.gitprofiler.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.code.gitprofiler.Helper.Utility;
import com.code.gitprofiler.Models.User;
import com.code.gitprofiler.R;
import com.code.gitprofiler.UI.Fragments.SortDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter {

    private final int USER_VIEW = 1001;
    private final int PROGRESS_VIEW = 1002;

    private List<User> usersList;
    private Context context;
    private IUserConsole listener;
    private boolean isLoadingComplete;//flag to keep track if all the users are loaded

    public UsersAdapter(Context context, List<User> usersList) {
        this.context = context;

        if (this.usersList == null) this.usersList = new ArrayList<>();

        this.usersList.clear();

        if (usersList != null) {
            this.usersList.addAll(usersList);
        }
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == USER_VIEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.user_grid, parent, false);
            return new UserViewHolder(view);
        }
        if (viewType == PROGRESS_VIEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.progress_view, parent, false);
            return new ProgressHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof UserViewHolder) {
            try {
                final User user = usersList.get(position);

                ((UserViewHolder) holder).userName.setText(user.getName());
                ((UserViewHolder) holder).userScore.setText("Score : " + Utility.getFormattedScore(user.getScore()));
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.github)
                        .error(R.mipmap.ic_launcher_round);
                //Glide library to load images
                Glide.with(context).load(user.getImageURL()).apply(options).into(((UserViewHolder) holder).userImage);
                ((UserViewHolder) holder).user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemSelected(user);
                    }
                });
            } catch (Exception e) {
                Utility.showDialog(context.getString(R.string.app_name), "Something went wrong", null, null);
            }
        }

        if (holder instanceof ProgressHolder) {
            ((ProgressHolder) holder).progressBar.setVisibility(View.VISIBLE);
            listener.loadNextUsers();
        }
    }

    @Override
    public int getItemCount() {
        try {
            //kept size 29 since git hub returns one page as 30 items if items are leass than 30 so footer will not be visible
            if (usersList.size() > 29) {
                if (isLoadingComplete) {
                    return usersList.size();
                }
                return usersList.size() + 1;
            } else {

                return usersList.size();
            }
        } catch (Exception e) {
            //TODO:: Log Message
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (position == usersList.size()) {
                return PROGRESS_VIEW;
            } else {
                return USER_VIEW;
            }
        } catch (Exception e) {
            //TODO:: Log Message
        }
        return -1;
    }

    public IUserConsole getListener() {
        return listener;
    }

    public void setListener(IUserConsole listener) {
        this.listener = listener;
    }

    /**
     * this method will be called on subsequent load next methods so we are passing sort criteria to this function as well
     *
     * @param usersList
     * @param menu
     */
    public void setUsersList(List<User> usersList, final SortDialogFragment.SortingMenu menu) {
        if (this.usersList == null) {
            this.usersList = new ArrayList<>();
        }
        this.usersList.addAll(usersList);

        Collections.sort(this.usersList, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                switch (menu) {
                    case SCORE_DESC:
                        return lhs.getScore() < rhs.getScore() ? -1 : (lhs.getScore() > rhs.getScore()) ? 1 : 0;
                    case SCORE_ASC:
                        return lhs.getScore() > rhs.getScore() ? -1 : (lhs.getScore() < rhs.getScore()) ? 1 : 0;
                    case NAME_DESC:
                        return lhs.getName().compareTo(rhs.getName());
                    case NAME_ASC:
                        return rhs.getName().compareTo(lhs.getName());
                }
                return 0;
            }
        });

        notifyDataSetChanged();
    }

    /**
     * Method will sort the list as per sort mode is selected
     *
     * @param menu
     */
    public void sortList(final SortDialogFragment.SortingMenu menu) {
        Collections.sort(usersList, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                switch (menu) {
                    case SCORE_DESC:
                        return lhs.getScore() > rhs.getScore() ? -1 : (lhs.getScore() < rhs.getScore()) ? 1 : 0;
                    case SCORE_ASC:
                        return lhs.getScore() < rhs.getScore() ? -1 : (lhs.getScore() > rhs.getScore()) ? 1 : 0;
                    case NAME_DESC:
                        return rhs.getName().compareTo(lhs.getName());
                    case NAME_ASC:
                        return lhs.getName().compareTo(rhs.getName());
                }
                return 0;
            }
        });

        notifyDataSetChanged();
    }

    public boolean isLoadingComplete() {
        return isLoadingComplete;
    }

    public void setLoadingComplete(boolean loadingComplete) {
        isLoadingComplete = loadingComplete;
    }

    public interface IUserConsole {
        /**
         * This method will be invoked when user taps on show details
         *
         * @param user
         */
        void onItemSelected(User user);

        /**
         * Once progress indicator is visible then this method will be called
         */
        void loadNextUsers();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private CardView user;
        private ImageView userImage;
        private TextView userName;
        private TextView userScore;

        public UserViewHolder(View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.name);
            userScore = itemView.findViewById(R.id.score);
            user = itemView.findViewById(R.id.user);
        }
    }

    public static class ProgressHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        ProgressHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_holder);
        }
    }
}

