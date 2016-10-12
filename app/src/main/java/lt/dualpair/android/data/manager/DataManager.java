package lt.dualpair.android.data.manager;

import android.content.Context;
import android.content.Intent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import lt.dualpair.android.data.service.TaskProcessingService;
import lt.dualpair.android.data.task.Task;
import rx.Observable;
import rx.Subscriber;

public abstract class DataManager {

    protected Context context;

    private static Queue<LinkedRequest> requests = new ConcurrentLinkedQueue<>();

    public DataManager(Context context) {
        this.context = context;
    }

    public static <T> Observable<T> execute(final Context context, final DataRequest<T> dataRequest) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                requests.add(new LinkedRequest<>(dataRequest, subscriber));
                Intent intent = new Intent(context, TaskProcessingService.class);
                context.startService(intent);
            }
        });
    }

    public static Queue<LinkedRequest> getRequests() {
        return requests;
    }

    public interface TaskCreator<T> {
        Task<T> createTask(Context context);
    }

    public static class DataRequest<T> {

        private String key;
        private TaskCreator<T> creator;

        public DataRequest(String key, TaskCreator<T> creator) {
            this.key = key;
            this.creator = creator;
        }

        public String getKey() {
            return key;
        }

        public TaskCreator<T> getCreator() {
            return creator;
        }
    }

    public static class LinkedRequest<T> {

        private DataRequest<T> dataRequest;
        private Subscriber<? super T> link;

        public LinkedRequest(DataRequest<T> dataRequest, Subscriber<? super T> link) {
            this.dataRequest = dataRequest;
            this.link = link;
        }

        public DataRequest<T> getDataRequest() {
            return dataRequest;
        }

        public Subscriber<? super T> getLink() {
            return link;
        }
    }
}
