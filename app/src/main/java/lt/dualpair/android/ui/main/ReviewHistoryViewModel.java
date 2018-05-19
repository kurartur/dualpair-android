package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class ReviewHistoryViewModel extends ViewModel {

    private MatchDataManager matchDataManager;
    private final MutableLiveData<List<Match>> reviewHistory;

    public ReviewHistoryViewModel(MatchDataManager matchDataManager) {
        this.matchDataManager = matchDataManager;
        reviewHistory = new MutableLiveData<>();
        loadHistory();
    }

    private void loadHistory() {
        // TODO pagination
        final List<Match> history = new ArrayList<>();
        matchDataManager.historyMatches(0, 1000)
                .collect(new Func0<List<Match>>() {
                    @Override
                    public List<Match> call() {
                        return history;
                    }
                }, new Action2<List<Match>, Match>() {
                    @Override
                    public void call(List<Match> matches, Match match) {
                        matches.add(match);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Match>>() {
                    @Override
                    public void call(List<Match> matches) {
                        reviewHistory.setValue(matches);
                    }
                });
    }

    public LiveData<List<Match>> getReviewHistory() {
        return reviewHistory;
    }

    public void refresh() {
        loadHistory();
    }

    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {

        private Application application;
        public Factory(@NonNull Application application) {
            super(application);
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ReviewHistoryViewModel.class)) {
                return (T) new ReviewHistoryViewModel(new MatchDataManager(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
