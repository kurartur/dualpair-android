package lt.dualpair.android.ui.main;


import android.content.Context;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.SearchParametersManager;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.resource.User;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NextMatchRequestValidator {

    public enum Error {
        NO_SOCIOTYPE, NO_DATE_OF_BIRTH, NO_SEARCH_PARAMETERS
    }

    private Context context;

    public NextMatchRequestValidator(Context context) {
        this.context = context;
    }

    public Observable<Error> validate() {
        return Observable.create(new Observable.OnSubscribe<Error>() {
            @Override
            public void call(final Subscriber<? super Error> subscriber) {
                new UserDataManager(context).getUser()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new EmptySubscriber<User>() {
                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onNext(User user) {
                                unsubscribe();
                                validateUser(user, subscriber);
                            }
                        });
            }
        });
    }

    private void validateUser(User user, Subscriber<? super Error> subscriber) {
        if (user.getSociotypes().isEmpty()) {
            subscriber.onNext(Error.NO_SOCIOTYPE);
        } else if (user.getDateOfBirth() == null) {
            subscriber.onNext(Error.NO_DATE_OF_BIRTH);
        } else {
            validateSearchParameters(subscriber);
        }

    }

    private void validateSearchParameters(final Subscriber<? super Error> subscriber) {
        new SearchParametersManager(context).getSearchParameters()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<SearchParameters>() {
                    @Override
                    public void onError(Throwable e) {
                        subscriber.onError(e);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onNext(SearchParameters searchParameters) {
                        unsubscribe();
                        if (searchParameters == null
                                || (!searchParameters.getSearchFemale() && !searchParameters.getSearchMale())
                                || searchParameters.getMinAge() == null
                                || searchParameters.getMaxAge() == null) {
                            subscriber.onNext(Error.NO_SEARCH_PARAMETERS);
                        }
                        subscriber.onCompleted();
                    }
                });
    }


}
