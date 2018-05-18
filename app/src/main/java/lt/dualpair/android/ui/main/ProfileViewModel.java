package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import lt.dualpair.android.accounts.Logouter;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileViewModel extends ViewModel {

    private UserDataManager userDataManager;
    private Logouter logouter;
    private final MutableLiveData<User> user;
    private final MutableLiveData<Boolean> isLoggedOut;

    public ProfileViewModel(UserDataManager userDataManager, Logouter logouter) {
        this.userDataManager = userDataManager;
        this.logouter = logouter;
        user = new MutableLiveData<>();
        isLoggedOut = new MutableLiveData<>();
        load();
    }

    public LiveData<User> getUser() {
        return user;
    }

    private void load() {
        userDataManager.getUser(true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onNext(User u) {
                        user.setValue(u);
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
        userDataManager.logout()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<Void>() {
                    @Override
                    public void onNext(Void v) {
                        logouter.logout();
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
                return (T) new ProfileViewModel(new UserDataManager(application), new Logouter(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
