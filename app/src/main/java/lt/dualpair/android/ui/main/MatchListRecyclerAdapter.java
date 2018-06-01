package lt.dualpair.android.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.MatchForListView;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.ui.accounts.AccountType;
import lt.dualpair.android.ui.match.UserActivity;
import lt.dualpair.android.utils.SocialUtils;

public class MatchListRecyclerAdapter extends RecyclerView.Adapter<MatchListRecyclerAdapter.MatchViewHolder> {

    final private List<MatchForListView> matchList;

    public MatchListRecyclerAdapter() {
        matchList = new ArrayList<>();
    }

    public MatchListRecyclerAdapter(List<MatchForListView> matches) {
        matchList = matches;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setupFacebookButton(final Context context, View button, UserAccount account) {
        if (account == null) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialUtils.openFacebookUser(context, account.getAccountId());
                }
            });
        }
    }

    private void setupVkontakteButton(final Context context, View button, UserAccount account) {
        if (account == null) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialUtils.openVKontakteUser(context, account.getAccountId());
                }
            });
        }
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_grid_item, parent, false);
        MatchViewHolder holder = new MatchViewHolder(parent.getContext(), v);
        ButterKnife.bind(holder, v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MatchViewHolder holder, int position) {
        final MatchForListView match = matchList.get(position);
        loadPhoto(holder.context, match.getOpponentPhotos(), holder.picture);
        final User opponent = match.getOpponent();
        holder.name.setText(opponent.getName());
        setupFacebookButton(holder.context, holder.facebookButton, getAccountByType(match.getOpponentAccounts(), AccountType.FB));
        setupVkontakteButton(holder.context, holder.vkontakteButton, getAccountByType(match.getOpponentAccounts(), AccountType.VK));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.context.startActivity(UserActivity.createIntent(holder.context, match.getMatch().getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    private void loadPhoto(Context context, List<UserPhoto> photos, ImageView picture) {
        Picasso.with(context)
                .load(photos.get(0).getSourceLink())
                .error(R.drawable.image_not_found)
                .into(picture, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Log.e("MatchListPhoto", "Error while loading photo");
                    }
                });
    }

    private UserAccount getAccountByType(List<UserAccount> accounts, AccountType accountType) {
        if (accounts != null) {
            for (UserAccount account : accounts) {
                if (account.getAccountType().equals(accountType.name())) {
                    return account;
                }
            }
        }
        return null;
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {

        public Context context;

        @Bind(R.id.picture) public ImageView picture;
        @Bind(R.id.name) public TextView name;
        @Bind(R.id.facebook_button) public View facebookButton;
        @Bind(R.id.vkontakte_button) public View vkontakteButton;

        public MatchViewHolder(Context ctx, View itemView) {
            super(itemView);
            context = ctx;
        }
    }
}
