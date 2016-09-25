package lt.dualpair.android.core.match;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.resource.Response;
import lt.dualpair.android.services.match.SetResponseClient;

public class SetResponseTask extends AuthenticatedUserTask<Void> {

    private Long matchPartyId;
    private Response response;

    public SetResponseTask(Context context, Long matchPartyId, Response response) {
        super(context);
        this.matchPartyId = matchPartyId;
        this.response = response;
    }

    @Override
    protected Void run() throws Exception {
        return new SetResponseClient(matchPartyId, response).observable().toBlocking().first();
    }
}
