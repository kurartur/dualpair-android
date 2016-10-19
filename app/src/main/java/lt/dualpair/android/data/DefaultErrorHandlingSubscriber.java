package lt.dualpair.android.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import lt.dualpair.android.accounts.AccountConstants;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.data.resource.ErrorResponse;
import lt.dualpair.android.utils.ToastUtils;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class DefaultErrorHandlingSubscriber<T> extends EmptySubscriber<T> {

    private Context context;
    private String logTag;

    public DefaultErrorHandlingSubscriber(Context context) {
        this.context = context;
        this.logTag = context.getClass().getName();
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ServiceException) {
            ServiceException se = (ServiceException)e;
            try {
                Response response = se.getResponse();
                ErrorResponse body = se.getErrorBodyAs(ErrorResponse.class);
                if (response.code() == 401) {
                    onUnauthorized();
                    return;
                }
                ToastUtils.show(context, body.getMessage());
            } catch (IOException ioe) {
                Log.e(logTag, "Error converting to error response", e);
            }
        } else {
            Log.e(logTag, "Error", e);
        }
    }

    public void onUnauthorized() {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                AccountManager manager = AccountManager.get(context);
                Account account = AccountUtils.getAccount(context);
                try {
                    Bundle result = manager.updateCredentials(account, AccountConstants.ACCOUNT_TYPE, null, null, null, null).getResult();
                    context.startActivity((Intent) result.get(AccountManager.KEY_INTENT));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                    throw new RuntimeException(e);
                }
            }
        }).subscribeOn(Schedulers.newThread()).subscribe(new EmptySubscriber<Object>() {

        });

    }
}
