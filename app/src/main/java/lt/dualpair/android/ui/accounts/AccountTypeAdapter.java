package lt.dualpair.android.ui.accounts;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lt.dualpair.android.R;


public class AccountTypeAdapter extends BaseAdapter {

    private Activity activity;
    private List<AccountType> accountTypes;

    public AccountTypeAdapter(Activity activity, List<AccountType> accountTypes) {
        this.activity = activity;
        this.accountTypes = accountTypes;
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
        AccountType accountType = (AccountType)getItem(position);
        View view = activity.getLayoutInflater().inflate(R.layout.add_user_account_item, null);
        ImageView icon = (ImageView)view.findViewById(R.id.icon);
        TextView name = (TextView)view.findViewById(R.id.name);
        icon.setImageResource(accountType.getIcon());
        name.setText(activity.getResources().getString(activity.getResources().getIdentifier(accountType.name(), "string", activity.getPackageName())));
        return view;
    }

}
