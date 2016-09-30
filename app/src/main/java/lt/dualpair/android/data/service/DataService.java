package lt.dualpair.android.data.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.task.user.GetUserPrincipalTask;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.User;

public class DataService extends IntentService {

    public DataService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver resultReceiver = intent.getParcelableExtra("RECEIVER");
        final UserRepository userRepository = new UserRepository(DbHelper.forCurrentUser(this).getWritableDatabase());
        new GetUserPrincipalTask(this).execute(new EmptySubscriber<User>() {
            @Override
            public void onError(Throwable e) {
                Log.e("UserService", "Unable to get user", e);
            }

            @Override
            public void onNext(User user) {
                userRepository.save(user);
                Bundle bundle = new Bundle();
                bundle.putSerializable("User", user);
                resultReceiver.send(0, bundle);
            }
        });
    }
}
