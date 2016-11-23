package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.user.SetSearchParametersClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.AuthenticatedUserTask;

public class SetSearchParametersTask extends AuthenticatedUserTask<SearchParameters> {

    private SearchParameters searchParameters;
    private SearchParametersRepository searchParametersRepository;
    private MatchRepository matchRepository;

    public SetSearchParametersTask(Context context, SearchParameters searchParameters) {
        super(context);
        this.searchParameters = searchParameters;
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        searchParametersRepository = new SearchParametersRepository(db);
        matchRepository = new MatchRepository(db);
    }

    @Override
    protected SearchParameters run() throws Exception {
        new SetSearchParametersClient(getUserId(), searchParameters).observable().toBlocking().first();
        searchParametersRepository.save(searchParameters);
        matchRepository.clearNotReviewedMatches(getUserId());
        return searchParameters;
    }
}
