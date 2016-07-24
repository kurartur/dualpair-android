package com.artur.dualpair.android.ui.main;

import com.artur.dualpair.android.dto.Match;

public interface ReviewView {

    void renderReview(Match user);

    void showLoading();

    void showLoadingError(String text);

    void showNoMatches();
}
