package lt.dualpair.android.ui.user;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import lt.dualpair.android.data.resource.UserAccount;

public class SocialButtonsRecyclerAdapter extends RecyclerView.Adapter<SocialButtonsRecyclerAdapter.ViewHolder> {

    private List<UserAccount> userAccounts;
    private OnButtonClick onButtonClick;

    public SocialButtonsRecyclerAdapter(List<UserAccount> userAccounts, OnButtonClick onButtonClick) {
        this.userAccounts = userAccounts;
        this.onButtonClick = onButtonClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, parent.getContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, parent.getContext().getResources().getDisplayMetrics()));
        imageView.setLayoutParams(layoutParams);
        imageView.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, parent.getContext().getResources().getDisplayMetrics()),
                0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, parent.getContext().getResources().getDisplayMetrics()),
                0);
        imageView.setAlpha(0.9f);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserAccount userAccount = userAccounts.get(holder.getAdapterPosition());
        holder.imageView().setImageResource(userAccount.getAccountType().getIcon());
        holder.imageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick.onClick(userAccount);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userAccounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ImageView imageView() {
            return (ImageView)itemView;
        }
    }

    public interface OnButtonClick {
        void onClick(UserAccount userAccount);
    }

}
