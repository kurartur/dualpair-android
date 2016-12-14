package lt.dualpair.android.ui.search;

import android.os.Bundle;
import android.util.Log;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.SearchParametersManager;
import lt.dualpair.android.data.resource.SearchParameters;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchParametersPresenter {

    private static final String TAG = "SPPresenter";

    private static final String SEARCH_PARAMETERS_KEY = "SEARCH_PARAMETERS";
    public static final Integer MIN_SEARCH_AGE = 18;
    public static final Integer MAX_SEARCH_AGE = 110;

    private SearchParametersActivity view;

    private String error;

    SearchParameters searchParameters;

    public SearchParametersPresenter(SearchParametersActivity view) {
        new SearchParametersManager(view).getSearchParameters().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<SearchParameters>() {
                    @Override
                    public void onError(Throwable e) {
                        error = "Unable to load search parameters";
                        Log.e(TAG, error, e);
                        publish();
                    }

                    @Override
                    public void onNext(SearchParameters sp) {
                        if (sp != null) {
                            searchParameters = sp;
                            publish();
                        }
                    }
                });
    }

    public SearchParametersPresenter(Bundle savedInstanceState) {
        searchParameters = (SearchParameters)savedInstanceState.getSerializable(SEARCH_PARAMETERS_KEY);
        publish();
    }

    public void onTakeView(SearchParametersActivity view) {
        this.view = view;
        publish();
    }

    private void publish() {
        if (view != null) {
            if (error != null) {
                view.render(error);
            } else if (searchParameters != null) {
                view.render(searchParameters);
            }
        }
    }

    public void save(SearchParameters searchParameters) {
        new SearchParametersManager(view).setSearchParameters(searchParameters)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<SearchParameters>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to save search parameters", e);
                        if (view != null) {
                            view.onSaveError("Unable to save search parameters " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "Search parameters saved");
                    }
                });
    }

    public void onSave(Bundle outState) {
        outState.putSerializable(SEARCH_PARAMETERS_KEY, searchParameters);
    }
}
