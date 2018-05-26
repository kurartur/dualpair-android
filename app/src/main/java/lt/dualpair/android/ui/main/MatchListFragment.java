package lt.dualpair.android.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.Match;

public class MatchListFragment extends UserListFragment {

    private MatchListViewModel viewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new MatchListViewModel.Factory(getActivity().getApplication())).get(MatchListViewModel.class);
        subscribeUi();
    }

    private void subscribeUi() {
        viewModel.getMatchList().observe(this, new Observer<List<Match>>() {
            @Override
            public void onChanged(@Nullable List<Match> matches) {
                if (matches.isEmpty()) {
                    setAdapter(new MatchListRecyclerAdapter());
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
        return getResources().getString(R.string.you_have_no_matches);
    }

    @Override
    protected void refresh() {
        viewModel.refresh();
    }

    @Override
    protected String getActionBarTitle() {
        return getResources().getString(R.string.my_matches);
    }
}
