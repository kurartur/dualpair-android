package lt.dualpair.android.data.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.task.user.GetSearchParametersTask;
import lt.dualpair.android.data.remote.task.user.SetSearchParametersTask;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.SearchParameters;
import rx.Subscriber;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class SearchParametersManager extends DataManager {

    private static final String TAG = "SPManager";

    private static Subject<SearchParameters, SearchParameters> searchParametersSubject = PublishSubject.create();

    private SearchParametersRepository searchParametersRepository;

    public SearchParametersManager(Context context) {
        super(context);
        SQLiteDatabase db = DbHelper.forCurrentUser(context).getWritableDatabase();
        searchParametersRepository = new SearchParametersRepository(db);
    }

    public Subscription getSearchParameters(Subscriber<SearchParameters> subscriber) {
        Subscription subscription = searchParametersSubject.subscribe(subscriber);
        SearchParameters sp = searchParametersRepository.getLastUsed();
        if (sp != null) {
            searchParametersSubject.onNext(sp);
        } else {
            enqueueTask(new QueuedTask<>("getSearchParameters", new GetSearchParametersTask(context), new EmptySubscriber<SearchParameters>() {
                @Override
                public void onNext(SearchParameters searchParameters) {
                    searchParametersRepository.save(searchParameters);
                    searchParametersSubject.onNext(searchParameters);
                }
            }));
        }
        return subscription;
    }

    public void setSearchParameters(final SearchParameters sp) {
        enqueueTask(new QueuedTask<>("setSearchParameters", new SetSearchParametersTask(context, sp), new EmptySubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to save sp", e);
            }

            @Override
            public void onNext(Void aVoid) {
                searchParametersRepository.save(sp);
                searchParametersSubject.onNext(sp);
            }
        }));
    }


}
