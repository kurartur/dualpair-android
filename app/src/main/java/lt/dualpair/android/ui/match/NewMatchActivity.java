package lt.dualpair.android.ui.match;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.resource.UserAccount;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.accounts.AccountType;
import lt.dualpair.android.utils.SocialUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewMatchActivity extends BaseActivity {

    public static final String TAG = "NewMatchActivity";
    public static final String MATCH_ID = "matchId";

    @Bind(R.id.main_picture) protected ImageView mainPicture;
    @Bind(R.id.name) protected TextView name;
    @Bind(R.id.forward) protected ImageView forward;
    @Bind(R.id.facebook_button) protected View facebookButton;
    @Bind(R.id.vkontakte_button) protected View vkontakteButton;

    private Long matchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_match_layout);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ButterKnife.bind(this);

        matchId = getIntent().getLongExtra(MATCH_ID, -1);

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MatchActivity.createIntent(NewMatchActivity.this, matchId));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (matchId == -1) {
            Log.w(TAG, "empty match id");
            finish();
        } else {
            init(matchId);
        }
    }

    @OnClick(R.id.close) void onCloseClick(View v) {
        finish();
    }

    private void init(final Long matchId) {
        new MatchDataManager(this).match(matchId, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(this.<Match>bindToLifecycle())
                .subscribe(new EmptySubscriber<Match>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to load match " + matchId, e);
                        finish();
                    }

                    @Override
                    public void onNext(Match match) {
                        render(match);
                    }
                });
    }

    private void render(Match match) {
        User opponent = match.getOpponent().getUser();
        name.setText(opponent.getName());
        if (!opponent.getPhotos().isEmpty()) {
            loadPhoto(opponent.getPhotos().get(0));
        }

        UserAccount userAccount;
        if ((userAccount = opponent.getAccountByType(AccountType.FB)) != null) {
            facebookButton.setVisibility(View.VISIBLE);
            final String accountId = userAccount.getAccountId();
            facebookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialUtils.openFacebookUser(NewMatchActivity.this, accountId);
                }
            });
        }

        if ((userAccount = opponent.getAccountByType(AccountType.VK)) != null) {
            vkontakteButton.setVisibility(View.VISIBLE);
            final String accountId = userAccount.getAccountId();
            vkontakteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialUtils.openVKontakteUser(NewMatchActivity.this, accountId);
                }
            });
        }

    }

    private void loadPhoto(Photo photo) {
        Picasso.with(this)
                .load(photo.getSourceUrl())
                .error(R.drawable.image_not_found)
                .into(mainPicture, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Log.e("NewMatchActivity", "Error while loading photo");
                    }
                });
    }

    public static Intent createIntent(Activity activity, Long matchId) {
        Intent intent = new Intent(activity, NewMatchActivity.class);
        intent.putExtra(MATCH_ID, matchId);
        return intent;
    }

}
