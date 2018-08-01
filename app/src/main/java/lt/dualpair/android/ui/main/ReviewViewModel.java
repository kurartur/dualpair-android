package lt.dualpair.android.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import io.reactivex.Completable;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.repository.ResponseRepository;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class ReviewViewModel extends ViewModel {

    private static final String TAG = ReviewViewModel.class.getName();

    private final ResponseRepository responseRepository;
    private final LiveData<UserLocation> lastStoredLocation;

    public ReviewViewModel(UserPrincipalRepository userPrincipalRepository,
                           ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
        lastStoredLocation = userPrincipalRepository.getLastStoredLocation();
    }

    public LiveData<UserLocation> getLastStoredLocation() {
        return lastStoredLocation;
    }


    public Completable respondWithYes(Long userId) {
        return responseRepository.respondWithYes(userId);
    }

    public Completable respondWithNo(Long userId) {
        return responseRepository.respondWithNo(userId);
    }

}
