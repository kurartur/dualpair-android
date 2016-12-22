package lt.dualpair.android.ui.user;


import android.content.Context;
import android.util.Log;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditPhotosPresenter {

    private static final String TAG = "EditPhotosPresenter";

    private EditPhotosActivity view;

    private User user;

    public EditPhotosPresenter(Context context) {
        new UserDataManager(context).getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to load user", e);
                    }

                    @Override
                    public void onNext(User u) {
                        user = u;
                        publish();
                    }
                });
    }

    private void publish() {
        if (view != null) {
            if (user != null) {
                view.render(user);
            }
        }
    }

    public void onTakeView(EditPhotosActivity view) {
        this.view = view;
        publish();
    }
}
