package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.data.repository.MatchRepository;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class MatchListViewModel extends ViewModel {

    private MatchRepository matchRepository;

    private final LiveData<List<UserListItem>> matchList;

    public MatchListViewModel(MatchRepository matchRepository, UserPrincipalRepository userPrincipalRepository) {
        this.matchRepository = matchRepository;
        matchList = LiveDataReactiveStreams.fromPublisher(matchRepository.getMatches());
    }

    public LiveData<List<UserListItem>> getMatchList() {
        return matchList;
    }

    public Completable refresh() {
        return matchRepository.loadMatchesFromApi();
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
                return (T) new MatchListViewModel(new MatchRepository(application), new UserPrincipalRepository(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
