package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.UserAccount;

public class AccountListAdapter extends BaseAdapter {

    private List<UserAccount> accountList;
    private Activity activity;

    public AccountListAdapter(List<UserAccount> accountList, Activity activity) {
        this.accountList = accountList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return accountList.size();
    }

    @Override
    public Object getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserAccount account = (UserAccount) getItem(position);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.user_account_list_item, parent, false);
        TextView accountType = view.findViewById(R.id.account_type);
        accountType.setText(account.getAccountType().name());
        return view;
    }
}
