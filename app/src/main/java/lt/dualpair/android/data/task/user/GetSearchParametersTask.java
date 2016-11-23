package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.user.GetSearchParametersClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.SearchParametersRepository;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.task.AuthenticatedUserTask;

public class GetSearchParametersTask extends AuthenticatedUserTask<SearchParameters> {

    private SearchParametersRepository searchParametersRepository;

    public GetSearchParametersTask(Context context) {
        super(context);
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        searchParametersRepository = new SearchParametersRepository(db);
    }

    @Override
    protected SearchParameters run() throws Exception {
        SearchParameters sp = searchParametersRepository.getLastUsed();
        if (sp != null) {
            return sp;
        } else {
            sp = new GetSearchParametersClient(getUserId()).observable().toBlocking().first();
            searchParametersRepository.save(sp);
            return sp;
        }
    }
}
