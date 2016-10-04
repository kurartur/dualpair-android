package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.client.match.SetResponseClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.MatchParty;
import lt.dualpair.android.data.resource.Response;

public class SetResponseTask extends AuthenticatedUserTask<Match> {

    private Long matchId;
    private Response response;

    private MatchRepository matchRepository;

    public SetResponseTask(Context context, Long matchId, Response response) {
        super(context);
        this.matchId = matchId;
        this.response = response;

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        matchRepository = new MatchRepository(db);
    }

    @Override
    protected Match run() throws Exception {
        Match match = matchRepository.findOne(matchId, getUserId());
        MatchParty matchParty = match.getUser();
        new SetResponseClient(matchParty.getId(), response).observable().toBlocking().first();
        match.getUser().setResponse(response);
        matchRepository.setResponse(matchParty.getId(), response);
        return match;
    }
}
