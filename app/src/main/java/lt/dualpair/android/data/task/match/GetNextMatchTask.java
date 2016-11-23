package lt.dualpair.android.data.task.match;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import lt.dualpair.android.data.remote.client.match.GetNextMatchClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.task.AuthenticatedUserTask;

public class GetNextMatchTask extends AuthenticatedUserTask<Match> {

    private Integer minAge;
    private Integer maxAge;
    private Boolean searchFemale;
    private Boolean searchMale;

    private MatchRepository matchRepository;
    private UserRepository userRepository;

    public GetNextMatchTask(Context context, Integer minAge, Integer maxAge, Boolean searchFemale, Boolean searchMale) {
        super(context);
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.searchFemale = searchFemale;
        this.searchMale = searchMale;

        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        matchRepository = new MatchRepository(db);
        userRepository = new UserRepository(db);
    }

    @Override
    protected Match run() throws Exception {
        List<Match> matchList = matchRepository.next(getUserId());
        if (!matchList.isEmpty()) {
            return matchList.get(0);
        } else {
            Match match = new GetNextMatchClient(minAge, maxAge, searchFemale, searchMale).observable().toBlocking().first();
            match.getUser().setUser(userRepository.get(getUserId()));
            matchRepository.save(match);
            return match;
        }
    }
}
