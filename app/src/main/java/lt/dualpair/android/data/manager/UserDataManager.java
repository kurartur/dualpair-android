package lt.dualpair.android.data.manager;

import android.content.Context;

import java.util.Date;
import java.util.List;
import java.util.Set;

import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.Task;
import lt.dualpair.android.data.task.user.AddPhotoTask;
import lt.dualpair.android.data.task.user.DeletePhotoTask;
import lt.dualpair.android.data.task.user.GetAvailablePhotosTask;
import lt.dualpair.android.data.task.user.GetUserPrincipalTask;
import lt.dualpair.android.data.task.user.SetDateOfBirthTask;
import lt.dualpair.android.data.task.user.SetUserSociotypesTask;
import lt.dualpair.android.data.task.user.UpdateUserTask;
import lt.dualpair.android.ui.accounts.AccountType;
import rx.Observable;

public class UserDataManager extends DataManager {

    public UserDataManager(Context context) {
        super(context);
    }

    public Observable<User> getUser() {
        return getUser(false);
    }

    public Observable<User> getUser(final boolean forceUpdate) {
        return execute(context, new DataRequest<>("getUser", new AuthenticatedTaskCreator<User>() {
            @Override
            protected Task<User> doCreateTask(String authToken) {
                return new GetUserPrincipalTask(authToken, forceUpdate);
            }
        }));
    }

    public Observable<User> setSociotypes(final Set<Sociotype> sociotypes) {
        return execute(context, new DataRequest<>("setSociotypes", new AuthenticatedTaskCreator<User>() {
            @Override
            protected Task<User> doCreateTask(String authToken) {
                return new SetUserSociotypesTask(authToken, sociotypes);
            }
        }));
    }

    public Observable<User> setDateOfBirth(final Date dateOfBirth) {
        return execute(context, new DataRequest<>("setDateOfBirth", new AuthenticatedTaskCreator<User>() {
            @Override
            protected Task<User> doCreateTask(String authToken) {
                return new SetDateOfBirthTask(authToken, dateOfBirth);
            }
        }));
    }

    public Observable<User> deletePhoto(final Photo photo) {
        return execute(context, new DataRequest<>("deletePhoto" + photo, new AuthenticatedTaskCreator<User>() {
            @Override
            protected Task<User> doCreateTask(String authToken) {
                return new DeletePhotoTask(authToken, photo);
            }
        }));
    }

    public Observable<User> updateUser(final User user) {
        return execute(context, new DataRequest<>("updateUser", new AuthenticatedTaskCreator<User>() {
            @Override
            protected Task<User> doCreateTask(String authToken) {
                return new UpdateUserTask(authToken, user);
            }
        }));
    }

    public Observable<User> addPhoto(final Photo photo) {
        return execute(context, new DataRequest<>("addPhoto", new AuthenticatedTaskCreator<User>() {
            @Override
            protected Task<User> doCreateTask(String authToken) {
                return new AddPhotoTask(authToken, photo);
            }
        }));
    }

    public Observable<List<Photo>> getAvailablePhotos(final AccountType accountType) {
        return execute(context, new DataRequest<List<Photo>>("getAvailablePhotos" + accountType.name(), new AuthenticatedTaskCreator<List<Photo>>() {
            @Override
            protected Task<List<Photo>> doCreateTask(String authToken) {
                return new GetAvailablePhotosTask(authToken, accountType);
            }
        }));
    }
}
