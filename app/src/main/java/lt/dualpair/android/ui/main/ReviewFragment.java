package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.match.ReviewHistoryActivity;
import lt.dualpair.android.ui.search.SearchParametersActivity;
import lt.dualpair.android.ui.user.AddSociotypeActivity;
import lt.dualpair.android.ui.user.OpponentUserView;
import lt.dualpair.android.ui.user.SetDateOfBirthActivity;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.LocationUtil;

public class ReviewFragment extends MainTabFragment {

    private static final String TAG = "ReviewFragment";

    private static final int SP_REQ_CODE = 1;
    private static final int ADD_SOCIOTYPE_REQUEST_CODE = 2;
    private static final int SET_BIRTHDAY_REQUEST_CODE = 3;

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final int RESOLUTION_FOR_RESULT_REQ_CODE = 4;

    @Bind(R.id.review) LinearLayout reviewLayout;

    @Bind(R.id.progress_layout) LinearLayout progressLayout;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.progress_text) TextView progressText;
    @Bind(R.id.retry_button) Button retryButton;

    @Bind(R.id.validation_layout) View validationLayout;
    @Bind(R.id.provide_sociotype) View provideSociotype;
    @Bind(R.id.provide_date_of_birth) View provideDateOfBirth;
    @Bind(R.id.provide_search_parameters) View provideSearchParameters;

    @Bind(R.id.opponent_user_view) OpponentUserView opponentUserView;

    private ActionBarViewHolder actionBarViewHolder;

    private static ReviewPresenter presenter;

    private LocationUtil locationUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.review_layout, container, false);
        ButterKnife.bind(this, view);

        showLoading();
        actionBarViewHolder = new ActionBarViewHolder();
        actionBarViewHolder.actionBarView = getLayoutInflater(savedInstanceState).inflate(R.layout.review_action_bar_layout, null);
        ButterKnife.bind(actionBarViewHolder, actionBarViewHolder.actionBarView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        opponentUserView.setPhotoOverlay(R.layout.review_buttons);
        ButterKnife.findById(opponentUserView, R.id.yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.yes();
            }
        });
        ButterKnife.findById(opponentUserView, R.id.no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.no();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (presenter == null) {
            presenter = new ReviewPresenter(getActivity());
        }

        locationUtil = new LocationUtil(this, PERMISSIONS_REQUEST_CODE, RESOLUTION_FOR_RESULT_REQ_CODE);
        presenter.setLocationUtil(locationUtil);

        locationUtil.connect();
        presenter.onTakeView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) {
            presenter.onTakeView(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSave(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationUtil.disconnect();
        presenter.onTakeView(null);
        presenter = null;
    }

    @OnClick(R.id.retry_button) void onRetryClick() {
        presenter.retry();
    }

    /*@OnClick(R.id.yes_button) void onYesClick() {
        presenter.yes();
    }

    @OnClick(R.id.no_button) void onNoClick() {
        presenter.no();
    }*/

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
    protected View getActionBarView() {
        return actionBarViewHolder.actionBarView;
    }

    public void renderReview(Match match) {
        User opponentUser = match.getOpponent().getUser();
        progressLayout.setVisibility(View.GONE);
        reviewLayout.setVisibility(View.VISIBLE);
        validationLayout.setVisibility(View.GONE);

        actionBarViewHolder.name.setText(opponentUser.getName());
        actionBarViewHolder.age.setText(getString(R.string.review_age, opponentUser.getAge()));
        Location location = opponentUser.getFirstLocation();
        if (location != null) {
            actionBarViewHolder.city.setText(getString(R.string.review_city, location.getCity(), match.getDistance() / 1000));
        }
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (Sociotype sociotype : opponentUser.getSociotypes()) {
            sb.append(prefix);
            prefix = ", ";
            String code = sociotype.getCode1();
            int titleId = getResources().getIdentifier(code.toLowerCase() + "_title", "string", getActivity().getPackageName());
            sb.append(getString(titleId) + " (" + sociotype.getCode1() + ")");
        }
        opponentUserView.setUser(opponentUser);
    }

    public void showLoading() {
        showLoading(R.string.loading);
    }

    public void showLoading(int loadingText) {
        progressText.setText(loadingText);
        retryButton.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        reviewLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        validationLayout.setVisibility(View.GONE);
    }

    public void showLoadingError(String text) {
        progressLayout.setVisibility(View.VISIBLE);
        validationLayout.setVisibility(View.GONE);
        progressText.setText(text);
        progressText.setTextColor(Color.RED);
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }

    public void showNoMatches() {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.review_fragment_menu, menu);
        for(int i=0 ; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            DrawableUtils.setActionBarIconColorFilter(getActivity(), menuItem.getIcon());
        }
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
                    presenter.updateSearchParameters((SearchParameters)data.getBundleExtra(SearchParametersActivity.RESULT_BUNDLE_KEY)
                            .getSerializable(SearchParametersActivity.SEARCH_PARAMETERS_KEY)
                    );
                }
                break;
            case ADD_SOCIOTYPE_REQUEST_CODE:
            case SET_BIRTHDAY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    presenter.retry();
                }
                break;
            case RESOLUTION_FOR_RESULT_REQ_CODE:
                locationUtil.onResolutionForResultResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                locationUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    protected static class ActionBarViewHolder {

        View actionBarView;

        @Bind(R.id.name) TextView name;
        @Bind(R.id.age) TextView age;
        @Bind(R.id.city) TextView city;

    }

}
