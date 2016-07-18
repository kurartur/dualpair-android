package com.artur.dualpair.android.core.match;

import android.app.Activity;

import com.artur.dualpair.android.accounts.AuthenticatedUserTask;
import com.artur.dualpair.android.dto.Match;
import com.artur.dualpair.android.services.match.GetNextMatchClient;

public class GetNextMatchTask extends AuthenticatedUserTask<Match> {

    public GetNextMatchTask(Activity activity) {
        super(activity);
    }

    @Override
    protected Match run() throws Exception {
        return new GetNextMatchClient().observable().toBlocking().first();
    }
}
