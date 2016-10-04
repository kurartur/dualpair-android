package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.client.user.SetDateOfBirthClient;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.User;

public class SetDateOfBirthTask extends AuthenticatedUserTask<User> {

    private Date date;
    private UserRepository userRepository;

    public SetDateOfBirthTask(Context context, Date date) {
        super(context);
        this.date = date;
        SQLiteDatabase db = DbHelper.forCurrentUser(context).getWritableDatabase();
        userRepository = new UserRepository(db);
    }

    @Override
    protected User run() throws Exception {
        new SetDateOfBirthClient(getUserId(), date).observable().toBlocking().first();
        User user = userRepository.get(AccountUtils.getUserId(context));
        user.setDateOfBirth(date); // TODO calculate age;
        userRepository.save(user);
        return user;
    }
}
