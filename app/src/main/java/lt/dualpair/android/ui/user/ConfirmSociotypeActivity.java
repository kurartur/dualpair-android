package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.task.user.SetUserSociotypesTask;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.user.UserProvider;
import lt.dualpair.android.utils.ToastUtils;
import rx.Subscription;

public class ConfirmSociotypeActivity extends Activity {

    private static final String TAG = "ConfirmSocActivity";
    public static final String PARAM_SOCIOTYPE = "sociotype";

    private Subscription userSubscription;

    @Bind(R.id.header)
    TextView header;

    @Bind(R.id.button_confirm)
    Button confirmButton;

    private Sociotype sociotype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_confirm_sociotype);
        ButterKnife.bind(this);

        loadSociotype((Sociotype)getIntent().getSerializableExtra(PARAM_SOCIOTYPE));

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserSociotypes();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        userSubscription.unsubscribe();
    }

    private void updateUserSociotypes() {
        userSubscription = new UserProvider(this).getUser(new EmptySubscriber<User>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to get user", e);
                ToastUtils.show(ConfirmSociotypeActivity.this, e.getMessage());
            }

            @Override
            public void onNext(User user) {
                userSubscription.unsubscribe();
                Set<Sociotype> currentSociotypes = user.getSociotypes();
                if (currentSociotypes.size() > 1) {
                    if (currentSociotypes.contains(sociotype)) {
                        // TODO you already have this sociotype, leave only this one?
                    } else {
                        // TODO leave only this one?
                    }
                } else if (currentSociotypes.size() == 1 && currentSociotypes.contains(sociotype)) {
                    // TODO already have this
                } else {
                    Set<String> newSociotypes = new HashSet<>();
                    newSociotypes.add(sociotype.getCode1());
                    setSociotypes(newSociotypes);
                }
            }
        });
    }

    private void setSociotypes(Set<String> resultingSociotypes) {
        new SetUserSociotypesTask(this, resultingSociotypes).execute(new EmptySubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to set sociotypes", e);
                ToastUtils.show(ConfirmSociotypeActivity.this, e.getMessage());
            }

            @Override
            public void onNext(Void v) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void loadSociotype(Sociotype sociotype) {
        this.sociotype = sociotype;
        header.setText(sociotype.getCode1());
    }

    public static Intent createIntent(Activity activity, Sociotype sociotype) {
        Intent intent = new Intent(activity, ConfirmSociotypeActivity.class);
        intent.putExtra(PARAM_SOCIOTYPE, sociotype);
        return intent;
    }
}
