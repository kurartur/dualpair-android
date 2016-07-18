package com.artur.dualpair.android.ui.main;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.artur.dualpair.android.core.match.GetNextMatchTask;
import com.artur.dualpair.android.dto.Match;
import com.artur.dualpair.android.rx.EmptySubscriber;
import com.artur.dualpair.android.utils.ToastUtils;

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

    private void hideViewLoading() {
        reviewView.hideLoading();
    }

    public void getReview() {
        new GetNextMatchTask(activity).execute(new EmptySubscriber<Match>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Couldn't fetch next match", e);
                ToastUtils.show(activity, "Couldn't fetch next match");
                hideViewLoading();
            }

            @Override
            public void onNext(Match match) {
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
