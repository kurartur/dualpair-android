package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.LocationSingle;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.data.repository.UserPrincipalRepository;
import lt.dualpair.android.data.repository.UserRepository;
import lt.dualpair.android.utils.LocationUtil;

public class SearchViewModel extends AndroidViewModel {

    private static final long LOCATION_UPDATE_INTERVAL = 1000 * 60 * 5;
    private long lastLocationUpdate;

    private UserRepository userRepository;
    private UserPrincipalRepository userPrincipalRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        userPrincipalRepository = new UserPrincipalRepository(application);
    }

    public Maybe<UserForView> find() {
        return Completable.defer(() -> {
            if (isLocationUpdateRequired()) {
                return new LocationSingle(getApplication(), LocationUtil.createLocationRequest())
                    .flatMapCompletable(location ->
                            userPrincipalRepository.saveLocation(location)
                                .subscribeOn(Schedulers.io())
                                .doOnComplete(() -> lastLocationUpdate = System.currentTimeMillis()));
            }
            return Completable.complete();
        }).andThen(userPrincipalRepository.getSearchParameters())
        .flatMapMaybe((Function<UserSearchParameters, Maybe<UserForView>>) userSearchParameters -> userRepository.find(userSearchParameters));
    }

    private boolean isLocationUpdateRequired() {
        return System.currentTimeMillis() - lastLocationUpdate > LOCATION_UPDATE_INTERVAL;
    }

}
