package lt.dualpair.android.ui.search;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class SearchParametersViewModel extends ViewModel {

    private static final String SEARCH_PARAMETERS_KEY = "SEARCH_PARAMETERS";
    public static final Integer MIN_SEARCH_AGE = 18;
    public static final Integer MAX_SEARCH_AGE = 110;

    private final MutableLiveData<UserSearchParameters> searchParameters;
    private UserPrincipalRepository userPrincipalRepository;

    public SearchParametersViewModel(UserPrincipalRepository userPrincipalRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
        searchParameters = new MutableLiveData<>();
        loadSearchParameters();
    }

    private void loadSearchParameters() {
        userPrincipalRepository.getSearchParameters()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<UserSearchParameters>() {
                    @Override
                    public void accept(UserSearchParameters userSearchParameters) {
                        searchParameters.setValue(userSearchParameters);
                    }
                });
    }

    public Completable save(final UserSearchParameters sp) {
        return userPrincipalRepository.setSearchParameters(sp)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() {
                        searchParameters.setValue(sp);
                    }
                });
    }

    public LiveData<UserSearchParameters> getSearchParameters() {
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
                return (T) new SearchParametersViewModel(new UserPrincipalRepository(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }

    }
}
