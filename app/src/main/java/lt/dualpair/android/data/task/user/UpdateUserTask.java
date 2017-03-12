package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.Set;

import lt.dualpair.android.data.remote.client.user.UpdateUserClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.PurposeOfBeing;
import lt.dualpair.android.data.resource.RelationshipStatus;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;


public class UpdateUserTask extends AuthenticatedUserTask<User> {

    private User user;

    private String name;
    private Date dateOfBirth;
    private String description;
    private RelationshipStatus relationshipStatus;
    private Set<PurposeOfBeing> purposesOfBeing;

    public UpdateUserTask(String authToken, User user) {
        super(authToken);
        this.user = user;
    }

    public UpdateUserTask(String token,
                          String name,
                          Date dateOfBirth,
                          String description,
                          RelationshipStatus relationshipStatus,
                          Set<PurposeOfBeing> purposesOfBeing) {
        super(token);
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.description = description;
        this.relationshipStatus = relationshipStatus;
        this.purposesOfBeing = purposesOfBeing;
    }

    @Override
    protected Observable<User> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                UserRepository userRepository = new UserRepository(db);
                if (user != null) {
                } else {
                    user = userRepository.get(getUserId(context));
                    user.setName(name);
                    user.setDateOfBirth(dateOfBirth);
                    user.setDescription(description);
                    user.setRelationshipStatus(relationshipStatus);
                    user.setPurposesOfBeing(purposesOfBeing);
                }
                new UpdateUserClient(user).observable().toBlocking().first();
                userRepository.save(user);
                subscriber.onNext(user);
                subscriber.onCompleted();
            }
        });
    }
}
