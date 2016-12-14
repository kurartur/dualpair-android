package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.match.OpponentUserView;
import lt.dualpair.android.ui.match.ReviewHistoryActivity;
import lt.dualpair.android.ui.search.SearchParametersActivity;
import lt.dualpair.android.ui.user.AddSociotypeActivity;
import lt.dualpair.android.ui.user.SetDateOfBirthActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReviewFragment extends BaseFragment {

    private static final String TAG = "ReviewFragment";
    private static final int SP_REQ_CODE = 1;
    private static final int ADD_SOCIOTYPE_REQUEST_CODE = 2;
    private static final int SET_BIRTHDAY_REQUEST_CODE = 3;

    private Match match;

    private OpponentUserView opponentUserView;

    @Bind(R.id.review) LinearLayout reviewLayout;
    @Bind(R.id.no_button) Button noButton;
    @Bind(R.id.yes_button) Button yesButton;

    @Bind(R.id.progress_layout) LinearLayout progressLayout;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.progress_text) TextView progressText;
    @Bind(R.id.retry_button) Button retryButton;

    @Bind(R.id.validation_layout) View validationLayout;
    @Bind(R.id.provide_sociotype) View provideSociotype;
    @Bind(R.id.provide_date_of_birth) View provideDateOfBirth;
    @Bind(R.id.provide_search_parameters) View provideSearchParameters;

    private static ReviewPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        if (presenter == null) {
            presenter = new ReviewPresenter();
        }
        presenter.onTakeView(this);

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

    @OnClick(R.id.retry_button) void onRetryClick() {
        presenter.retry();
    }

    @OnClick(R.id.yes_button) void onYesClick() {
        presenter.yes();
    }

    @OnClick(R.id.no_button) void onNoClick() {
        presenter.no();
    }

    @OnClick(R.id.provide_sociotype) void onProvideSociotypeClick() {
        startActivityForResult(AddSociotypeActivity.createIntent(getActivity()), ADD_SOCIOTYPE_REQUEST_CODE);
    }

    @OnClick(R.id.provide_date_of_birth) void onProvideDateOfBirthClick() {
        startActivityForResult(SetDateOfBirthActivity.createIntent(getActivity()), SET_BIRTHDAY_REQUEST_CODE);
    }

    @OnClick(R.id.provide_search_parameters) void onProvideSearchParametersClick() {
        startActivityForResult(SearchParametersActivity.createIntent(getActivity()), SP_REQ_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (opponentUserView == null) {
            opponentUserView = new OpponentUserView(this.getActivity(), getView());
        }
        if (match == null) {
            validateAndLoadReview();
        } else {
            renderReview(match);
        }
    }

    private void showViewLoading() {
        showLoading();
    }



    private void renderReview(Match match) {
        opponentUserView.render(match.getOpponent().getUser());
        progressLayout.setVisibility(View.GONE);
        reviewLayout.setVisibility(View.VISIBLE);
        validationLayout.setVisibility(View.GONE);
    }

    private void showLoading() {
        progressText.setText(getResources().getString(R.string.loading) + "...");
        retryButton.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        reviewLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.GONE);
        validationLayout.setVisibility(View.GONE);
    }

    private void showLoadingError(String text) {
        progressLayout.setVisibility(View.VISIBLE);
        validationLayout.setVisibility(View.GONE);
        progressText.setText(text);
        progressText.setTextColor(Color.RED);
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }

    private void showNoMatches() {
        progressLayout.setVisibility(View.VISIBLE);
        validationLayout.setVisibility(View.GONE);
        progressText.setText(getResources().getString(R.string.no_matches_found));
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }

    public void showValidationErrors(List<NextMatchRequestValidator.Error> errors) {
        validationLayout.setVisibility(View.VISIBLE);
        reviewLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.GONE);
        provideSociotype.setVisibility(View.GONE);
        provideDateOfBirth.setVisibility(View.GONE);
        provideSearchParameters.setVisibility(View.GONE);
        for (NextMatchRequestValidator.Error error : errors) {
            switch (error) {
                case NO_SOCIOTYPE:
                    provideSociotype.setVisibility(View.VISIBLE);
                    break;
                case NO_DATE_OF_BIRTH:
                    provideDateOfBirth.setVisibility(View.VISIBLE);
                    break;
                case NO_SEARCH_PARAMETERS:
                    provideSearchParameters.setVisibility(View.VISIBLE);
                    break;
            }
        }
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
                        validateAndLoadReview();
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
                    presenter.updateSearchParameters(
                            data.getBooleanExtra("SEARCH_MALE", false),
                            data.getBooleanExtra("SEARCH_FEMALE", false),
                            data.getIntExtra("MIN_AGE", 0),
                            data.getIntExtra("MAX_AGE", 0)
                    );
                    // TODO
                }
                break;
            case ADD_SOCIOTYPE_REQUEST_CODE:
            case SET_BIRTHDAY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    validateAndLoadReview();
                }
                break;
        }
    }

}
