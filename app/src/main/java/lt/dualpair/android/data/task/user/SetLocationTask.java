package lt.dualpair.android.data.task.user;

import android.content.Context;

import lt.dualpair.android.data.remote.client.user.SetLocationClient;
import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;

public class SetLocationTask extends AuthenticatedUserTask<Void> {

    private Location location;

    public SetLocationTask(String authToken, Location location) {
        super(authToken);
        this.location = location;
    }

    @Override
    protected Observable<Void> run(Context context) {
        return new SetLocationClient(getUserId(context), location).observable();
    }

}
