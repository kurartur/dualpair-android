package lt.dualpair.android.data.manager;

import android.content.Context;
import android.content.Intent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import lt.dualpair.android.data.service.DataService;
import lt.dualpair.android.data.task.Task;
import rx.Subscriber;

public abstract class DataManager {

    protected Context context;

    private static Queue<QueuedTask> tasks = new ConcurrentLinkedQueue<>();

    public DataManager(Context context) {
        this.context = context;
    }


    public void enqueueTask(QueuedTask queuedTask) {
        tasks.add(queuedTask);
        Intent intent = new Intent(context, DataService.class);
        context.startService(intent);
    }

    public static Queue<QueuedTask> getTasks() {
        return tasks;
    }

    public static class QueuedTask<T> {

        private String key;
        private Task task;
        private Subscriber subscriber;

        public QueuedTask(String key, Task<T> task, Subscriber<T> subscriber) {
            this.key = key;
            this.task = task;
            this.subscriber = subscriber;
        }

        public String getKey() {
            return key;
        }

        public Task getTask() {
            return task;
        }

        public Subscriber getSubscriber() {
            return subscriber;
        }
    }
}
