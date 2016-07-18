package com.artur.dualpair.android.core.user;

import android.app.Activity;

import com.artur.dualpair.android.accounts.AuthenticatedUserTask;
import com.artur.dualpair.android.dto.Location;

public class SetLocationTask extends AuthenticatedUserTask<Void> {

    private Location location;

    public SetLocationTask(Activity activity, Location location) {
        super(activity);
        this.location = location;
    }

    @Override
    protected Void run() throws Exception {
        return null;
    }
}
