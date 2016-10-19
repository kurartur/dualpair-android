package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.UserAccount;

public class AccountGridAdapter extends BaseAdapter { // TODO replace with ArrayAdapter?

    private List<UserAccount> userAccounts;
    private Activity activity;

    public AccountGridAdapter(Activity activity) {
        this.activity = activity;
    }

    public void append(UserAccount userAccount) {
        userAccounts.add(userAccount);
    }

    public void clear() {
        userAccounts.clear();
    }

    @Override
    public int getCount() {
        return userAccounts.size();
    }

    @Override
    public Object getItem(int position) {
        return userAccounts.get(position);
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
        ImageView icon = (ImageView)view.findViewById(R.id.account_icon);
        icon.setImageResource(R.drawable.fb_f_logo__blue_50);
        return view;
    }
}
