package lt.dualpair.android.data.task.user;

import android.app.Activity;

import lt.dualpair.android.data.remote.client.user.SetLocationClient;
import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.task.AuthenticatedUserTask;

public class SetLocationTask extends AuthenticatedUserTask<Void> {

    private Location location;

    public SetLocationTask(Activity activity, Location location) {
        super(activity);
        this.location = location;
    }

    @Override
    protected Void run() throws Exception {
        return new SetLocationClient(getUserId(), location).observable().toBlocking().first();
    }
}
