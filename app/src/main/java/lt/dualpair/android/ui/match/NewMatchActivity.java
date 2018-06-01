package lt.dualpair.android.ui.match;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.accounts.AccountType;
import lt.dualpair.android.utils.SocialUtils;

public class NewMatchActivity extends BaseActivity {

    public static final String TAG = "NewMatchActivity";
    public static final String MATCH_ID = "matchId";

    @Bind(R.id.main_picture) protected ImageView mainPicture;
    @Bind(R.id.name) protected TextView name;
    @Bind(R.id.forward) protected ImageView forward;
    @Bind(R.id.facebook_button) protected View facebookButton;
    @Bind(R.id.vkontakte_button) protected View vkontakteButton;

    private Long matchId;

    private NewMatchViewModel viewModel;

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
                startActivity(UserActivity.createIntent(NewMatchActivity.this, matchId));
                finish();
            }
        });

        viewModel = ViewModelProviders.of(this, new NewMatchViewModel.Factory(getApplication(), matchId)).get(NewMatchViewModel.class);
        subscribeUi();
    }

    private void subscribeUi() {
        viewModel.getUser().observe(this, new Observer<UserForView>() {
            @Override
            public void onChanged(@Nullable UserForView userForView) {
                render(userForView);
            }
        });
    }

    @OnClick(R.id.close) void onCloseClick(View v) {
        finish();
    }

    private void render(UserForView user) {
        User opponent = user.getUser();
        name.setText(opponent.getName());
        if (!user.getPhotos().isEmpty()) {
            loadPhoto(user.getPhotos().get(0));
        }

        UserAccount userAccount;
        if ((userAccount = getAccountByType(user.getAccounts(), AccountType.FB)) != null) {
            facebookButton.setVisibility(View.VISIBLE);
            final String accountId = userAccount.getAccountId();
            facebookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SocialUtils.openFacebookUser(NewMatchActivity.this, accountId);
                }
            });
        }

        if ((userAccount = getAccountByType(user.getAccounts(), AccountType.VK)) != null) {
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

    public UserAccount getAccountByType(List<UserAccount> accounts, AccountType accountType) {
        if (accounts != null) {
            for (UserAccount account : accounts) {
                if (account.getAccountType().equals(accountType.name())) {
                    return account;
                }
            }
        }
        return null;
    }


    private void loadPhoto(UserPhoto photo) {
        Picasso.with(this)
                .load(photo.getSourceLink())
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
