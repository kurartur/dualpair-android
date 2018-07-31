package lt.dualpair.android.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserListItem;

public class MatchListFragment extends UserListFragment {

    private MatchListViewModel viewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity(), new MatchListViewModel.Factory(getActivity().getApplication())).get(MatchListViewModel.class);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected Flowable<List<UserListItem>> getItemsFlowable() {
        return viewModel.getMatchList();
    }

    @Override
    protected String getEmptyViewText() {
        return getResources().getString(R.string.you_have_no_matches);
    }

    @Override
    protected Completable getRefreshCompletable() {
        return viewModel.refresh();
    }

    @Override
    public String getActionBarTitle() {
        return getResources().getString(R.string.my_matches);
    }

    public static MatchListFragment newInstance() {
        return new MatchListFragment();
    }
}
