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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.History;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.ui.accounts.AccountType;
import lt.dualpair.android.ui.match.UserActivity;
import lt.dualpair.android.utils.SocialUtils;

public class HistoryListRecyclerAdapter extends RecyclerView.Adapter<HistoryListRecyclerAdapter.ItemViewHolder> {

    final private List<History> historyList;

    public HistoryListRecyclerAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setupFacebookButton(final Context context, View button, History history) {
        final UserAccount account = history.getAccountByType(AccountType.FB.name());
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

    private void setupVkontakteButton(final Context context, View button, History history) {
        final UserAccount account = history.getAccountByType(AccountType.VK.name());
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
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_grid_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(parent.getContext(), v);
        ButterKnife.bind(holder, v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final History history = historyList.get(position);
        loadPhoto(holder.context, history.getSourceLink(), holder.picture);
        holder.name.setText(history.getName());
        setupFacebookButton(holder.context, holder.facebookButton, history);
        setupVkontakteButton(holder.context, holder.vkontakteButton, history);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.context.startActivity(UserActivity.createIntent(holder.context, history.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    private void loadPhoto(Context context, String sourceLink, ImageView picture) {
        Picasso.with(context)
                .load(sourceLink)
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

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public Context context;

        @Bind(R.id.picture) public ImageView picture;
        @Bind(R.id.name) public TextView name;
        @Bind(R.id.facebook_button) public View facebookButton;
        @Bind(R.id.vkontakte_button) public View vkontakteButton;

        public ItemViewHolder(Context ctx, View itemView) {
            super(itemView);
            context = ctx;
        }
    }
}
