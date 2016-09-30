package lt.dualpair.android.data.manager;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.ResultReceiver;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.task.user.GetSearchParametersTask;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.service.DataService;
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
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra("RECEIVER", new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                searchParametersSubject.onNext((SearchParameters) resultData.getSerializable("SP"));
            }
        });
        context.startService(intent);
    }


}
