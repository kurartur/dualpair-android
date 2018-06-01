package lt.dualpair.android.data.remote.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import lt.dualpair.android.accounts.AccountUtils;

public class TokenProvider {

    private static TokenProvider INSTANCE;

    private BehaviorSubject<String> tokenSubject = BehaviorSubject.create();
    private PublishSubject<String> refreshTokenSubject = PublishSubject.create();
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);
    private Context context;

    private TokenProvider(Application application) {
        context = application;
        publishToken();
    }

    public void publishToken() {
        AccountManager am = AccountManager.get(context);
        Account account = AccountUtils.getAccount(am);
        if (account != null) {
            String token = AccountUtils.peekAuthToken(am, account);
            if (token != null) {
                tokenSubject.onNext(token);
                return;
            }
            AccountUtils.getAuthToken(am, account, new CustomAccountManagerCallback() {
                @Override
                protected void doWithToken(String token) {
                    tokenSubject.onNext(token);
                }
            });
        }
    }

    public Observable<String> getAuthToken() {
        return tokenSubject;
    }

    public Observable<String> requestTokenRefresh() {
        if (isRefreshing.compareAndSet(false, true)) {
            AccountManager am = AccountManager.get(context);
            AccountUtils.invalidateCurrentAuthToken(am);
            AccountUtils.getAuthToken(am, AccountUtils.getAccount(am), new CustomAccountManagerCallback() {
                @Override
                protected void doWithToken(String token) {
                    tokenSubject.onNext(token);
                    refreshTokenSubject.onNext(token);
                    isRefreshing.compareAndSet(true, false);
                }
            });
        }
        return refreshTokenSubject;
    }

    public static void initialize(Application application) {
        INSTANCE = new TokenProvider(application);
    }

    public static TokenProvider getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException(TokenProvider.class.getSimpleName() + " not initialized!");
        }
        return INSTANCE;
    }

    public abstract static class CustomAccountManagerCallback implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            Bundle bundle = null;
            Intent authIntent = null;
            try {
                bundle = future.getResult();
                authIntent = (Intent) bundle.get(AccountManager.KEY_INTENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (authIntent != null) {
                throw new UnsupportedOperationException("Returned intent");
            }

            doWithToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));

        }

        protected abstract void doWithToken(String token);
    }

}
