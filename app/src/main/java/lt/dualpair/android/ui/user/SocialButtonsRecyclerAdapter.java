package lt.dualpair.android.ui.user;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.ui.accounts.AccountType;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.LabelUtils;

public class SocialButtonsRecyclerAdapter extends RecyclerView.Adapter<SocialButtonsRecyclerAdapter.ViewHolder> {

    private List<UserAccount> userAccounts;
    private OnButtonClick onButtonClick;

    public SocialButtonsRecyclerAdapter(List<UserAccount> userAccounts, OnButtonClick onButtonClick) {
        this.userAccounts = userAccounts;
        this.onButtonClick = onButtonClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.social_buttons_item, parent, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, parent.getContext().getResources().getDisplayMetrics()));
        }
        return new ViewHolder(view, onButtonClick);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserAccount userAccount = userAccounts.get(holder.getAdapterPosition());
        holder.setAccount(userAccount);
    }

    @Override
    public int getItemCount() {
        return userAccounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView accountTypeNameView;
        private ImageView accountTypeIconView;
        private OnButtonClick onClickListener;

        public ViewHolder(View itemView, OnButtonClick onClickListener) {
            super(itemView);
            accountTypeNameView = itemView.findViewById(R.id.acccount_type_name);
            accountTypeIconView = itemView.findViewById(R.id.account_type_icon);
            this.onClickListener = onClickListener;
        }

        public void setAccount(UserAccount userAccount) {
            AccountType accountType = AccountType.valueOf(userAccount.getAccountType());
            accountTypeNameView.setText(LabelUtils.getAccountTypeLabel(itemView.getContext(), accountType));
            accountTypeIconView.setImageResource(accountType.getIcon());
            itemView.setBackgroundResource(DrawableUtils.getAccountTypeColor(itemView.getContext(), accountType));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(userAccount);
                }
            });
        }
    }

    public interface OnButtonClick {
        void onClick(UserAccount userAccount);
    }

}
