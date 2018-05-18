package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.remote.client.user.UpdateUserClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class SetDateOfBirthTask extends AuthenticatedUserTask<User> {

    private Date date;

    public SetDateOfBirthTask(String authToken, Date date) {
        super(authToken);
        this.date = date;
    }

    @Override
    protected Observable<User> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                UserRepository userRepository = new UserRepository(db);
                User user = userRepository.get(AccountUtils.getUserId(context));
                user.setDateOfBirth(date);
                new UpdateUserClient(user).observable().toBlocking().first();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                user.setAge(getAge(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));

                userRepository.save(user);
                subscriber.onNext(user);
                subscriber.onCompleted();
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
}
