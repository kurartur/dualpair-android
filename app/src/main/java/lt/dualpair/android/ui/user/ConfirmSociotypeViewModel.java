package lt.dualpair.android.ui.user;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.local.entity.UserSociotype;
import lt.dualpair.android.data.repository.SociotypeRepository;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class ConfirmSociotypeViewModel extends ViewModel {

    private final MediatorLiveData<List<UserSociotype>> currentSociotypes;
    private UserPrincipalRepository userPrincipalRepository;
    private SociotypeRepository sociotypeRepository;
    private CompositeDisposable disposable = new CompositeDisposable();

    public ConfirmSociotypeViewModel(UserPrincipalRepository userPrincipalRepository, SociotypeRepository sociotypeRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
        this.sociotypeRepository = sociotypeRepository;
        currentSociotypes = new MediatorLiveData<>();
        currentSociotypes.setValue(null);
        disposable.add(
                userPrincipalRepository.getSociotypes()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(currentSociotypes::setValue));
    }

    public Completable saveSociotype(String sociotypeCode) {
        return userPrincipalRepository.setSociotype(sociotypeCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public LiveData<Sociotype> getSociotype(String code1) {
        return sociotypeRepository.getSociotype(code1);
    }

    @Override
    protected void onCleared() {
        disposable.clear();
    }

    public static class ConfirmSociotypeViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

        private Application application;

        public ConfirmSociotypeViewModelFactory(@NonNull Application application) {
            super(application);
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ConfirmSociotypeViewModel.class)) {
                return (T) new ConfirmSociotypeViewModel(new UserPrincipalRepository(application), new SociotypeRepository(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
