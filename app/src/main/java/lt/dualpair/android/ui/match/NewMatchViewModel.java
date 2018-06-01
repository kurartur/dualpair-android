package lt.dualpair.android.ui.match;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.repository.UserRepository;

public class NewMatchViewModel extends ViewModel {

    private final MutableLiveData<UserForView> userLiveData = new MutableLiveData<>();
    private UserRepository userRepository;
    private Long reference;

    public NewMatchViewModel(UserRepository userRepository, Long reference) {
        this.userRepository = userRepository;
        this.reference = reference;
        load();
    }

    private void load() {
        userRepository.getUser(reference)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserForView>() {
                    @Override
                    public void accept(UserForView userForView) {
                        userLiveData.setValue(userForView);
                    }
                });
    }

    public LiveData<UserForView> getUser() {
        return userLiveData;
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
            if (modelClass.isAssignableFrom(NewMatchViewModel.class)) {
                return (T) new NewMatchViewModel(new UserRepository(application), reference);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
