package lt.dualpair.android.ui.accounts;


import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {

    private List<SocialAccountItem> items;
    private OnItemClickListener onItemClickListener;

    public AccountListAdapter(List<SocialAccountItem> items, OnItemClickListener onItemClickListener) {
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_accounts_item, parent, false);
        return new AccountViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        final SocialAccountItem item = items.get(position);
        holder.setAccount(item, onItemClickListener, getConnectedAccountsCount() < 2);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int getConnectedAccountsCount() {
        int c = 0;
        for (SocialAccountItem item : items) {
            if (item.getUserAccount() != null) {
                c++;
            }
        }
        return c;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.icon) ImageView icon;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.connect) TextView connect;
        @Bind(R.id.disconnect) TextView disconnect;

        public AccountViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setAccount(SocialAccountItem account, OnItemClickListener onItemClickListener, boolean onlyOneConnected) {
            icon.setImageResource(account.getAccountType().getIcon());
            int identifier = itemView.getResources().getIdentifier(account.getAccountType().name(), "string", itemView.getContext().getPackageName());
            String title;
            if (account.getUserAccount() == null) {
                title = itemView.getResources().getString(R.string.connect_to,
                        itemView.getResources().getString(identifier));
                connect.setVisibility(View.VISIBLE);
                connect.setOnClickListener(view -> onItemClickListener.onConnectClick(account));
                disconnect.setVisibility(View.GONE);
            } else {
                title = itemView.getResources().getString(identifier) + " (" + account.getUserAccount().getAccountId() + ")";
                this.title.setPaintFlags(this.title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                this.title.setOnClickListener(v -> onItemClickListener.onConnectedItemClick(account));
                connect.setVisibility(View.GONE);
                if (!onlyOneConnected) {
                    disconnect.setVisibility(View.VISIBLE);
                    disconnect.setOnClickListener(view -> onItemClickListener.onDisconnectClick(account));
                } else {
                    disconnect.setVisibility(View.GONE);
                }

            }
            this.title.setText(title);
        }

    }

    public interface OnItemClickListener {
        void onConnectClick(SocialAccountItem item);
        void onDisconnectClick(SocialAccountItem item);
        void onConnectedItemClick(SocialAccountItem item);
    }
}
