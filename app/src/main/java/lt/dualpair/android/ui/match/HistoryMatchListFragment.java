package lt.dualpair.android.ui.match;


import lt.dualpair.android.R;

public class HistoryMatchListFragment extends MatchListFragment {

    private static HistoryMatchListPresenter presenter;

    @Override
    protected String getEmptyViewText() {
        return getResources().getString(R.string.empty_history);
    }

    @Override
    protected void createPresenter() {
        presenter = new HistoryMatchListPresenter(getActivity(), new MatchListRecyclerAdapter());
    }

    @Override
    protected void destroyPresenter() {
        presenter = null;
    }

    @Override
    protected MatchListPresenter getPresenter() {
        return presenter;
    }
}
