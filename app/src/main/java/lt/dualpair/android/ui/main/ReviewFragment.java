package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.core.match.SetResponseTask;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.resource.Photo;
import lt.dualpair.android.resource.Response;
import lt.dualpair.android.resource.Sociotype;
import lt.dualpair.android.resource.User;
import lt.dualpair.android.rx.EmptySubscriber;
import lt.dualpair.android.ui.search.SearchParametersActivity;

public class ReviewFragment extends Fragment implements ReviewView, LoaderManager.LoaderCallbacks {

    private static final String TAG = "ReviewFragment";

    ReviewPresenter reviewPresenter;
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
                sendResponse(Response.YES);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResponse(Response.NO);
            }
        });
        reviewPresenter = new ReviewPresenter(this, this.getActivity());
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

    private void loadReview() {
        if (reviewPresenter != null) {
            reviewPresenter.initialize();
        }
    }

    @Override
    public void renderReview(Match match) {
        this.match = match;
        User user = match.getOpponent().getUser();
        name.setText(user.getName());
        age.setText(Integer.toString(user.getAge()));
        location.setText(user.getLocations().iterator().next().getCity());
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

    @Override
    public void showLoading() {
        progressText.setText(getResources().getString(R.string.loading) + "...");
        retryButton.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        reviewLayout.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingError(String text) {
        progressText.setText(text);
        progressText.setTextColor(Color.RED);
        progressBar.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }

    @Override
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

    private void sendResponse(final Response response) {
        final Activity activity = getActivity();
        new SetResponseTask(activity, match.getUser().getId(), response).execute(new EmptySubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to send response", e);
            }

            @Override
            public void onNext(Void v) {
                ReviewFragment.this.match = null;
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
                startActivityForResult(SearchParametersActivity.createIntent(getActivity()), 1);
                break;
        }
        return false;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
