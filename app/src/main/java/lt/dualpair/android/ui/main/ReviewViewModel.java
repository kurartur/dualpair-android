package lt.dualpair.android.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationSettingsResult;

import java.io.IOException;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.data.resource.ErrorResponse;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.SearchParameters;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ReviewViewModel extends ViewModel {

    private static final String TAG = "ReviewViewModel";
    private final UserDataManager userDataManager;
    private final MatchDataManager matchDataManager;

    private MutableLiveData<Match> userToReview;

    private LiveData<LocationSettingsResult> locationSettingsResult;
    private LiveData<Location> location;

    private long lastLocationUpdate;
    private static final long LOCATION_UPDATE_INTERVAL = 1000 * 60 * 5;

    public ReviewViewModel(UserDataManager userDataManager, MatchDataManager matchDataManager, LiveData<LocationSettingsResult> locationSettingsResult, LiveData<Location> location) {
        this.userDataManager = userDataManager;
        this.matchDataManager = matchDataManager;
        this.locationSettingsResult = locationSettingsResult;
        this.location = location;
        userToReview = new MutableLiveData<>();
    }

    public LiveData<Match> getUserToReview() {
        return userToReview;
    }

    public void loadNext() {
        if (isLocationUpdateRequired()) {
            updateLocation();
        } else {
            fetchNext();
        }
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
        userDataManager.setLocation(lt.dualpair.android.data.resource.Location.fromAndroidLocation(location))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        lastLocationUpdate = System.currentTimeMillis();
                        onLocationUpdated();
                    }
                });
    }

    private void fetchNext() {
        userDataManager.getSearchParameters()
                .flatMap(new Func1<SearchParameters, Observable<Match>>() {
                    @Override
                    public Observable<Match> call(SearchParameters searchParameters) {
                        return matchDataManager.next(searchParameters);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onError(Throwable e) {
                        userToReview.setValue(new Match());
                        String error = "";
                        if (e instanceof ServiceException) {
                            ServiceException se = (ServiceException)e;
                            if (se.getResponse().code() != 404) {
                                try {
                                    error = se.getErrorBodyAs(ErrorResponse.class).getMessage();
                                } catch (IOException ioe) {
                                    error = ioe.getMessage();
                                }
                            }
                        } else {
                            error = e.getMessage();
                        }
                        Log.e("Review", error, e);
                    }

                    @Override
                    public void onNext(Match m) {
                        userToReview.setValue(m);
                    }
                });
    }

    private void onUserReviewed() {
        loadNext();
    }

    public void yes() {
        setResponse(Response.YES);
    }

    public void no() {
        setResponse(Response.NO);
    }

    private void setResponse(final Response response) {
        matchDataManager.setResponse(userToReview.getValue().getId(), response)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onNext(Match m) {
                        userToReview.setValue(null);
                        onUserReviewed();
                    }
                });
    }

    public void retry() {
        loadNext();
    }

}
