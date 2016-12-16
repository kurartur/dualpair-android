package lt.dualpair.android.ui.match;

import android.content.Context;
import android.os.Bundle;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.resource.Match;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class MatchListPresenter {

    protected final MatchListRecyclerAdapter adapter;

    private MatchListFragment view;

    public MatchListPresenter(Context context, MatchListRecyclerAdapter adpter) {
        adapter = adpter;
        fetch(context, 0, 10);

    }

    public void refresh(Context context) {
        adapter.clear();
        fetch(context, 0, 10);
    }

    protected abstract Observable<Match> observable(Context ctx, int start, int count);

    private void fetch(Context context, int start, int count) {
        observable(context, start, count)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onCompleted() {
                        view.stopRefreshing();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(Match match) {
                        adapter.append(match);
                        adapter.notifyDataSetChanged();
                        view.stopRefreshing();
                        publish();
                    }
                });
    }

    public void onTakeView(MatchListFragment v) {
        view = v;
        setAdapter();
        publish();
    }

    private void setAdapter() {
        if (view != null) {
            view.setAdapter(adapter);
        }
    }

    private void publish() {
        if (view != null) {
            if (adapter.getItemCount() == 0) {
                view.showEmpty();
            } else {
                view.showList();
            }
        }
    }

    public void onSave(Bundle outState) {

    }
}
