package lt.dualpair.android.ui.user;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.repository.SociotypeRepository;

public class AddSociotypeViewModel extends ViewModel {

    private final LiveData<List<Sociotype>> sociotypes;

    public AddSociotypeViewModel(SociotypeRepository sociotypeRepository) {
        sociotypes = sociotypeRepository.getSociotypes();
    }

    public LiveData<List<Sociotype>> getSociotypes() {
        return sociotypes;
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
            if (modelClass.isAssignableFrom(AddSociotypeViewModel.class)) {
                return (T) new AddSociotypeViewModel(new SociotypeRepository(application));
            }
            throw new IllegalArgumentException("Illegal class");
        }
    }

}
