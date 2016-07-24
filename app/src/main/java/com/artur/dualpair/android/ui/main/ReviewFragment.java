package com.artur.dualpair.android.ui.main;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.artur.dualpair.android.R;
import com.artur.dualpair.android.dto.Match;
import com.artur.dualpair.android.dto.Photo;
import com.artur.dualpair.android.dto.Sociotype;
import com.artur.dualpair.android.dto.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReviewFragment extends Fragment implements ReviewView {

    private static final String TAG = "ReviewFragment";

    ReviewPresenter reviewPresenter;
    Match match;

    @Bind(R.id.review) LinearLayout reviewLayout;
    @Bind(R.id.photo_pager) ViewPager photoPager;
    @Bind(R.id.name_surname) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.location) TextView location;
    @Bind(R.id.sociotypes) TextView sociotypes;
    @Bind(R.id.description) TextView description;

    @Bind(R.id.progress_layout) LinearLayout progressLayout;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.progress_text) TextView progressText;
    @Bind(R.id.retry_button) Button retryButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_review, container, false);
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
        User user = match.getOpponent();
        name.setText(user.getName());
        age.setText(Integer.toString(user.getAge()));
        location.setText(user.getLocation().getCity());
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
        initPhotos(match.getOpponent().getPhotos());
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("MATCH", match);
        super.onSaveInstanceState(outState);
    }
}
