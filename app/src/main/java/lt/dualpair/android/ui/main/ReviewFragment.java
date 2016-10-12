package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.data.resource.ErrorResponse;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.match.OpponentUserView;
import lt.dualpair.android.ui.match.ReviewHistoryActivity;
import lt.dualpair.android.ui.search.SearchParametersActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReviewFragment extends BaseFragment {

    private static final String TAG = "ReviewFragment";
    private static final int SP_REQ_CODE = 1;

    private Match match;

    private OpponentUserView opponentUserView;

    @Bind(R.id.review) LinearLayout reviewLayout;
    @Bind(R.id.no_button) Button noButton;
    @Bind(R.id.yes_button) Button yesButton;

    @Bind(R.id.progress_layout) LinearLayout progressLayout;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.progress_text) TextView progressText;
    @Bind(R.id.retry_button) Button retryButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            match = (Match)savedInstanceState.getSerializable("MATCH");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.review_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadReview();
            }
        });
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResponse(Response.YES);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResponse(Response.NO);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (opponentUserView == null) {
            opponentUserView = new OpponentUserView(this.getActivity(), getView());
        }
        if (match == null) {
            loadReview();
        } else {
            renderReview(match);
        }
    }

    private void showViewLoading() {
        showLoading();
    }

    private void loadReview() {
        showViewLoading();
        new MatchDataManager(getActivity()).next()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(this.<Match>bindToLifecycle())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ServiceException) {
                            ServiceException se = (ServiceException)e;
                            if (se.getResponse().code() == 404) {final Handler handler = new Handler();
                                showNoMatches();

                            } else {showNoMatches();
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

    public void renderReview(Match match) {
        opponentUserView.render(match.getOpponent().getUser());
        progressLayout.setVisibility(View.GONE);
        reviewLayout.setVisibility(View.VISIBLE);
    }

    public void showLoading() {
        progressText.setText(getResources().getString(R.string.loading) + "...");
        retryButton.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        reviewLayout.setVisibility(View.GONE);
    }

    public void showLoadingError(String text) {
        progressText.setText(text);
        progressText.setTextColor(Color.RED);
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }

    public void showNoMatches() {
        progressText.setText(getResources().getString(R.string.no_matches_found));
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }

    private void setResponse(final Response response) {
        new MatchDataManager(getActivity()).setResponse(match.getId(), response)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(this.<Match>bindToLifecycle())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onNext(Match m) {
                        match = null;
                        loadReview();
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("MATCH", match);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.review_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_parameters_menu_item:
                startActivityForResult(SearchParametersActivity.createIntent(getActivity()), SP_REQ_CODE);
                break;
            case R.id.history_menu_item:
                startActivity(ReviewHistoryActivity.createIntent(getActivity()));
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SP_REQ_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    loadReview();
                }
                break;
        }
    }
}
