package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.remote.client.user.SetDateOfBirthClient;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.BaseActivity;

public class SetDateOfBirthActivity extends BaseActivity {

    private static final String TAG = SetDateOfBirthActivity.class.getName();

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
                new SetDateOfBirthClient(AccountUtils.getUserId(SetDateOfBirthActivity.this), getDate()).completable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .compose(SetDateOfBirthActivity.this.<User>bindToLifecycle())
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                setResult(Activity.RESULT_OK);
                                finish();
                            }
                        });
            }
        });
    }

    private int getAge(int _year, int _month, int _day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 0)
            throw new IllegalArgumentException("Age < 0");
        return a;
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
