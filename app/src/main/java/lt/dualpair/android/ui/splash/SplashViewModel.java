package lt.dualpair.android.ui.splash;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Single;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserSociotype;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class SplashViewModel extends ViewModel {

    private UserPrincipalRepository userPrincipalRepository;

    public SplashViewModel(UserPrincipalRepository userPrincipalRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
    }

    public Single<User> getUser() {
        return userPrincipalRepository.getUser();
    }

    public Single<List<UserSociotype>> getUserSociotypes() {
        return userPrincipalRepository.getSociotypes();
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
            if (modelClass.isAssignableFrom(SplashViewModel.class)) {
                return (T) new SplashViewModel(new UserPrincipalRepository(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
