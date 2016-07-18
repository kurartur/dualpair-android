package com.artur.dualpair.android.core.user;

import android.app.Activity;

import com.artur.dualpair.android.accounts.AuthenticatedUserTask;
import com.artur.dualpair.android.dto.Sociotype;
import com.artur.dualpair.android.services.user.SetUserSociotypesClient;

import java.util.Set;

public class SetUserSociotypesTask extends AuthenticatedUserTask<Void> {

    private Set<Sociotype> sociotypes;

    public SetUserSociotypesTask(Activity activity, Set<Sociotype> sociotypes) {
        super(activity);
        this.sociotypes = sociotypes;
    }

    @Override
    protected Void run() throws Exception {
        return new SetUserSociotypesClient(sociotypes).observable().toBlocking().first();
    }
}
