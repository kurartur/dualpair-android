package lt.dualpair.android.data.manager;

import android.content.Context;
import android.content.Intent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import lt.dualpair.android.data.TaskProcessingService;
import lt.dualpair.android.data.task.Task;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class DataManager {

    protected Context context;

    private static Queue<LinkedTask> requests = new ConcurrentLinkedQueue<>();

    public DataManager(Context context) {
        this.context = context;
    }

    public static <T> Observable<T> execute(final Context context, final DataRequest<T> dataRequest) {
        return dataRequest.getCreator()
                .createTask(context)
                .subscribeOn(Schedulers.io())
                .concatMap(new Func1<Task<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(final Task<T> tTask) {
                        return Observable.create(new Observable.OnSubscribe<T>() {
                            @Override
                            public void call(Subscriber<? super T> subscriber) {
                                requests.add(new LinkedTask<>(tTask, subscriber));
                                Intent intent = new Intent(context, TaskProcessingService.class);
                                context.startService(intent);
                            }
                        });
                    }
                });
    }

    public static Queue<LinkedTask> getRequests() {
        return requests;
    }

    public static class LinkedTask<T> {

        private Task<T> task;
        private Subscriber<? super T> link;

        public LinkedTask(Task<T> task, Subscriber<? super T> link) {
            this.task = task;
            this.link = link;
        }

        public Task<T> getTask() {
            return task;
        }

        public Subscriber<? super T> getLink() {
            return link;
        }
    }
}
