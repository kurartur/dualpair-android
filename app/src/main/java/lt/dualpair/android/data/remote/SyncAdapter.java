package lt.dualpair.android.data.remote;


import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.List;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.remote.task.match.SetResponseTask;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.MatchRepository;
import lt.dualpair.android.data.resource.MatchParty;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private Context context;

    public SyncAdapter(Context ctx, boolean autoInitialize) {
        super(ctx, autoInitialize);
        this.context = ctx;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        SQLiteDatabase db = DbHelper.forCurrentUser(context).getWritableDatabase(); // TODO get for account passed as parameter;
        final MatchRepository matchRepository = new MatchRepository(db);
        List<MatchParty> uMatchParties = matchRepository.getMatchPartiesWithSyncStatus(SyncStatus.UPDATE);
        for (MatchParty matchParty : uMatchParties) {
            final Long partyId = matchParty.getId(); // TODO instead of passing final parameters create subscribers that will hold all needed data
            new SetResponseTask(context, matchParty.getId(), matchParty.getResponse()).execute(new EmptySubscriber<Void>() {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }

                @Override
                public void onNext(Void aVoid) {
                    matchRepository.setMatchPartySyncStatus(partyId, SyncStatus.READY);
                    new MatchDataManager(context).notifySubscribers(matchRepository.findByPartyId(partyId, AccountUtils.getUserId(context)));
                }
            });
        }


    }
}
