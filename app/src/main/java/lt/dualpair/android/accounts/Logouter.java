package lt.dualpair.android.accounts;

import android.accounts.AccountManager;
import android.content.Context;

import io.reactivex.Completable;
import io.reactivex.functions.Action;
import lt.dualpair.android.TokenProvider;
import lt.dualpair.android.data.local.DualPairRoomDatabase;

public class Logouter {

    private Context context;

    public Logouter(Context context) {
        this.context = context;
    }

    public Completable logout() {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                TokenProvider.getInstance().storeToken(null);
                DualPairRoomDatabase.reset();
                AccountUtils.removeAccount(AccountManager.get(context));
            }
        });
    }

}
