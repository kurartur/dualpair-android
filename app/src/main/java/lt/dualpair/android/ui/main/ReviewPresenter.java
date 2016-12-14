package lt.dualpair.android.ui.main;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.data.resource.ErrorResponse;
import lt.dualpair.android.data.resource.Match;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReviewPresenter {

    private boolean searchMale;
    private boolean searchFemale;
    private int minAge;
    private int maxAge;

    private ReviewFragment view;



    private void validateAndLoadReview() {
        showViewLoading();
        final List<NextMatchRequestValidator.Error> errors = new ArrayList<>();
        new NextMatchRequestValidator(getActivity()).validate()
                .subscribe(new EmptySubscriber<NextMatchRequestValidator.Error>() {
                    @Override
                    public void onCompleted() {
                        if (errors.isEmpty()) {
                            loadReview();
                        } else {
                            showValidationErrors(errors);
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

    private void loadReview() {
        new MatchDataManager(view.getActivity()).next()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ServiceException) {
                            ServiceException se = (ServiceException)e;
                            if (se.getResponse().code() == 404) {
                                showNoMatches();
                            } else {
                                showNoMatches();
                                try {
                                    showLoadingError(se.getErrorBodyAs(ErrorResponse.class).getMessage());
                                } catch (IOException ioe) {
                                    Log.e(TAG, "Error", ioe);
                                    showLoadingError(ioe.getMessage());
                                }
                            }
                        } else {
                            Log.e(TAG, "Error", e);
                            showLoadingError(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Match match) {
                        if (match != null) {
                            ReviewFragment.this.match = match;
                            renderReview(match);
                        } else {
                            showNoMatches();
                        }
                    }
                });
    }

    public void onTakeView(ReviewFragment view) {
        this.view = view;
        publish();
    }

    private void publish() {
        if (view != null) {
            if (error == null) {
                view.render(searchMale, searchFemale, minAge, maxAge);
            } else {
                view.render(error);
            }
        }
    }

    public void updateSearchParameters(boolean searchMale, boolean searchFemale, int minAge, int maxAge) {
        this.searchMale = searchMale;
        this.searchFemale = searchFemale;
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

}
