package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.client.match.GetMutualMatchClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Match;

public class GetMutualMatchTask extends AuthenticatedUserTask<Match> {

    private Long matchId;

    private MatchRepository matchRepository;
    private UserRepository userRepository;

    public GetMutualMatchTask(Context context, Long matchId) {
        super(context);
        this.matchId = matchId;

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        matchRepository = new MatchRepository(db);
        userRepository = new UserRepository(db);
    }

    @Override
    protected Match run() throws Exception {
        Match match = matchRepository.findOne(matchId, getUserId());
        if (match != null) {
            return match;
        } else {
            match = new GetMutualMatchClient(getUserId(), matchId).observable().toBlocking().first();
            match.getUser().setUser(userRepository.get(getUserId()));
            matchRepository.save(match);
            return match;
        }
    }
}
