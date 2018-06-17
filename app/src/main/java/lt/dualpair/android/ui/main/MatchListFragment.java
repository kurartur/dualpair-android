package lt.dualpair.android.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.utils.ToastUtils;

public class MatchListFragment extends UserListFragment {

    private MatchListViewModel viewModel;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), new MatchListViewModel.Factory(getActivity().getApplication())).get(MatchListViewModel.class);
        subscribeUi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private void subscribeUi() {
        viewModel.getMatchList().observe(this, new Observer<List<UserListItem>>() {
            @Override
            public void onChanged(@Nullable List<UserListItem> items) {
                if (items.isEmpty()) {
                    showEmpty();
                } else {
                    matchesView.setAdapter(new UserListRecyclerAdapter<>(items, (UserListRecyclerAdapter.OnItemClickListener)getActivity()));
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
        disposable.add(
                viewModel.refresh()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onRefreshed, throwable -> {
                        onRefreshed();
                        ToastUtils.show(getActivity(), throwable.getMessage());
                    })
        );
    }

    @Override
    public String getActionBarTitle() {
        return getResources().getString(R.string.my_matches);
    }

    public static MatchListFragment newInstance() {
        return new MatchListFragment();
    }
}
