package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashSet;
import java.util.Set;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.user.SetUserSociotypesClient;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;

public class SetUserSociotypesTask extends AuthenticatedUserTask<User> {

    private Set<Sociotype> sociotypes;
    private UserRepository userRepository;

    public SetUserSociotypesTask(Context context, Set<Sociotype> sociotypes) {
        super(context);
        this.sociotypes = sociotypes;
        SQLiteDatabase db = DbHelper.forCurrentUser(context).getWritableDatabase();
        userRepository = new UserRepository(db);
    }

    @Override
    protected User run() throws Exception {
        Set<String> codes = new HashSet<>();
        for (Sociotype sociotype : sociotypes) {
            codes.add(sociotype.getCode1());
        }
        new SetUserSociotypesClient(codes, getUserId()).observable().toBlocking().first();
        User user = userRepository.get(AccountUtils.getUserId(context));
        user.setSociotypes(sociotypes);
        userRepository.save(user);
        return user;
    }
}
