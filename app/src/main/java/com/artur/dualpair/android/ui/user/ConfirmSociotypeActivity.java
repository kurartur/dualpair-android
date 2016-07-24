package com.artur.dualpair.android.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.artur.dualpair.android.R;
import com.artur.dualpair.android.core.user.GetUserPrincipalTask;
import com.artur.dualpair.android.core.user.SetUserSociotypesTask;
import com.artur.dualpair.android.dto.Sociotype;
import com.artur.dualpair.android.dto.User;
import com.artur.dualpair.android.rx.EmptySubscriber;
import com.artur.dualpair.android.utils.ToastUtils;

import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConfirmSociotypeActivity extends Activity {

    private static final String TAG = "ConfirmSocActivity";
    public static final String PARAM_SOCIOTYPE = "sociotype";

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

    private void updateUserSociotypes() {
        new GetUserPrincipalTask(ConfirmSociotypeActivity.this).execute(new EmptySubscriber<User>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to get user", e);
                ToastUtils.show(ConfirmSociotypeActivity.this, e.getMessage());
            }

            @Override
            public void onNext(User user) {
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
                    Set<Sociotype> newSociotypes = new HashSet<>();
                    newSociotypes.add(sociotype);
                    setSociotypes(newSociotypes);
                }
            }
        });
    }

    private void setSociotypes(Set<Sociotype> resultingSociotypes) {
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
