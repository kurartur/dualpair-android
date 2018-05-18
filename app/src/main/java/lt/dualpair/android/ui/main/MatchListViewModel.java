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

public class MatchListViewModel extends ViewModel {

    private MatchDataManager matchDataManager;

    private final MutableLiveData<List<Match>> matchList;

    private static final int MATCH_COUNT = 1000; // TODO pagination
    
    public MatchListViewModel(MatchDataManager matchDataManager) {
        this.matchDataManager = matchDataManager;
        matchList = new MutableLiveData<>();
        loadMatches();
    }

    private void loadMatches() {
        final List<Match> matches = new ArrayList<>();
        matchDataManager.mutualMatches(0, MATCH_COUNT)
            .collect(new Func0<List<Match>>() {
                @Override
                public List<Match> call() {
                    return matches;
                }
            }, new Action2<List<Match>, Match>() {
                @Override
                public void call(List<Match> matches, Match match) {
                    matches.add(match);
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Match>>() {
                @Override
                public void call(List<Match> matches) {
                    matchList.setValue(matches);
                }
        });
    }

    public LiveData<List<Match>> getMatchList() {
        return matchList;
    }

    public void refresh() {
        loadMatches();
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
            if (modelClass.isAssignableFrom(MatchListViewModel.class)) {
                return (T) new MatchListViewModel(new MatchDataManager(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
