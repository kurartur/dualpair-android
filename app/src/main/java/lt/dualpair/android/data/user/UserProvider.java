package lt.dualpair.android.data.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.core.user.GetSearchParametersTask;
import lt.dualpair.android.core.user.GetUserPrincipalTask;
import lt.dualpair.android.core.user.SetSearchParametersTask;
import lt.dualpair.android.data.DbHelper;
import lt.dualpair.android.data.Provider;
import lt.dualpair.android.resource.SearchParameters;
import lt.dualpair.android.resource.User;
import lt.dualpair.android.rx.EmptySubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class UserProvider extends Provider {

    private static final String TAG = "UserProvider";

    private static Subject<User, User> userSubject = PublishSubject.create();
    private static Subject<SearchParameters, SearchParameters> searchParametersSubject = PublishSubject.create();

    private UserRepository userRepository;
    private SearchParametersRepository searchParametersRepository;

    public UserProvider(Context context) {
        super(context);
        SQLiteDatabase db = DbHelper.forCurrentUser(context).getWritableDatabase();
        userRepository = new UserRepository(db);
        searchParametersRepository = new SearchParametersRepository(db);
    }

    public Subscription user(Subscriber<User> subscriber) {
        Subscription subscription = userSubject.subscribe(subscriber);
        User user = userRepository.get(AccountUtils.getUserId(context));
        if (user != null) {
            userSubject.onNext(user);
        } else {
            new GetUserPrincipalTask(context).execute(new EmptySubscriber<User>() {
                @Override
                public void onNext(User user) {
                    userRepository.save(user);
                    userSubject.onNext(user);
                }
            });
        }
        return subscription;
    }

    public Subscription searchParameters(Subscriber<SearchParameters> subscriber) {
        Subscription subscription = searchParametersSubject.subscribe(subscriber);
        SearchParameters sp = searchParametersRepository.get();
        if (sp != null) {
            searchParametersSubject.onNext(sp);
        } else {
            new GetSearchParametersTask(context).execute(new EmptySubscriber<SearchParameters>() {
                @Override
                public void onNext(SearchParameters searchParameters) {
                    searchParametersRepository.save(searchParameters);
                    searchParametersSubject.onNext(searchParameters);
                }
            });
        }
        return subscription;
    }

    public void setSearchParameters(final SearchParameters sp) {
        searchParametersRepository.save(sp);
        searchParametersSubject.onNext(sp);
        new SetSearchParametersTask(context, sp).execute(new EmptySubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to save search parameters", e);
            }

            @Override
            public void onNext(Void aVoid) {
                // TODO update row
            }
        });
    }
}
