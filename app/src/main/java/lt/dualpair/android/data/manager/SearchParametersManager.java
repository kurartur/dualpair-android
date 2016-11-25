package lt.dualpair.android.data.manager;

import android.content.Context;

import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.Task;
import lt.dualpair.android.data.task.user.GetSearchParametersTask;
import lt.dualpair.android.data.task.user.SetSearchParametersTask;
import rx.Observable;

public class SearchParametersManager extends DataManager {

    public SearchParametersManager(Context context) {
        super(context);
    }

    public Observable<SearchParameters> getSearchParameters() {
        return execute(context, new DataRequest<>("getSearchParameters", new AuthenticatedTaskCreator<SearchParameters>() {
            @Override
            protected Task<SearchParameters> doCreateTask(String authToken) {
                return new GetSearchParametersTask(authToken);
            }
        }));
    }

    public Observable<SearchParameters> setSearchParameters(final SearchParameters sp) {
        return execute(context, new DataRequest<>("setSearchParameters", new AuthenticatedTaskCreator<SearchParameters>() {
            @Override
            protected Task<SearchParameters> doCreateTask(String authToken) {
                return new SetSearchParametersTask(authToken, sp);
            }
        }));
    }


}
