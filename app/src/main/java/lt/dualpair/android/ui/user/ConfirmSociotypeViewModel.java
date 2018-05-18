package lt.dualpair.android.ui.user;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.Set;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

public class ConfirmSociotypeViewModel extends ViewModel {

    private UserDataManager userDataManager;
    private final Sociotype sociotype;
    private final MediatorLiveData<Set<Sociotype>> currentSociotypes;

    public ConfirmSociotypeViewModel(UserDataManager userDataManager, Sociotype sociotype) {
        this.userDataManager = userDataManager;
        this.sociotype = sociotype;
        currentSociotypes = new MediatorLiveData<>();
        currentSociotypes.setValue(null);
        userDataManager.getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onNext(User user) {
                        unsubscribe();
                        currentSociotypes.setValue(user.getSociotypes());
                    }
                });
    }

    public LiveData<Set<Sociotype>> getCurrentSociotypes() {
        return currentSociotypes;
    }

    public Observable saveSociotypes(Set<Sociotype> sociotypes) {
        final Subject subject = BehaviorSubject.create();
        userDataManager.setSociotypes(sociotypes)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onNext(User u) {
                        subject.onCompleted();
                    }
                });
        return subject;
    }

    public Sociotype getSociotype() {
        return sociotype;
    }
}
