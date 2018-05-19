package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.user.SetSearchParametersClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class SetSearchParametersTask extends AuthenticatedUserTask<SearchParameters> {

    private SearchParameters searchParameters;
    private SearchParametersRepository searchParametersRepository;
    private MatchRepository matchRepository;

    public SetSearchParametersTask(SearchParameters searchParameters) {
        this.searchParameters = searchParameters;
    }

    @Override
    protected Observable<SearchParameters> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<SearchParameters>() {
            @Override
            public void call(Subscriber<? super SearchParameters> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                searchParametersRepository = new SearchParametersRepository(db);
                matchRepository = new MatchRepository(db);
                new SetSearchParametersClient(getUserId(context), searchParameters).observable().toBlocking().first();
                searchParametersRepository.save(searchParameters);
                matchRepository.clearNotReviewedMatches(getUserId(context));
                subscriber.onNext(searchParameters);
                subscriber.onCompleted();
            }
        });
    }

}
