package com.artur.dualpair.android.core.user;

import android.app.Activity;

import com.artur.dualpair.android.accounts.AuthenticatedUserTask;
import com.artur.dualpair.android.dto.User;
import com.artur.dualpair.android.services.user.GetUserPrincipal;

public class GetUserPrincipalTask extends AuthenticatedUserTask<User> {

    public GetUserPrincipalTask(Activity activity) {
        super(activity);
    }

    @Override
    protected User run() {
        return new GetUserPrincipal().observable().toBlocking().first();
    }
}
