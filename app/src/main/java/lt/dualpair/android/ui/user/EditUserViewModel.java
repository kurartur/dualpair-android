package lt.dualpair.android.ui.user;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.local.entity.PurposeOfBeing;
import lt.dualpair.android.data.local.entity.RelationshipStatus;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class EditUserViewModel extends ViewModel {

    private UserPrincipalRepository userPrincipalRepository;
    private final MutableLiveData<UserData> data;

    public EditUserViewModel(UserPrincipalRepository userPrincipalRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
        data = new MutableLiveData<>();
        load();
    }

    private void load() {
        Single.zip(userPrincipalRepository.getUser(), userPrincipalRepository.getUserPurposesOfBeing(), new BiFunction<User, List<UserPurposeOfBeing>, UserData>() {
                    @Override
                    public UserData apply(User user, List<UserPurposeOfBeing> userPurposeOfBeings) throws Exception {
                        return new UserData(user, userPurposeOfBeings);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserData>() {
                    @Override
                    public void accept(UserData userData) throws Exception {
                        data.setValue(userData);
                    }
                });
    }

    public MutableLiveData<UserData> getData() {
        return data;
    }

    public Completable save(String name, Date dateOfBirth, String description, RelationshipStatus relationshipStatus, List<PurposeOfBeing> purposesOfBeing) {
        return userPrincipalRepository.updateUser(
                            name,
                            dateOfBirth,
                            description,
                            relationshipStatus,
                            purposesOfBeing);
    }

    public static class UserData {

        private User user;
        private List<UserPurposeOfBeing> purposeOfBeings;

        public UserData(User user, List<UserPurposeOfBeing> purposeOfBeings) {
            this.user = user;
            this.purposeOfBeings = purposeOfBeings;
        }

        public User getUser() {
            return user;
        }

        public List<UserPurposeOfBeing> getPurposeOfBeings() {
            return purposeOfBeings;
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
            if (modelClass.isAssignableFrom(EditUserViewModel.class)) {
                return (T) new EditUserViewModel(new UserPrincipalRepository(application));
            }
            throw new IllegalArgumentException("Wrong classs");
        }
    }
}
