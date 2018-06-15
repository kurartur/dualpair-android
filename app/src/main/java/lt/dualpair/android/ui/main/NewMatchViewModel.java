package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import io.reactivex.Flowable;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.repository.UserRepository;

public class NewMatchViewModel extends ViewModel {

    private final Flowable<UserForView> user;

    public NewMatchViewModel(UserRepository userRepository, Long matchId) {
        user = userRepository.getUserByMatchId(matchId).toFlowable();
    }

    public Flowable<UserForView> getUser() {
        return user;
    }

    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {

        private Application application;
        private Long reference;

        public Factory(@NonNull Application application, Long reference) {
            super(application);
            this.application = application;
            this.reference = reference;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(NewMatchViewModel.class)) {
                return (T) new NewMatchViewModel(new UserRepository(application), reference);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
