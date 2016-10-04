package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.user.SetSearchParametersClient;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.SearchParameters;

public class SetSearchParametersTask extends AuthenticatedUserTask<SearchParameters> {

    private SearchParameters searchParameters;
    private SearchParametersRepository searchParametersRepository;

    public SetSearchParametersTask(Context context, SearchParameters searchParameters) {
        super(context);
        this.searchParameters = searchParameters;
        SQLiteDatabase db = DbHelper.forCurrentUser(context).getWritableDatabase();
        searchParametersRepository = new SearchParametersRepository(db);
    }

    @Override
    protected SearchParameters run() throws Exception {
        new SetSearchParametersClient(getUserId(), searchParameters).observable().toBlocking().first();
        searchParametersRepository.save(searchParameters);
        return searchParameters;
    }
}
