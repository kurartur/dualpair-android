package lt.dualpair.android.ui.accounts;


import android.content.Context;
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
        return new AccountViewHolder(v, parent.getContext());
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        final SocialAccountItem item = items.get(position);
        ButterKnife.bind(holder, holder.itemView);
        holder.icon.setImageResource(item.getAccountType().getIcon());
        String title;
        if (item.getUserAccount() == null) {
            int identifier = holder.context.getResources().getIdentifier(item.getAccountType().name(), "string", holder.context.getPackageName());
            title = holder.context.getString(R.string.connect_to,
                    holder.context.getString(identifier));
        } else {
            title = item.getUserAccount().getAccountId();
        }
        holder.title.setText(title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.icon) ImageView icon;
        @Bind(R.id.title) TextView title;

        Context context;

        public AccountViewHolder(View itemView, Context ctx) {
            super(itemView);
            context = ctx;
        }
    }

    public interface OnItemClickListener {
        void onClick(SocialAccountItem item);
    }
}
