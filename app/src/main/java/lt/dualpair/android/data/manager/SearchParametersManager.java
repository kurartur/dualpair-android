package lt.dualpair.android.data.manager;

import android.content.Context;

import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.Task;
import lt.dualpair.android.data.task.user.GetSearchParametersTask;
import lt.dualpair.android.data.task.user.SetSearchParametersTask;
import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class SearchParametersManager extends DataManager {

    private static Subject<SearchParameters, SearchParameters> searchParametersSubject = PublishSubject.create();

    public SearchParametersManager(Context context) {
        super(context);
    }

    public Observable<SearchParameters> getSearchParameters() {
        PublishSubject<SearchParameters> subject = PublishSubject.create();
        searchParametersSubject.subscribe(subject);
        enqueueTask(new QueuedTask<>("getSearchParameters", new TaskCreator<SearchParameters>() {
            @Override
            public Task<SearchParameters> createTask(Context context) {
                return new GetSearchParametersTask(context);
            }
        }, subject));
        return subject.asObservable();
    }

    public Observable<SearchParameters> setSearchParameters(final SearchParameters sp) {
        PublishSubject<SearchParameters> subject = PublishSubject.create();
        subject.doOnNext(new Action1<SearchParameters>() {
            @Override
            public void call(SearchParameters searchParameters) {
                searchParametersSubject.onNext(searchParameters);
            }
        });
        enqueueTask(new QueuedTask<>("setSearchParameters", new TaskCreator<SearchParameters>() {
            @Override
            public Task<SearchParameters> createTask(Context context) {
                return new SetSearchParametersTask(context, sp);
            }
        }, subject));
        return subject.asObservable();
    }


}
