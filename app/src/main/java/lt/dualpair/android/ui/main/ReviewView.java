package lt.dualpair.android.ui.main;

import lt.dualpair.android.resource.Match;

public interface ReviewView {

    void renderReview(Match user);

    void showLoading();

    void showLoadingError(String text);

    void showNoMatches();
}
