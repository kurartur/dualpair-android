package lt.dualpair.android.data.manager;

import android.content.Context;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lt.dualpair.android.data.resource.Choice;
import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.PurposeOfBeing;
import lt.dualpair.android.data.resource.RelationshipStatus;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.LogoutTask;
import lt.dualpair.android.data.task.socionics.EvaluateTestTask;
import lt.dualpair.android.data.task.user.GetAvailablePhotosTask;
import lt.dualpair.android.data.task.user.GetSearchParametersTask;
import lt.dualpair.android.data.task.user.GetUserPrincipalTask;
import lt.dualpair.android.data.task.user.ReportUserTask;
import lt.dualpair.android.data.task.user.SavePhotosTask;
import lt.dualpair.android.data.task.user.SetDateOfBirthTask;
import lt.dualpair.android.data.task.user.SetLocationTask;
import lt.dualpair.android.data.task.user.SetSearchParametersTask;
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
        return new GetUserPrincipalTask(forceUpdate).execute(context);
    }

    public Observable<User> setSociotypes(final Set<Sociotype> sociotypes) {
        return new SetUserSociotypesTask(sociotypes).execute(context);
    }

    public Observable<User> setDateOfBirth(final Date dateOfBirth) {
        return new SetDateOfBirthTask(dateOfBirth).execute(context);
    }

    public Observable<Void> setLocation(final Location location) {
        return new SetLocationTask(location).execute(context);
    }

    public Observable<User> updateUser(final String name,
                                       final Date dateOfBirth,
                                       final String description,
                                       final RelationshipStatus relationshipStatus,
                                       final Set<PurposeOfBeing> purposesOfBeing) {
        return new UpdateUserTask(name, dateOfBirth, description, relationshipStatus, purposesOfBeing).execute(context);
    }

    public Observable<List<Photo>> getAvailablePhotos(final AccountType accountType) {
        return new GetAvailablePhotosTask(accountType).execute(context);
    }

    public Observable<Void> logout() {
        return new LogoutTask().execute(context);
    }

    public Observable<Sociotype> evaluateTest(final Map<String, Choice> choices) {
        return new EvaluateTestTask(choices).execute(context);
    }

    public Observable<List<Photo>> savePhotos(final List<Photo> photos) {
        return new SavePhotosTask(photos).execute(context);
    }

    public Observable<Void> reportUser(final Long userId) {
        return new ReportUserTask(userId).execute(context);
    }

    public Observable<SearchParameters> getSearchParameters() {
        return new GetSearchParametersTask().execute(context);
    }

    public Observable<SearchParameters> setSearchParameters(final SearchParameters sp) {
        return new SetSearchParametersTask(sp).execute(context);
    }
}
