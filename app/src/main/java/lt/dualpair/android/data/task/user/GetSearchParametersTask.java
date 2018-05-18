package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.user.GetSearchParametersClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class GetSearchParametersTask extends AuthenticatedUserTask<SearchParameters> {

    public GetSearchParametersTask(String authToken) {
        super(authToken);
    }

    @Override
    protected Observable<SearchParameters> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<SearchParameters>() {
            @Override
            public void call(Subscriber<? super SearchParameters> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                SearchParametersRepository searchParametersRepository = new SearchParametersRepository(db);
                SearchParameters sp = new GetSearchParametersClient(getUserId(context)).observable().toBlocking().first();
                searchParametersRepository.save(sp);
                subscriber.onNext(sp);
                subscriber.onCompleted();
            }
        });
    }
}
