package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.client.user.UpdateUserClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.User;


public class UpdateUserTask extends AuthenticatedUserTask<User> {

    private User user;
    private UserRepository userRepository;

    public UpdateUserTask(Context context, User user) {
        super(context);
        this.user = user;
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        userRepository = new UserRepository(db);
    }

    @Override
    protected User run() throws Exception {
        new UpdateUserClient(user).observable().toBlocking().first();
        userRepository.save(user);
        return user;
    }
}
