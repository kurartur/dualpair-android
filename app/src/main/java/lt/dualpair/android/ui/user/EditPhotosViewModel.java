package lt.dualpair.android.ui.user;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.repository.PhotoRepository;
import lt.dualpair.android.data.repository.UserPrincipalRepository;
import lt.dualpair.android.ui.accounts.AccountType;

public class EditPhotosViewModel extends AndroidViewModel {

    private UserPrincipalRepository userPrincipalRepository;
    private PhotoRepository photoRepository;
    private final Map<AccountType, List<UserPhoto>> availablePhotos = new HashMap<>();
    private final Flowable<List<UserPhoto>> photosFlowable;
    private final Flowable<List<UserAccount>> userAccounts;

    public EditPhotosViewModel(@NonNull Application application) {
        super(application);
        this.userPrincipalRepository = new UserPrincipalRepository(application);
        this.photoRepository = new PhotoRepository(application);

        photosFlowable = userPrincipalRepository.getPhotos().toFlowable();
        userAccounts = userPrincipalRepository.getUserAccounts().toFlowable();
    }

    public Single<List<UserPhoto>> getPhotos() {
        return photosFlowable.singleOrError();
    }

    public Single<List<UserAccount>> getUserAccounts() {
        return userAccounts.singleOrError();
    }

    public Completable save(List<UserPhoto> photos) {
        return userPrincipalRepository.savePhotos(photos);
    }

    public Observable<List<UserPhoto>> getAvailablePhotos(AccountType accountType) {
        if (availablePhotos.containsKey(accountType)) {
            return Observable.just(availablePhotos.get(accountType));
        } else {
            return photoRepository.getAvailableUserPhotos(accountType)
                    .doOnNext(userPhotos -> availablePhotos.put(accountType, userPhotos));
        }
    }
}
