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

    public static final Integer MIN_SEARCH_AGE = 18;
    public static final Integer MAX_SEARCH_AGE = 110;

    private static final String SEARCH_MALE_KEY = "SEARCH_MALE";
    private static final String SEARCH_FEMALE_KEY = "SEARCH_FEMALE";
    private static final String MIN_AGE_KEY = "MIN_AGE";
    private static final String MAX_AGE_KEY = "MAX_AGE";

    private SearchParametersActivity view;

    private String error;

    private boolean searchMale;
    private boolean searchFemale;
    private int minAge;
    private int maxAge;

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
                    public void onNext(SearchParameters searchParameters) {
                        if (searchParameters != null) {
                            searchMale = searchParameters.getSearchMale();
                            searchFemale = searchParameters.getSearchFemale();
                            minAge = searchParameters.getMinAge();
                            maxAge = searchParameters.getMaxAge();
                            publish();
                        }
                    }
                });
    }

    public SearchParametersPresenter(Bundle savedInstanceState) {
        searchMale = savedInstanceState.getBoolean(SEARCH_MALE_KEY);
        searchFemale = savedInstanceState.getBoolean(SEARCH_FEMALE_KEY);
        minAge = savedInstanceState.getInt(MIN_AGE_KEY);
        maxAge = savedInstanceState.getInt(MAX_AGE_KEY);
        publish();
    }

    public void onTakeView(SearchParametersActivity view) {
        this.view = view;
        publish();
    }

    private void publish() {
        if (view != null) {
            if (error == null) {
                view.render(searchMale, searchFemale, minAge, maxAge);
            } else {
                view.render(error);
            }
        }
    }

    public void save(boolean searchMale, boolean searchFemale, int minAge, int maxAge) {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchMale(searchMale);
        searchParameters.setSearchFemale(searchFemale);
        searchParameters.setMinAge(minAge);
        searchParameters.setMaxAge(maxAge);
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
        outState.putBoolean(SEARCH_MALE_KEY, searchMale);
        outState.putBoolean(SEARCH_FEMALE_KEY, searchFemale);
        outState.putInt(MIN_AGE_KEY, minAge);
        outState.putInt(MAX_AGE_KEY, maxAge);
    }
}
