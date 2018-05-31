package lt.dualpair.android.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationSettingsResult;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.data.repository.UserPrincipalRepository;
import lt.dualpair.android.data.repository.UserRepository;
import lt.dualpair.android.ui.Resource;

public class ReviewViewModel extends ViewModel {

    private static final String TAG = ReviewViewModel.class.getName();

    private final UserPrincipalRepository userPrincipalRepository;
    private final UserRepository userRepository;

    private MutableLiveData<Resource<UserForView>> userToReview;

    private LiveData<LocationSettingsResult> locationSettingsResult;
    private LiveData<Location> location;
    private final LiveData<UserLocation> lastStoredLocation;

    private long lastLocationUpdate;
    private static final long LOCATION_UPDATE_INTERVAL = 1000 * 60 * 5;

    public ReviewViewModel(UserPrincipalRepository userPrincipalRepository, UserRepository userRepository, LiveData<LocationSettingsResult> locationSettingsResult, LiveData<Location> location) {
        this.userPrincipalRepository = userPrincipalRepository;
        this.userRepository = userRepository;
        this.locationSettingsResult = locationSettingsResult;
        this.location = location;
        userToReview = new MutableLiveData<>();
        lastStoredLocation = userPrincipalRepository.getLastStoredLocation();
    }

    public LiveData<Resource<UserForView>> getUserToReview() {
        return userToReview;
    }

    public void loadNext() {
        userToReview.setValue(Resource.loading());
        if (isLocationUpdateRequired()) {
            updateLocation();
        } else {
            fetchNext();
        }
    }

    public LiveData<UserLocation> getLastStoredLocation() {
        return lastStoredLocation;
    }

    public LiveData<LocationSettingsResult> getLocationSettings() {
        return locationSettingsResult;
    }

    private void updateLocation() {
        location.observeForever(new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location loc) {
                saveLocation(loc);
                location.removeObserver(this);
            }

        });
    }

    private void onLocationUpdated() {
        fetchNext();
    }

    private boolean isLocationUpdateRequired() {
        return System.currentTimeMillis() - lastLocationUpdate > LOCATION_UPDATE_INTERVAL;
    }

    private void saveLocation(Location location) {
        userPrincipalRepository.saveLocation(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        lastLocationUpdate = System.currentTimeMillis();
                        onLocationUpdated();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);
                        userToReview.setValue(Resource.error(throwable));
                    }
                });
    }

    private void fetchNext() {
        userPrincipalRepository.getSearchParameters()
                .flatMapMaybe(new Function<UserSearchParameters, Maybe<UserForView>>() {
                    @Override
                    public Maybe<UserForView> apply(UserSearchParameters userSearchParameters) {
                        return userRepository.next(userSearchParameters);
                    }
                })
                .defaultIfEmpty(new UserForView())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<UserForView>() {
                    @Override
                    public void accept(UserForView user) {
                        userToReview.setValue(Resource.success(user));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) {
                        Log.e("Review", e.getMessage(), e);
                        userToReview.setValue(Resource.error(e));
                    }
                });
    }

    public void respondWithYes() {
        userRepository.respondWithYes(userToReview.getValue().getData().getReference())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> userToReview.setValue(Resource.loading()))
                .subscribe(onRespondCompleteAction);
    }

    public void respondWithNo() {
        userRepository.respondWithNo(userToReview.getValue().getData().getReference())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> userToReview.setValue(Resource.loading()))
                .subscribe(onRespondCompleteAction);
    }

    private Action onRespondCompleteAction = () -> loadNext();

    public void retry() {
        loadNext();
    }

}
