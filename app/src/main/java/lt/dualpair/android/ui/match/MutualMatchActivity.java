package lt.dualpair.android.ui.match;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.bus.RxBus;
import lt.dualpair.android.bus.UnmatchEvent;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Response;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.resource.UserAccount;
import lt.dualpair.android.ui.user.SocialButtonsRecyclerAdapter;
import lt.dualpair.android.utils.SocialUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MutualMatchActivity extends MatchActivity {

    private static final int UNMATCH_MENU_ITEM = 2;

    private static final int UNMATCH_RESULT_CODE = 10;

    private SocialButtonsViewHolder socialButtonsViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socialButtonsViewHolder = new SocialButtonsViewHolder(opponentUserView.setPhotoOverlay(R.layout.match_social_buttons));
    }

    @Override
    protected void renderMatch(Match match) {
        super.renderMatch(match);
        User opponentUser = match.getOpponent().getUser();
        socialButtonsViewHolder.buttons.setAdapter(new SocialButtonsRecyclerAdapter(opponentUser.getAccounts(), new SocialButtonsRecyclerAdapter.OnButtonClick() {
            @Override
            public void onClick(UserAccount userAccount) {
                SocialUtils.openUserAccount(MutualMatchActivity.this, userAccount);
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, UNMATCH_MENU_ITEM, Menu.NONE, R.string.unmatch);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) return true;
        switch (item.getItemId()) {
            case UNMATCH_MENU_ITEM:
                if (match != null) {
                    unmatch(match);
                }
                return true;
        }
        return false;
    }

    private void unmatch(Match match) {
        new MatchDataManager(this).setResponse(match.getId(), Response.NO)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new EmptySubscriber<Match>() {
                @Override
                public void onNext(Match match) {
                    RxBus.getInstance().post(new UnmatchEvent());
                }
            });
        setResult(UNMATCH_RESULT_CODE);
        finish();
    }

    protected static class SocialButtonsViewHolder {

        @Bind(R.id.buttons)
        RecyclerView buttons;

        public SocialButtonsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static Intent createIntent(Context ctx, Long matchId) {
        Intent intent = new Intent(ctx, MutualMatchActivity.class);
        intent.putExtra(MATCH_ID, matchId);
        return intent;
    }
}
