package lt.dualpair.android.ui.user;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.repository.UserPrincipalRepository;
import lt.dualpair.android.data.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;
    private LiveData<UserLocation> lastStoredLocation;
    private UserPrincipalRepository userPrincipalRepository;

    public UserViewModel(UserRepository userRepository, UserPrincipalRepository userPrincipalRepository) {
        this.userRepository = userRepository;
        this.userPrincipalRepository = userPrincipalRepository;
        lastStoredLocation = userPrincipalRepository.getLastStoredLocation();
    }

    public Flowable<UserForView> getUser(Long userId) {
        return userRepository.getUser2(userId);
    }

    public Completable unmatch(Long userId) {
        return userRepository.unmatch(userId);
    }

    public Completable report(Long userId) {
        return userRepository.report(userId);
    }

    public LiveData<UserLocation> getLastStoredLocation() {
        return lastStoredLocation;
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
            if (modelClass.isAssignableFrom(UserViewModel.class)) {
                return (T) new UserViewModel(new UserRepository(application), new UserPrincipalRepository(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
