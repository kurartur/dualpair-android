package lt.dualpair.android.ui.user;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class EditPhotosViewModel extends ViewModel {

    private UserPrincipalRepository userPrincipalRepository;
    private final MutableLiveData<PhotoEditingData> data;

    public EditPhotosViewModel(UserPrincipalRepository userPrincipalRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
        data = new MutableLiveData<>();
        load();
    }

    private void load() {
        Single.zip(
                userPrincipalRepository.getUser(),
                userPrincipalRepository.getPhotos(),
                userPrincipalRepository.getUserAccounts(),
                new Function3<User, List<UserPhoto>, List<UserAccount>, PhotoEditingData>() {
                    @Override
                    public PhotoEditingData apply(User user, List<UserPhoto> userPhotos, List<UserAccount> userAccounts) {
                        return new PhotoEditingData(user, userPhotos, userAccounts);
                    }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<PhotoEditingData>() {
                @Override
                public void accept(PhotoEditingData photoEditingData) {
                    data.setValue(photoEditingData);
                }
            });

    }

    public LiveData<PhotoEditingData> getData() {
        return data;
    }

    public Completable save(List<UserPhoto> photos) {
        return userPrincipalRepository.savePhotos(photos)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public static class PhotoEditingData {
        private User user;
        private List<UserPhoto> photos;
        private List<UserAccount> userAccounts;

        public PhotoEditingData(User user, List<UserPhoto> photos, List<UserAccount> userAccounts) {
            this.user = user;
            this.photos = photos;
            this.userAccounts = userAccounts;
        }

        public User getUser() {
            return user;
        }

        public List<UserPhoto> getPhotos() {
            return photos;
        }

        public List<UserAccount> getUserAccounts() {
            return userAccounts;
        }
    }

    public static class Factory implements ViewModelProvider.Factory {
        private Application application;
        public Factory(Application application) {
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(EditPhotosViewModel.class)) {
                return (T) new EditPhotosViewModel(new UserPrincipalRepository(application));
            }
            throw new IllegalArgumentException("Wrong classs");
        }
    }
}
