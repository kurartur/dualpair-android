package lt.dualpair.android.data.remote.client.user;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import lt.dualpair.android.data.remote.client.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class SetDateOfBirthClient extends BaseClient<Void> {

    private Long userId;
    private Date date;

    public SetDateOfBirthClient(Long userId, Date date) {
        this.userId = userId;
        this.date = date;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setDateOfBirth(userId, dateToString(date));
    }

    private String dateToString(Date date) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }
}
