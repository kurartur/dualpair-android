package lt.dualpair.android.ui.search;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.SearchParameters;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchParametersViewModel extends ViewModel {

    private static final String SEARCH_PARAMETERS_KEY = "SEARCH_PARAMETERS";
    public static final Integer MIN_SEARCH_AGE = 18;
    public static final Integer MAX_SEARCH_AGE = 110;

    private final MutableLiveData<SearchParameters> searchParameters;
    private UserDataManager userDataManager;

    public SearchParametersViewModel(UserDataManager userDataManager) {
        this.userDataManager = userDataManager;
        searchParameters = new MutableLiveData<>();
        loadSearchParameters();
    }

    private void loadSearchParameters() {
        userDataManager.getSearchParameters()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<SearchParameters>() {
                    @Override
                    public void onNext(SearchParameters sp) {
                        if (sp != null) {
                            searchParameters.setValue(sp);
                        }
                    }
                });
    }

    public Observable<SearchParameters> save(final SearchParameters sp) {
        return userDataManager.setSearchParameters(sp)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<SearchParameters, Observable<SearchParameters>>() {
                    @Override
                    public Observable<SearchParameters> call(SearchParameters sp) {
                        searchParameters.setValue(sp);
                        return Observable.just(sp);
                    }
                });
    }

    public LiveData<SearchParameters> getSearchParameters() {
        return searchParameters;
    }

    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {

        private Application application;
        public Factory(@NonNull Application application) {
            super(application);
            this.application = application;
        }
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(SearchParametersViewModel.class)) {
                return (T) new SearchParametersViewModel(new UserDataManager(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }

    }
}
