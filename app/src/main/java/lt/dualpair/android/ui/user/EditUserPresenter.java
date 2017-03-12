package lt.dualpair.android.ui.user;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.PurposeOfBeing;
import lt.dualpair.android.data.resource.RelationshipStatus;
import lt.dualpair.android.data.resource.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditUserPresenter {

    private static final String TAG = "EditUserPresenter";

    private static final String NAME_KEY = "NAME";
    private static final String DATE_OF_BIRTH_KEY = "DATE_OF_BIRTH";
    private static final String RELATIONSHIP_STATUS_KEY = "RELATIONSHIP_STATUS";
    private static final String DESCRIPTION_KEY = "DESCRIPTION";
    private static final String PURPOSES_OF_BEING_KEY = "PURPOSES_OF_BEING";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private EditUserActivity view;

    private String name;
    private String dateOfBirth;
    private RelationshipStatus relationshipStatus;
    private Set<PurposeOfBeing> purposesOfBeing;
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
                        relationshipStatus = u.getRelationshipStatus();
                        description = u.getDescription();
                        purposesOfBeing = u.getPurposesOfBeing();
                        publish();
                    }
                });
    }

    public EditUserPresenter(Bundle savedInstanceState) {
        name = savedInstanceState.getString(NAME_KEY);
        dateOfBirth = savedInstanceState.getString(DATE_OF_BIRTH_KEY);
        relationshipStatus = (RelationshipStatus)savedInstanceState.getSerializable(RELATIONSHIP_STATUS_KEY);
        description = savedInstanceState.getString(DESCRIPTION_KEY);
        purposesOfBeing = (Set)savedInstanceState.getSerializable(PURPOSES_OF_BEING_KEY);
        publish();
    }

    public void onTakeView(EditUserActivity view) {
        this.view = view;
        publish();
    }

    private void publish() {
        if (view != null) {
            if (error == null) {
                view.render(name, dateOfBirth, relationshipStatus, description, purposesOfBeing);
            } else {
                view.render(error);
            }
        }
    }

    public void setRelationshipStatus(RelationshipStatus relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public void setPurposesOfBeing(Set<PurposeOfBeing> purposesOfBeing) {
        this.purposesOfBeing = purposesOfBeing;
    }

    public void save(String name, String dateOfBirth, String description) {
        try {
            new UserDataManager(view)
                    .updateUser(name,
                                dateFormat.parse(dateOfBirth),
                                description,
                                relationshipStatus,
                                purposesOfBeing)
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
        outState.putSerializable(RELATIONSHIP_STATUS_KEY, relationshipStatus);
        outState.putString(DESCRIPTION_KEY, description);
        outState.putSerializable(PURPOSES_OF_BEING_KEY, new HashSet<>(purposesOfBeing));
    }

}
