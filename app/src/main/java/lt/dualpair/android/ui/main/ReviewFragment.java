package lt.dualpair.android.ui.main;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.remote.services.ServiceException;
import lt.dualpair.android.data.resource.ErrorResponse;
import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.search.SearchParametersActivity;
import rx.Subscription;

public class ReviewFragment extends Fragment {

    private static final String TAG = "ReviewFragment";

    private Subscription nextMatchSubscription;
    Match match;
    ImageView[] dotImages;

    @Bind(R.id.review) LinearLayout reviewLayout;
    @Bind(R.id.photo_pager) ViewPager photoPager;
    @Bind(R.id.photo_dots) LinearLayout photoDots;
    @Bind(R.id.name_surname) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.location) TextView location;
    @Bind(R.id.sociotypes) TextView sociotypes;
    @Bind(R.id.description) TextView description;
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
        photoPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotImages.length; i++) {
                    dotImages[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
                }
                dotImages[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
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
        if (savedInstanceState == null) {
            loadReview();
        } else {
            Match match = (Match)savedInstanceState.getSerializable("MATCH");
            if (match != null) {
                renderReview(match);
            } else {
                loadReview();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        nextMatchSubscription.unsubscribe();
    }

    private void showViewLoading() {
        showLoading();
    }

    private void loadReview() {
        showViewLoading();
        nextMatchSubscription = new MatchDataManager(getActivity()).next(new EmptySubscriber<Match>() {
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
                    renderReview(match);
                } else {
                    showNoMatches();
                }
            }
        });
    }

    public void renderReview(Match match) {
        this.match = match;
        User user = match.getOpponent().getUser();
        name.setText(user.getName());
        age.setText(Integer.toString(user.getAge()));
        Location firstLocation = user.getFirstLocation();
        if (firstLocation != null) {
            location.setText(firstLocation.getCity());
        } else {
            location.setText("Unknown");
        }
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (Sociotype sociotype : user.getSociotypes()) {
            sb.append(prefix);
            prefix = ", ";
            sb.append(sociotype.getCode1());
        }
        sociotypes.setText(sb);
        description.setText(user.getDescription());
        progressLayout.setVisibility(View.GONE);
        initPhotos(match.getOpponent().getUser().getPhotos());
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

    private void initPhotos(List<Photo> photos) {
        photoPager.setAdapter(new UserPhotosAdapter(this.getActivity(), photos));
        photoDots.removeAllViews();
        dotImages = new ImageView[photos.size()];
        for (int i = 0; i < photos.size(); i++) {
            dotImages[i] = new ImageView(this.getActivity());
            if (i == 0) {
                dotImages[i].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
            } else {
                dotImages[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(7, 0, 7, 0);
            photoDots.addView(dotImages[i], params);
        }
    }

    private void setResponse(final Response response) {
        new MatchDataManager(getActivity()).setResponse(match.getId(), response);
        match = null;
        loadReview();
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
                startActivityForResult(SearchParametersActivity.createIntent(getActivity()), 1);
                break;
        }
        return false;
    }
}
