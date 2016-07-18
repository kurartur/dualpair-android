package com.artur.dualpair.android.core.user;

import android.app.Activity;

import com.artur.dualpair.android.accounts.AuthenticatedUserTask;
import com.artur.dualpair.android.services.user.SetDateOfBirthClient;

import java.util.Date;

public class SetDateOfBirthTask extends AuthenticatedUserTask<Void> {

    private Date date;

    public SetDateOfBirthTask(Activity activity, Date date) {
        super(activity);
        this.date = date;
    }

    @Override
    protected Void run() throws Exception {
        return new SetDateOfBirthClient(date).observable().toBlocking().first();
    }
}
