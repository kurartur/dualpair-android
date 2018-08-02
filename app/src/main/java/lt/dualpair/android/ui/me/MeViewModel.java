package lt.dualpair.android.ui.me;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.accounts.Logouter;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.repository.UserPrincipalRepository;
import lt.dualpair.android.data.repository.UserRepository;

public class MeViewModel extends AndroidViewModel {

    private Long userId;
    private Flowable<List<Sociotype>> sociotypesFlowable;
    private Flowable<User> userFlowable;
    private Flowable<List<UserPhoto>> userPhotosFlowable;
    private UserPrincipalRepository userPrincipalRepository;
    private Logouter logouter;

    public MeViewModel(@NonNull Application application) {
        super(application);
        userId = AccountUtils.getUserId(application);
        UserRepository userRepository = new UserRepository(application);
        logouter = new Logouter(application);
        userPrincipalRepository = new UserPrincipalRepository(application);
        sociotypesFlowable = userRepository.getSociotypes(userId);
        userFlowable = userPrincipalRepository.getUser().toFlowable();
        userPhotosFlowable = userPrincipalRepository.getUserPhotos();
    }

    public Completable logout() {
        return Completable.mergeArray(userPrincipalRepository.logout(), logouter.logout());
    }

    public Flowable<List<Sociotype>> getSociotypes() {
        return sociotypesFlowable;
    }

    public Flowable<User> getUser() {
        return userFlowable;
    }

    public Flowable<List<UserPhoto>> getPhotos() {
        return userPhotosFlowable;
    }
}
