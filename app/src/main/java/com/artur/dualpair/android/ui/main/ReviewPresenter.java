package com.artur.dualpair.android.ui.main;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.artur.dualpair.android.core.match.GetNextMatchTask;
import com.artur.dualpair.android.dto.ErrorResponse;
import com.artur.dualpair.android.dto.Match;
import com.artur.dualpair.android.rx.EmptySubscriber;
import com.artur.dualpair.android.services.ServiceException;

import java.io.IOException;

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
