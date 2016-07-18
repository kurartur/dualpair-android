package com.artur.dualpair.android.services.user;

import com.artur.dualpair.android.services.BaseClient;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Retrofit;
import rx.Observable;

public class SetDateOfBirthClient extends BaseClient<Void> {

    private Date date;

    public SetDateOfBirthClient(Date date) {
        this.date = date;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setDateOfBirth(dateToString(date));
    }

    private String dateToString(Date date) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }
}
