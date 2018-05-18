package lt.dualpair.android.ui.main;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Match;

public class ReviewHistoryFragment extends UserListFragment {

    private ReviewHistoryViewModel viewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ReviewHistoryViewModel.Factory(getActivity().getApplication())).get(ReviewHistoryViewModel.class);
        subscribeUi();
    }

    private void subscribeUi() {
        viewModel.getReviewHistory().observe(this, new Observer<List<Match>>() {
            @Override
            public void onChanged(@Nullable List<Match> matches) {
                if (matches.isEmpty()) {
                    showEmpty();
                } else {
                    setAdapter(new MatchListRecyclerAdapter(matches));
                    showList();
                }
            }
        });
    }

    @Override
    protected String getEmptyViewText() {
        return getResources().getString(R.string.empty_history);
    }

    @Override
    protected void refresh() {
        viewModel.refresh();
    }
}
