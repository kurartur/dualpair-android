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
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.ui.accounts.AccountType;
import lt.dualpair.android.utils.SocialUtils;

public class UserListRecyclerAdapter<T extends UserListItem> extends RecyclerView.Adapter<UserListRecyclerAdapter.ItemViewHolder> {

    private final List<T> items;
    private OnItemClickListener onItemClickListener;

    public UserListRecyclerAdapter(List<T> items, OnItemClickListener onItemClickListener) {
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_grid_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(parent.getContext(), v);
        ButterKnife.bind(holder, v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final UserListItem item = items.get(position);
        loadPhoto(holder.context, item.getPhoto().getSourceLink(), holder.picture);
        holder.name.setText(item.getName());
        setupFacebookButton(holder.context, holder.facebookButton, item.getAccountByType(AccountType.FB.name()));
        setupVkontakteButton(holder.context, holder.vkontakteButton, item.getAccountByType(AccountType.VK.name()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
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
                        Log.e(getClass().getName(), "Error while loading photo");
                    }
                });
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

    public interface OnItemClickListener {
        void onClick(UserListItem item);
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
