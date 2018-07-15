package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.data.repository.ResponseRepository;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class ReviewHistoryViewModel extends ViewModel {

    private final Flowable<List<UserListItem>> reviewedUsers;
    private ResponseRepository responseRepository;

    public ReviewHistoryViewModel(ResponseRepository responseRepository, UserPrincipalRepository userPrincipalRepository) {
        this.responseRepository = responseRepository;
        reviewedUsers = responseRepository.getReviewedUsers();

        refresh()
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {}, e -> {
                    Log.e(ReviewHistoryViewModel.class.getName(), e.getMessage(), e);
                });
    }

    public Flowable<List<UserListItem>> getReviewedUsers() {
        return reviewedUsers;
    }

    public Completable refresh() {
        return responseRepository.loadFromApi();
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
                return (T) new ReviewHistoryViewModel(new ResponseRepository(application), new UserPrincipalRepository(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
