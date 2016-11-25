package lt.dualpair.android.data.task;

import android.content.Context;

import rx.Observable;

public abstract class Task<Result> {

    public abstract Observable<Result> execute(Context context);

}
