package lt.dualpair.android.data.manager;

import android.content.Context;

import java.util.Date;
import java.util.Set;

import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.Task;
import lt.dualpair.android.data.task.user.GetUserPrincipalTask;
import lt.dualpair.android.data.task.user.SetDateOfBirthTask;
import lt.dualpair.android.data.task.user.SetUserSociotypesTask;
import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class UserDataManager extends DataManager {

    private static Subject<User, User> userSubject = PublishSubject.create();

    public UserDataManager(Context context) {
        super(context);
    }

    public Observable<User> getUser() {
        PublishSubject<User> subject = PublishSubject.create();
        userSubject.subscribe(subject);
        enqueueTask(new QueuedTask<>("getUser", new TaskCreator<User>() {
            @Override
            public Task<User> createTask(Context context) {
                return new GetUserPrincipalTask(context);
            }
        }, subject));
        return subject.asObservable();
    }

    public Observable<User> setSociotypes(final Set<Sociotype> sociotypes) {
        final PublishSubject<User> subject = PublishSubject.create();
        subject.doOnNext(new Action1<User>() {
            @Override
            public void call(User user) {
                userSubject.onNext(user);
            }
        });
        enqueueTask(new QueuedTask<>("setSociotypes", new TaskCreator<User>() {
            @Override
            public Task<User> createTask(Context context) {
                return new SetUserSociotypesTask(context, sociotypes);
            }
        }, subject));
        return subject.asObservable();
    }

    public Observable<User> setDateOfBirth(final Date dateOfBirth) {
        PublishSubject<User> subject = PublishSubject.create();
        userSubject.subscribe(subject);
        enqueueTask(new QueuedTask("setDateOfBirth", new TaskCreator<User>() {
            @Override
            public Task<User> createTask(Context context) {
                return new SetDateOfBirthTask(context, dateOfBirth);
            }
        }, subject));
        return subject.asObservable();
    }
}
