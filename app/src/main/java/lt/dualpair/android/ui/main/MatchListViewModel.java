package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import lt.dualpair.android.data.local.entity.MatchForListView;
import lt.dualpair.android.data.repository.MatchRepository;

public class MatchListViewModel extends ViewModel {

    private MatchRepository matchRepository;

    private final LiveData<List<MatchForListView>> matchList;

    public MatchListViewModel(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
        matchList = LiveDataReactiveStreams.fromPublisher(matchRepository.getMatches());
    }

    public LiveData<List<MatchForListView>> getMatchList() {
        return matchList;
    }

    public Completable refresh() {
        return matchRepository.loadMatchesFromApi()
                .debounce(400, TimeUnit.MILLISECONDS)
                .ignoreElements();
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
                return (T) new MatchListViewModel(new MatchRepository(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
