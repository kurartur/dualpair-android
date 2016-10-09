package lt.dualpair.android.ui.match;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidException;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.MatchDataManager;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.ui.BaseActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewMatchActivity extends BaseActivity {

    public static final String TAG = "NewMatchActivity";
    public static final String MATCH_ID = "matchId";

    @Bind(R.id.main_picture) protected ImageView mainPicture;
    @Bind(R.id.name) protected TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_match_layout);

        if (getActionBar() != null) {
            getActionBar().hide();
        }

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Long matchId = getIntent().getLongExtra(MATCH_ID, -1);
        if (matchId == -1) {
            Log.w(TAG, "empty match id");
            finish();
        } else {
            init(matchId);
        }
    }

    private void init(final Long matchId) {
        new MatchDataManager(this).match(matchId)
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
        name.setText(match.getOpponent().getUser().getName());
        if (!match.getOpponent().getUser().getPhotos().isEmpty()) {
            loadPhoto(match.getOpponent().getUser().getPhotos().get(0));
        }

    }

    private void loadPhoto(Photo photo) {
        Picasso.with(this)
                .load(photo.getSourceUrl())
                .resize(mainPicture.getWidth(), mainPicture.getHeight())
                .error(R.drawable.image_not_found)
                .centerCrop()
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
