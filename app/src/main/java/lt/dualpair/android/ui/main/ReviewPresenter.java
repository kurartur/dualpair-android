package lt.dualpair.android.ui.main;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.manager.SearchParametersManager;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.data.resource.ErrorResponse;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.user.SetLocationTask;
import lt.dualpair.android.utils.LocationUtil;
import lt.dualpair.android.utils.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReviewPresenter {

    private static final String TAG = "ReviewPresenter";

    private Match match;

    SearchParameters searchParameters;

    private List<NextMatchRequestValidator.Error> errors = new ArrayList<>();
    private String loadingError;
    private boolean noMatches;

    private ReviewFragment view;

    private LocationUtil locationUtil;

    private long lastLocationUpdate;
    private static final long LOCATION_UPDATE_INTERVAL = 1000 * 60 * 5;

    public ReviewPresenter(final Context context) {
        new SearchParametersManager(context).getSearchParameters()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<SearchParameters>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to load search parameters");
                        // TODO open search param activity?
                    }

                    @Override
                    public void onNext(SearchParameters sp) {
                        searchParameters = sp;
                        validate(context);
                    }
                });
    }

    private void validate(final Context context) {
        errors.clear();
        new NextMatchRequestValidator(context).validate()
                .subscribe(new EmptySubscriber<NextMatchRequestValidator.Error>() {
                    @Override
                    public void onCompleted() {
                        if (errors.isEmpty()) {
                            updateLocation();
                        } else {
                            publish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to validate", e);
                    }

                    @Override
                    public void onNext(NextMatchRequestValidator.Error error) {
                        errors.add(error);
                    }
                });
    }

    private void updateLocation() {
        if (System.currentTimeMillis() - lastLocationUpdate > LOCATION_UPDATE_INTERVAL) {
            view.showLoading(R.string.waiting_for_location);
            locationUtil.getLocation(new LocationUtil.LocationListener() {
                @Override
                public void onLocation(Location location) {
                    saveLocation(location);
                }
            });
        } else {
            fetchNextMatch();
        }
    }

    private void saveLocation(Location location) {
        new SetLocationTask(null, lt.dualpair.android.data.resource.Location.fromAndroidLocation(location)).execute(view.getActivity()) // TODO token null
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<Void>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to set location", e);
                        ToastUtils.show(view.getActivity(), "Unable to set location");
                    }

                    @Override
                    public void onCompleted() {
                        lastLocationUpdate = System.currentTimeMillis();
                        fetchNextMatch();
                    }
                });
    }

    private void fetchNextMatch() {
        new MatchDataManager(view.getActivity()).next()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ServiceException) {
                            ServiceException se = (ServiceException)e;
                            if (se.getResponse().code() == 404) {
                                view.showNoMatches();
                            } else {
                                view.showNoMatches();
                                try {
                                    loadingError = se.getErrorBodyAs(ErrorResponse.class).getMessage();
                                } catch (IOException ioe) {
                                    Log.e(TAG, "Error", ioe);
                                    loadingError = ioe.getMessage();
                                }
                            }
                        } else {
                            Log.e(TAG, "Error", e);
                            loadingError = e.getMessage();
                        }
                        publish();
                    }

                    @Override
                    public void onNext(Match m) {
                        if (m != null) {
                            match = m;
                        } else {
                            noMatches = true;
                        }
                        publish();
                    }
                });
    }

    public void onTakeView(ReviewFragment view) {
        this.view = view;
        publish();
    }

    private void publish() {
        if (view != null) {
            if (!errors.isEmpty()) {
                view.showValidationErrors(errors);
            } else if (!TextUtils.isEmpty(loadingError)) {
                view.showLoadingError(loadingError);
            } else if (noMatches) {
                view.showNoMatches();
            } else if (match != null) {
                view.renderReview(match);
            }
        }
    }

    public void updateSearchParameters(SearchParameters sp) {
        searchParameters = sp;
        match = null;
        if (view != null) {
            validate(view.getActivity());
        }
    }

    public void yes() {
        setResponse(Response.YES);
    }

    public void no() {
        setResponse(Response.NO);
    }

    private void setResponse(final Response response) {
        view.showLoading();
        new MatchDataManager(view.getActivity()).setResponse(match.getId(), response)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onNext(Match m) {
                        match = null;
                        validate(view.getActivity());
                    }
                });
    }

    public void retry() {
        validate(view.getActivity());
    }

    public void onSave(Bundle outState) {}

    public void setLocationUtil(LocationUtil locationUtil) {
        this.locationUtil = locationUtil;
    }

}
