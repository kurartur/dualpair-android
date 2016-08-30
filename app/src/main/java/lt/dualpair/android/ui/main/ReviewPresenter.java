package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import lt.dualpair.android.core.match.GetNextMatchTask;
import lt.dualpair.android.resource.ErrorResponse;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.rx.EmptySubscriber;
import lt.dualpair.android.services.ServiceException;

public class ReviewPresenter implements Presenter {

    private static final String TAG = "ReviewPresenter";

    private ReviewView reviewView;
    private Activity activity;

    public ReviewPresenter(@NonNull ReviewView reviewView, Activity ctx) {
        this.reviewView = reviewView;
        activity = ctx;
    }

    public void initialize() {
        loadReview();
    }

    private void loadReview() {
        showViewLoading();
        getReview();
    }

    private void showViewLoading() {
        reviewView.showLoading();
    }

    public void getReview() {
        new GetNextMatchTask(activity).execute(new EmptySubscriber<Match>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ServiceException) {
                    ServiceException se = (ServiceException)e;
                    if (se.getResponse().code() == 404) {final Handler handler = new Handler();
                        reviewView.showNoMatches();

                    } else {reviewView.showNoMatches();
                        try {
                            reviewView.showLoadingError(se.getErrorBodyAs(ErrorResponse.class).getMessage());
                        } catch (IOException ioe) {
                            Log.e(TAG, "Error", ioe);
                            reviewView.showLoadingError(ioe.getMessage());
                        }
                    }
                } else {
                    Log.e(TAG, "Error", e);
                    reviewView.showLoadingError(e.getMessage());
                }
            }

            @Override
            public void onNext(final Match match) {
                reviewView.renderReview(match);
            }
        });
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
