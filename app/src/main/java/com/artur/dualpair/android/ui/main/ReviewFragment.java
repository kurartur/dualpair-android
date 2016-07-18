package com.artur.dualpair.android.ui.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.artur.dualpair.android.R;
import com.artur.dualpair.android.dto.Match;
import com.artur.dualpair.android.dto.Sociotype;
import com.artur.dualpair.android.dto.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReviewFragment extends Fragment implements ReviewView {

    ReviewPresenter reviewPresenter;

    @Bind(R.id.name_surname) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.location) TextView location;
    @Bind(R.id.sociotypes) TextView sociotypes;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.progress) RelativeLayout progress;
    @Bind(R.id.profile_picture) ImageView profilePicture;
    @Bind(R.id.review) LinearLayout reviewLayout;

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
        reviewPresenter = new ReviewPresenter(this, this.getActivity());
        if (savedInstanceState == null) {
            loadReview();
        }
    }

    private void loadReview() {
        if (reviewPresenter != null) {
            reviewPresenter.initialize();
        }
    }

    @Override
    public void renderReview(Match match) {
        User user = match.getOpponent();
        name.setText(user.getName());
        age.setText(Integer.toString(user.getAge()));
        //location.setText(user.getLocation());
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (Sociotype sociotype : user.getSociotypes()) {
            sb.append(prefix);
            prefix = ", ";
            sb.append(sociotype.getCode1());
        }
        sociotypes.setText(sb);
        //description.setText(user.getDescription());
        hideLoading();
        reviewLayout.setVisibility(View.VISIBLE);
        //Picasso.with(this.getActivity()).load(user.getProfilePictureLinks().iterator().next()).into(profilePicture);
    }

    @Override
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        getActivity().setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void hideLoading() {
        progress.setVisibility(View.GONE);
        getActivity().setProgressBarIndeterminateVisibility(false);
    }
}
