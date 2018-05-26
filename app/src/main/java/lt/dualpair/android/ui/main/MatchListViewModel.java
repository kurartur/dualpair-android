package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.local.entity.Match;
import lt.dualpair.android.data.repository.MatchRepository;

public class MatchListViewModel extends ViewModel {

    private MatchRepository matchRepository;

    private final MutableLiveData<List<Match>> matchList;

    public MatchListViewModel(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
        matchList = new MutableLiveData<>();
        loadMatches();
    }

    private void loadMatches() {
        matchRepository.getMatches()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Consumer<List<lt.dualpair.android.data.local.entity.Match>>() {
                @Override
                public void accept(List<lt.dualpair.android.data.local.entity.Match> matches) {
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
                return (T) new MatchListViewModel(new MatchRepository(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
