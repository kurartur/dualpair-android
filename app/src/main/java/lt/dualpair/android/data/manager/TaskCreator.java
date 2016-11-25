package lt.dualpair.android.data.manager;

import android.content.Context;

import lt.dualpair.android.data.task.Task;
import rx.Observable;

public interface TaskCreator<T> {

    Observable<Task<T>> createTask(Context context);

}
