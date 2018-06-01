package lt.dualpair.android.ui.accounts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lt.dualpair.android.R;


public class AccountTypeAdapter extends BaseAdapter {

    private Context context;
    private List<AccountType> accountTypes;
    private OnAccountTypeClickListener onAccountTypeClickListener;

    public AccountTypeAdapter(Context context, List<AccountType> accountTypes, OnAccountTypeClickListener onAccountTypeClickListener) {
        this.context = context;
        this.accountTypes = accountTypes;
        this.onAccountTypeClickListener = onAccountTypeClickListener;
    }

    @Override
    public int getCount() {
        return accountTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return accountTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AccountType accountType = (AccountType)getItem(position);
        View view = LayoutInflater.from(context).inflate(R.layout.account_type_list_item, null);
        ImageView icon = view.findViewById(R.id.icon);
        TextView name = view.findViewById(R.id.name);
        icon.setImageResource(accountType.getIcon());
        name.setText(context.getResources().getString(context.getResources().getIdentifier(accountType.name(), "string", context.getPackageName())));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAccountTypeClickListener.onClick(accountType);
            }
        });

        return view;
    }

    public interface OnAccountTypeClickListener {
        void onClick(AccountType accountType);
    }

}
