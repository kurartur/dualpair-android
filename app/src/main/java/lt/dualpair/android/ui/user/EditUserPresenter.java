package lt.dualpair.android.ui.user;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditUserPresenter {

    private static final String TAG = "EditUserPresenter";

    private static final String NAME_KEY = "NAME";
    private static final String DATE_OF_BIRTH_KEY = "DATE_OF_BIRTH";
    private static final String DESCRIPTION_KEY = "DESCRIPTION";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private EditUserActivity view;

    private String name;
    private String dateOfBirth;
    private String description;

    private String error;

    public EditUserPresenter(Context context) {
        new UserDataManager(context).getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onError(Throwable e) {
                        error = "Unable to get user data";
                        Log.e(TAG, error, e);
                        publish();
                    }

                    @Override
                    public void onNext(User u) {
                        name = u.getName();
                        dateOfBirth = dateFormat.format(u.getDateOfBirth());
                        description = u.getDescription();
                        publish();
                    }
                });
    }

    public EditUserPresenter(Bundle savedInstanceState) {
        name = savedInstanceState.getString(NAME_KEY);
        dateOfBirth = savedInstanceState.getString(DATE_OF_BIRTH_KEY);
        description = savedInstanceState.getString(DESCRIPTION_KEY);
        publish();
    }

    public void onTakeView(EditUserActivity view) {
        this.view = view;
        publish();
    }

    private void publish() {
        if (view != null) {
            if (error == null) {
                view.render(name, dateOfBirth, description);
            } else {
                view.render(error);
            }
        }
    }

    public void save(String name, String dateOfBirth, String description) {
        try {
            new UserDataManager(view).updateUser(name, dateFormat.parse(dateOfBirth), description)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new EmptySubscriber<User>() {
                        @Override
                        public void onError(Throwable e) {
                            error = "Unable to save user";
                            Log.e(TAG, error, e);
                            publish();
                        }

                        @Override
                        public void onCompleted() {
                            if (view != null) {
                                view.onSaved();
                            }
                        }
                    });
        } catch (ParseException pe) {
            error = "Invalid date";
            Log.e(TAG, error, pe);
            publish();
        }
    }

    public void onSave(Bundle outState) {
        outState.putString(NAME_KEY, name);
        outState.putString(DATE_OF_BIRTH_KEY, dateOfBirth);
        outState.putString(DESCRIPTION_KEY, description);
    }

}
