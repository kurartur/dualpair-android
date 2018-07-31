package lt.dualpair.android.ui.main;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserListItem;

public class ReviewedUserListFragment extends UserListFragment {

    private ReviewHistoryViewModel viewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(getActivity(), new ReviewHistoryViewModel.Factory(getActivity().getApplication())).get(ReviewHistoryViewModel.class);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.history);
    }

    @Override
    protected String getEmptyViewText() {
        return getResources().getString(R.string.empty_history);
    }

    @SuppressLint("CheckResult")
    @Override
    protected Completable getRefreshCompletable() {
        return viewModel.refresh();
    }

    @Override
    protected Flowable<List<UserListItem>> getItemsFlowable() {
        return viewModel.getReviewedUsers();
    }
}
