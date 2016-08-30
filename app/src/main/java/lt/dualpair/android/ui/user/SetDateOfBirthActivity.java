package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.core.user.SetDateOfBirthTask;
import lt.dualpair.android.rx.EmptySubscriber;
import lt.dualpair.android.utils.ToastUtils;

public class SetDateOfBirthActivity extends Activity {

    private static final String TAG = "SetDateOfBirthActivity";

    @Bind(R.id.button_confirm)
    Button confirmButton;

    @Bind(R.id.date_picker)
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_birthday);
        ButterKnife.bind(this);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetDateOfBirthTask(SetDateOfBirthActivity.this, getDate()).execute(new EmptySubscriber<Void>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to set date of birth", e);
                        ToastUtils.show(SetDateOfBirthActivity.this, e.getMessage());
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }

    private Date getDate() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendaer = Calendar.getInstance();
        calendaer.set(year, month, day);
        return calendaer.getTime();
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, SetDateOfBirthActivity.class);
    }

}
