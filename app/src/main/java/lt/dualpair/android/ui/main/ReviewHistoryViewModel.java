package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

import lt.dualpair.android.data.local.entity.History;
import lt.dualpair.android.data.repository.ReviewHistoryRepository;

public class ReviewHistoryViewModel extends ViewModel {

    private final LiveData<List<History>> reviewHistory;
    private ReviewHistoryRepository reviewHistoryRepository;

    public ReviewHistoryViewModel(ReviewHistoryRepository reviewHistoryRepository) {
        this.reviewHistoryRepository = reviewHistoryRepository;
        reviewHistory = reviewHistoryRepository.getHistory();
    }

    public LiveData<List<History>> getReviewHistory() {
        return reviewHistory;
    }

    public void refresh() {
        reviewHistoryRepository.reload();
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
                return (T) new ReviewHistoryViewModel(new ReviewHistoryRepository(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
