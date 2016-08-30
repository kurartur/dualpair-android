package lt.dualpair.android.core.user;

import android.app.Activity;

import java.util.Date;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.services.user.SetDateOfBirthClient;

public class SetDateOfBirthTask extends AuthenticatedUserTask<Void> {

    private Date date;

    public SetDateOfBirthTask(Activity activity, Date date) {
        super(activity);
        this.date = date;
    }

    @Override
    protected Void run() throws Exception {
        return new SetDateOfBirthClient(getUserId(), date).observable().toBlocking().first();
    }
}