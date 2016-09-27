package lt.dualpair.android.data.remote.task.match;

import android.content.Context;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.services.match.GetNextMatchClient;
import lt.dualpair.android.data.resource.Match;

public class GetNextMatchTask extends AuthenticatedUserTask<Match> {

    private Integer minAge;
    private Integer maxAge;
    private Boolean searchFemale;
    private Boolean searchMale;

    public GetNextMatchTask(Context context, Integer minAge, Integer maxAge, Boolean searchFemale, Boolean searchMale) {
        super(context);
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.searchFemale = searchFemale;
        this.searchMale = searchMale;
    }

    @Override
    protected Match run() throws Exception {
        return new GetNextMatchClient(minAge, maxAge, searchFemale, searchMale).observable().toBlocking().first();
    }
}
