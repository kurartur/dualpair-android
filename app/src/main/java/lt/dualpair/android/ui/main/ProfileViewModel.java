package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
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
    private final MutableLiveData<User> user;
    private final MutableLiveData<List<FullUserSociotype>> userSociotypes;
    private final MutableLiveData<List<UserAccount>> userAccounts;
    private final MutableLiveData<List<UserPhoto>> userPhotos;
    private final MutableLiveData<List<UserPurposeOfBeing>> purposesOfBeing;
    private final MutableLiveData<Boolean> isLoggedOut;

    public ProfileViewModel(UserPrincipalRepository userPrincipalRepository, Logouter logouter) {
        this.userPrincipalRepository = userPrincipalRepository;
        this.logouter = logouter;
        user = new MutableLiveData<>();
        userSociotypes = new MutableLiveData<>();
        userAccounts = new MutableLiveData<>();
        userPhotos = new MutableLiveData<>();
        purposesOfBeing = new MutableLiveData<>();
        isLoggedOut = new MutableLiveData<>();
        load();
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<List<FullUserSociotype>> getUserSociotypes() {
        return userSociotypes;
    }

    public MutableLiveData<List<UserAccount>> getUserAccounts() {
        return userAccounts;
    }

    public MutableLiveData<List<UserPhoto>> getUserPhotos() {
        return userPhotos;
    }

    public MutableLiveData<List<UserPurposeOfBeing>> getPurposesOfBeing() {
        return purposesOfBeing;
    }

    private void load() {
        userPrincipalRepository.getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User u) {
                        user.setValue(u);
                    }
                });
        userPrincipalRepository.getFullUserSociotypes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<FullUserSociotype>>() {
                    @Override
                    public void accept(List<FullUserSociotype> us) {
                        userSociotypes.setValue(us);
                    }
                });
        userPrincipalRepository.getUserAccounts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<UserAccount>>() {
                    @Override
                    public void accept(List<UserAccount> ua) {
                        userAccounts.setValue(ua);
                    }
                });
        userPrincipalRepository.getPhotos()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<UserPhoto>>() {
                    @Override
                    public void accept(List<UserPhoto> up) {
                        userPhotos.setValue(up);
                    }
                });
        userPrincipalRepository.getUserPurposesOfBeing()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<UserPurposeOfBeing>>() {
                    @Override
                    public void accept(List<UserPurposeOfBeing> pob) {
                        purposesOfBeing.setValue(pob);
                    }
                });
    }

    public LiveData<Boolean> isLoggedOut() {
        return isLoggedOut;
    }

    public void refresh() {
        load();
    }

    public void logout() {
        Completable.mergeArray(userPrincipalRepository.logout(), logouter.logout())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        isLoggedOut.setValue(true);
                    }
                });
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
