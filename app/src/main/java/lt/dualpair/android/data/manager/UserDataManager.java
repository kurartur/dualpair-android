package lt.dualpair.android.data.manager;

import android.content.Context;

import java.util.Date;
import java.util.Set;

import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.Task;
import lt.dualpair.android.data.task.user.DeletePhotoTask;
import lt.dualpair.android.data.task.user.GetUserPrincipalTask;
import lt.dualpair.android.data.task.user.SetDateOfBirthTask;
import lt.dualpair.android.data.task.user.SetUserSociotypesTask;
import rx.Observable;

public class UserDataManager extends DataManager {

    public UserDataManager(Context context) {
        super(context);
    }

    public Observable<User> getUser() {
        return execute(context, new DataRequest<>("getUser", new TaskCreator<User>() {
            @Override
            public Task<User> createTask(Context context) {
                return new GetUserPrincipalTask(context);
            }
        }));
    }

    public Observable<User> setSociotypes(final Set<Sociotype> sociotypes) {
        return execute(context, new DataRequest<>("setSociotypes", new TaskCreator<User>() {
            @Override
            public Task<User> createTask(Context context) {
                return new SetUserSociotypesTask(context, sociotypes);
            }
        }));
    }

    public Observable<User> setDateOfBirth(final Date dateOfBirth) {
        return execute(context, new DataRequest<>("setDateOfBirth", new TaskCreator<User>() {
            @Override
            public Task<User> createTask(Context context) {
                return new SetDateOfBirthTask(context, dateOfBirth);
            }
        }));
    }

    public Observable<User> deletePhoto(final Photo photo) {
        return execute(context, new DataRequest<>("deletePhoto" + photo, new TaskCreator<User>() {
            @Override
            public Task<User> createTask(Context context) {
                return new DeletePhotoTask(context, photo);
            }
        }));
    }
}
