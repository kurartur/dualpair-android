package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import lt.dualpair.android.accounts.Logouter;
import lt.dualpair.android.data.local.entity.FullUserSociotype;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class ProfileViewModel extends ViewModel {

    private UserPrincipalRepository userPrincipalRepository;
    private Logouter logouter;
    private final LiveData<User> userLive;
    private final LiveData<List<FullUserSociotype>> userSociotypesLive;
    private final LiveData<List<UserAccount>> userAccountsLive;
    private final LiveData<List<UserPhoto>> userPhotosLive;
    private final LiveData<List<UserPurposeOfBeing>> purposesOfBeingLive;

    public ProfileViewModel(UserPrincipalRepository userPrincipalRepository, Logouter logouter) {
        this.userPrincipalRepository = userPrincipalRepository;
        this.logouter = logouter;
        userLive = userPrincipalRepository.getUserLive();
        userSociotypesLive = userPrincipalRepository.getFullUserSociotypesLive();
        userAccountsLive = userPrincipalRepository.getUserAccountsLive();
        userPhotosLive = userPrincipalRepository.getUserPhotosLive();
        purposesOfBeingLive = userPrincipalRepository.getUserPurposesOfBeingLive();
    }

    public LiveData<User> getUserLive() {
        return userLive;
    }

    public LiveData<List<FullUserSociotype>> getUserSociotypesLive() {
        return userSociotypesLive;
    }

    public LiveData<List<UserAccount>> getUserAccountsLive() {
        return userAccountsLive;
    }

    public LiveData<List<UserPhoto>> getUserPhotosLive() {
        return userPhotosLive;
    }

    public LiveData<List<UserPurposeOfBeing>> getPurposesOfBeingLive() {
        return purposesOfBeingLive;
    }

    public Completable refresh() {
        return userPrincipalRepository.loadFromApiIfTime();
    }

    public Completable logout() {
        return Completable.mergeArray(userPrincipalRepository.logout(), logouter.logout());
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
            if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
                return (T) new ProfileViewModel(new UserPrincipalRepository(application), new Logouter(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
