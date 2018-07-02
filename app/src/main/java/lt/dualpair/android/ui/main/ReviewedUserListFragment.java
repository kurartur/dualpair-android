package lt.dualpair.android.ui.main;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.ui.UserFriendlyErrorConsumer;

public class ReviewedUserListFragment extends UserListFragment {

    private ReviewHistoryViewModel viewModel;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), new ReviewHistoryViewModel.Factory(getActivity().getApplication())).get(ReviewHistoryViewModel.class);
        subscribeUi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.history);
    }

    @SuppressLint("CheckResult")
    private void subscribeUi() {
        viewModel.getReviewedUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(reviewedUsers -> {
                    if (reviewedUsers.isEmpty()) {
                        showEmpty();
                    } else {
                        matchesView.setAdapter(new UserListRecyclerAdapter(reviewedUsers, (UserListRecyclerAdapter.OnItemClickListener)getActivity()));
                        showList();
                    }
                });
    }

    @Override
    protected String getEmptyViewText() {
        return getResources().getString(R.string.empty_history);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void refresh() {
        disposable.add(
            viewModel.refresh()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(this::onRefreshed, new UserFriendlyErrorConsumer(this, t -> onRefreshed()))
        );
    }
}
