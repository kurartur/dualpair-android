package lt.dualpair.android.accounts;

import android.accounts.AccountManager;
import android.content.Context;

import lt.dualpair.android.TokenProvider;
import lt.dualpair.android.data.repo.DatabaseHelper;

public class Logouter {

    private Context context;

    public Logouter(Context context) {
        this.context = context;
    }

    public void logout() {
        TokenProvider.getInstance().storeToken(null);
        DatabaseHelper.reset(context);
        AccountUtils.removeAccount(AccountManager.get(context));
    }

}
