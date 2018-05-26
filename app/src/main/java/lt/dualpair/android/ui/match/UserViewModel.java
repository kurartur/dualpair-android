package lt.dualpair.android.ui.match;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.repository.UserPrincipalRepository;
import lt.dualpair.android.data.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;
    private Long reference;
    private MutableLiveData<UserForView> liveData = new MutableLiveData<>();
    private LiveData<UserLocation> lastStoredLocation;
    private UserPrincipalRepository userPrincipalRepository;

    public UserViewModel(UserRepository userRepository, UserPrincipalRepository userPrincipalRepository, Long reference) {
        this.userRepository = userRepository;
        this.userPrincipalRepository = userPrincipalRepository;
        this.reference = reference;
        load();
    }

    private void load() {
        userRepository.getUser(reference)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<UserForView>() {
                    @Override
                    public void accept(UserForView userForView) {
                        liveData.setValue(userForView);
                    }
                });
        lastStoredLocation = userPrincipalRepository.getLastStoredLocation();
    }

    public LiveData<UserForView> getUser() {
        return liveData;
    }

    public Completable unmatch() {
        return userRepository.unmatch(reference);
    }

    public Completable report() {
        return userRepository.report(reference);
    }

    public LiveData<UserLocation> getLastStoredLocation() {
        return lastStoredLocation;
    }

    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {

        private Application application;
        private Long reference;

        public Factory(@NonNull Application application, Long reference) {
            super(application);
            this.application = application;
            this.reference = reference;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(UserViewModel.class)) {
                return (T) new UserViewModel(new UserRepository(application), new UserPrincipalRepository(application), reference);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
