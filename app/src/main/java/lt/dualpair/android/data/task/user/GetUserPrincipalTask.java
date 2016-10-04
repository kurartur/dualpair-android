package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.user.GetUserPrincipalClient;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.User;

public class GetUserPrincipalTask extends AuthenticatedUserTask<User> {

    private UserRepository userRepository;

    public GetUserPrincipalTask(Context context) {
        super(context);
        SQLiteDatabase db = DbHelper.forCurrentUser(context).getWritableDatabase();
        userRepository = new UserRepository(db);
    }

    @Override
    protected User run() {
        if (true) { // TODO cache expired
            User user = new GetUserPrincipalClient().observable().toBlocking().first();
            userRepository.save(user);
            return user;
        } else {
            return userRepository.get(AccountUtils.getUserId(context));
        }
    }
}
